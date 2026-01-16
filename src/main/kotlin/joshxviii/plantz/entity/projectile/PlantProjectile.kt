package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.InsideBlockEffectApplier
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.*
import kotlin.math.sign

abstract class PlantProjectile(
    type: EntityType<out PlantProjectile>,
    level: Level,
    val plantOwner: Plant? = null,
    val spawnOffset: Vec2 = Vec2.ZERO,
    val damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT,
) : Projectile(type, level) {

    val damage : Float
    val knockback : Double
    protected var inGroundTime: Int = 0
    private var piercingIgnoreEntityIds = mutableSetOf<Int>()

    companion object {
        val PIERCE_LEVEL: EntityDataAccessor<Byte> = SynchedEntityData.defineId<Byte>(PlantProjectile::class.java, EntityDataSerializers.BYTE)
        val IN_GROUND: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(PlantProjectile::class.java, EntityDataSerializers.BOOLEAN)
    }

    init {
        if (plantOwner != null) {
            this.setOwner(plantOwner)
            val direction = plantOwner.headLookAngle
            this.setPos(
                plantOwner.x+direction.x*spawnOffset.x,
                plantOwner.y+plantOwner.eyeHeight+spawnOffset.y,
                plantOwner.z+direction.z*spawnOffset.x
            )
            this.setRot(
                plantOwner.xRot,
                plantOwner.yRot
            )
        }

        damage = (getOwner() as? LivingEntity)?.attributes?.getValue(Attributes.ATTACK_DAMAGE)?.toFloat()?:1.0f
        knockback = (getOwner() as? LivingEntity)?.attributes?.getValue(Attributes.ATTACK_KNOCKBACK)?:0.0
    }

    override fun tick() {
        val blockPos: BlockPos = this.blockPosition()
        val blockState: BlockState = this.level().getBlockState(blockPos)
        if (!blockState.isAir) {
            val shape = blockState.getCollisionShape(this.level(), blockPos)
            if (!shape.isEmpty) {
                val position = this.position()
                for (aabb in shape.toAabbs()) {
                    if (aabb.move(blockPos).contains(position)) {
                        this.deltaMovement = Vec3.ZERO
                        this.setInGround(true)
                        break
                    }
                }
            }
        }

        if (this.isInGround()) {
            inGroundTime++
        }
        else {
            inGroundTime=0
            this.updateRotation()
        }
        if (inGroundTime>stickInGroundTime()) discard()

        this.handleFirstTickBubbleColumn()

        val result =
            ProjectileUtil.getHitResultOnMoveVector(this) { entity: Entity? -> this.canHitEntity(entity!!) }
        val newPosition =
            if (result.type != HitResult.Type.MISS) result.getLocation()
            else this.position().add(this.deltaMovement)
        if (result.type != HitResult.Type.MISS && this.isAlive) {
            this.hitTargetOrDeflectSelf(result)
        }
        this.setPos(newPosition)
        this.applyEffectsFromBlocks()

        applyInertia()
        if(!this.isInGround()) {
            this.applyGravity()
        }

        super.tick()
    }

    override fun onHitEntity(hitResult: EntityHitResult) {
        super.onHitEntity(hitResult)
        val target = hitResult.entity
        val serverLevel = this.level() as? ServerLevel
        if (serverLevel != null) {
            val owner = this.getOwner() as? LivingEntity
            owner?.setLastHurtMob(target)

            // get damage from attribute
            val source = this.damageSources().source(damageType, this, owner)
            if(target.hurtServer(serverLevel, source, damage)) {
                if (target is LivingEntity) {
                    val knockbackDirection = calculateHorizontalHurtKnockbackDirection(target, source)
                    target.knockback(knockback, -knockbackDirection.leftDouble(), -knockbackDirection.rightDouble())
                    afterHitEntityEffect(target)
                }
            }
        }
        if (getPierceLevel() > 0) {
            if (piercingIgnoreEntityIds.size >= getPierceLevel()+1) {
                this.discard()
                return
            }
            piercingIgnoreEntityIds.add(target.id)
        }
        this.playSound(getHitSound(), 0.4f, 1.8f)
        if (this.getPierceLevel() <= 0) this.discard()
    }

    override fun onHitBlock(hitResult: BlockHitResult) {
        super.onHitBlock(hitResult)
        val movement = this.deltaMovement
        val offsetDirection = Vec3(sign(movement.x), sign(movement.y), sign(movement.z))
        val scaledMovement = offsetDirection.scale(0.05)
        this.setPos(this.position().subtract(scaledMovement))
        this.deltaMovement = Vec3.ZERO
        this.setPierceLevel(0)
        this.playSound(getHitSound(), 0.4f, 1.8f)
        afterHitBlockEffect(hitResult.blockPos)
    }

    open fun afterHitEntityEffect(target: LivingEntity) {}
    open fun afterHitBlockEffect(target: BlockPos) {}

    override fun getDefaultGravity(): Double { return 0.03 }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        entityData.define(IN_GROUND, false)
        entityData.define(PIERCE_LEVEL, 0.toByte())
    }

    fun setInGround(inGround: Boolean) = this.entityData.set(IN_GROUND, inGround)
    fun isInGround(): Boolean = this.entityData.get(IN_GROUND)

    private fun setPierceLevel(pieceLevel: Byte) = this.entityData.set(PIERCE_LEVEL, pieceLevel)
    open fun getPierceLevel(): Byte = this.entityData.get(PIERCE_LEVEL)

    open fun getHitSound(): SoundEvent = SoundEvents.HONEY_BLOCK_PLACE// TODO make custom sounds
    open fun stickInGroundTime(): Int = 0

    private fun shouldFall(): Boolean {
        return this.isInGround() && this.level().noCollision(AABB(this.position(), this.position()).inflate(0.06))
    }

    private fun startFalling() {
        this.setInGround(false)
        val deltaMovement = this.deltaMovement
        this.deltaMovement = deltaMovement.multiply(
            (this.random.nextFloat() * 0.2f).toDouble(),
            (this.random.nextFloat() * 0.2f).toDouble(),
            (this.random.nextFloat() * 0.2f).toDouble()
        )
    }

    private fun applyInertia() {
        val m = this.deltaMovement
        val p = this.position()
        val inertia: Float
        if (this.isInWater) {
            repeat(3) {
                val s = 0.25f
                this.level().addParticle(ParticleTypes.BUBBLE, p.x - m.x * s, p.y - m.y * s, p.z - m.z * s, m.x, m.y, m.z)
            }
            inertia = 0.8f
        } else inertia = 1.00f// No air drag so projectile calculations can be perfectly accurate

        this.deltaMovement = m.scale(inertia.toDouble())
    }

    private fun handleFirstTickBubbleColumn() {
        if (this.firstTick) {
            for (pos in BlockPos.betweenClosed(this.boundingBox)) {
                val state = this.level().getBlockState(pos)
                if (state.`is`(Blocks.BUBBLE_COLUMN)) state.entityInside(this.level(), pos, this, InsideBlockEffectApplier.NOOP, true)
            }
        }
    }

    // center hit box
    override fun makeBoundingBox(p: Vec3): AABB {
        val width = this.type.dimensions.width() / 2.0f
        val height = this.type.dimensions.height() / 2.0f
        return AABB(
            p.x - width, p.y - height, p.z - width,
            p.x + width, p.y + height, p.z + width
        )
    }

    override fun canHitEntity(entity: Entity): Boolean {
        val playerOwner = plantOwner?.owner as? Player
        return if (playerOwner!= null && entity.`is`(playerOwner)) false
        else entity !is Plant && entity !is Projectile && !this.piercingIgnoreEntityIds.contains(entity.id)
    }

    fun spawnParticle(
        particle : ParticleOptions = ParticleTypes.POOF,
        amount : Int = 6,
        spread: Vec3 = Vec3(0.3, 0.3, 0.3),
        offset: Vec3 = Vec3.ZERO,
        speed: Double = 0.4
    ) {
        (this.level() as? ServerLevel)?.sendParticles(
            particle,
            this.x+offset.x, this.y+offset.y, this.z+offset.z,
            amount,
            spread.x, spread.y, spread.z,
            speed
        )
    }
}


