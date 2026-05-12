package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.hasSameRootOwner
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ProjectileDeflection
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.*
import java.util.*
import java.util.function.Predicate
import java.util.function.ToDoubleFunction
import kotlin.math.sign

abstract class PazProjectile(
    type: EntityType<out PazProjectile>,
    level: Level,
    val entityOwner: LivingEntity? = null,
    val spawnOffset: Vec2 = Vec2.ZERO,
    val damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT,
    var damage : Float = entityOwner?.attributes?.getValue(Attributes.ATTACK_DAMAGE)?.toFloat()?:1.0f,
    var knockback : Double = entityOwner?.attributes?.getValue(Attributes.ATTACK_KNOCKBACK)?:0.0
) : Projectile(type, level) {

    protected var inGroundTime: Int = 0
    private var piercingIgnoreEntityIds = mutableSetOf<Int>()

    companion object {
        val PIERCE_LEVEL: EntityDataAccessor<Byte> = SynchedEntityData.defineId(PazProjectile::class.java, EntityDataSerializers.BYTE)
        val IN_GROUND: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(PazProjectile::class.java, EntityDataSerializers.BOOLEAN)
    }

    init {
        if (entityOwner != null) {
            setOwner(entityOwner)
            val direction = entityOwner.headLookAngle
            setPos(
                entityOwner.x+direction.x*spawnOffset.x,
                entityOwner.y+entityOwner.eyeHeight+spawnOffset.y,
                entityOwner.z+direction.z*spawnOffset.x
            )
            setRot(
                entityOwner.xRot,
                entityOwner.yRot
            )
        }
    }

    override fun tick() {
        val level = level()
        val blockPos: BlockPos = this.blockPosition()
        val blockState: BlockState = level.getBlockState(blockPos)
        val movement = deltaMovement
        val physicsEnabled: Boolean = !this.noPhysics
        if (!blockState.isAir) {
            val shape = blockState.getCollisionShape(level, blockPos)
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

        val originalPosition = this.position()
        if (physicsEnabled) {
            val blockHitResult = this.level()
                .clipIncludingBorder(
                    ClipContext(
                        originalPosition,
                        originalPosition.add(movement),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        this
                    )
                )
            this.stepMoveAndHit(blockHitResult)
        } else {
            this.setPos(originalPosition.add(movement))
            this.applyEffectsFromBlocks()
        }

        applyInertia()
        if(!this.isInGround()) this.applyGravity()

        super.tick()
    }

    private fun stepMoveAndHit(blockHitResult: BlockHitResult) {
        while (this.isAlive) {
            val initialPosition = this.position()
            val entitiesHit = findHitEntities(initialPosition, blockHitResult.getLocation())
                .toList()
                .sortedBy { initialPosition.distanceToSqr(it.entity.position()) }
            val firstEntityHit = entitiesHit.firstOrNull()
            val nextLocation = (firstEntityHit ?: blockHitResult).location
            this.setPos(nextLocation)
            this.applyEffectsFromBlocks(initialPosition, nextLocation)
            if (portalProcess != null && portalProcess!!.isInsidePortalThisTick) handlePortal()

            if (entitiesHit.isEmpty()) {
                if (this.isAlive && blockHitResult.type != HitResult.Type.MISS) {
                    this.hitTargetOrDeflectSelf(blockHitResult)
                    this.needsSync = true
                }
                break
            } else if (this.isAlive && !this.noPhysics) {
                val deflection: ProjectileDeflection = if (isAlive) entitiesHit
                    .map { hitTargetOrDeflectSelf(it) }
                    .find { it != ProjectileDeflection.NONE } ?: ProjectileDeflection.NONE
                    else ProjectileDeflection.NONE
                this.needsSync = true
                if (this.getPierceLevel() > 0 && deflection === ProjectileDeflection.NONE) {
                    continue
                }
                break
            }
        }
    }

    protected open fun findHitEntities(from: Vec3, to: Vec3): MutableCollection<EntityHitResult> {
        return ProjectileUtil.getManyEntityHitResult(
            this.level(), this, from, to, this.boundingBox.expandTowards(this.deltaMovement).inflate(1.0), { entity: Entity -> this.canHitEntity(entity) }, false
        )
    }

    override fun onHitEntity(hitResult: EntityHitResult) {
        super.onHitEntity(hitResult)
        val target = hitResult.entity
        val serverLevel = this.level() as? ServerLevel
        if (serverLevel != null) {
            val owner = getOwner().let {
                if (it is OwnableEntity && PazConfig.PLAYER_CREDIT_FOR_PLANT_KILLS) it.rootOwner
                else it as? LivingEntity
            }
            owner?.setLastHurtMob(target)

            // get damage from attribute
            val source = this.damageSources().source(damageType, this, owner)
            if(target.hurtServer(serverLevel, source, damage)) {
                if (target is LivingEntity) {
                    val knockbackDirection = calculateHorizontalHurtKnockbackDirection(target, source)
                    target.knockback(knockback, -knockbackDirection.leftDouble(), -knockbackDirection.rightDouble())
                    playSound(getHitSound(), 0.3f, 1.8f)
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
        if (this.getPierceLevel() <= 0) this.discard()
    }

    override fun onHitBlock(hitResult: BlockHitResult) {
        super.onHitBlock(hitResult)
        val movement = this.deltaMovement
        val offsetDirection = Vec3(sign(movement.x), sign(movement.y), sign(movement.z))
        val scaledMovement = offsetDirection.scale(0.05)
        this.setPos(this.position().subtract(scaledMovement))
        this.deltaMovement = Vec3.ZERO
        if (!isInGround()) {
            this.setPierceLevel(0)
            playSound(getHitSound(), 0.3f, 1.8f)
            afterHitBlockEffect(hitResult.blockPos)
            this.setInGround(true)
        }
    }

    open fun afterHitEntityEffect(target: LivingEntity) {
        if (isOnFire) target.igniteForSeconds(2.0f);
    }
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

    open fun getHitSound(): SoundEvent = SoundEvents.HONEY_BLOCK_BREAK
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

    protected fun knockbackNearby(damage: Float = this.damage, distance: Double = 1.0) {
        val serverLevel = this.level() as? ServerLevel?: return
        serverLevel.getEntitiesOfClass(
            LivingEntity::class.java,
            this.boundingBox.inflate(distance)
        ).forEach { nearby: LivingEntity? ->
            if (nearby == null || !canHitEntity(nearby)) return@forEach
            val direction = nearby.position().subtract(this.position())
            val knockbackVector = direction.normalize().scale(knockback)
            if (knockback > 0.0) {
                nearby.push(knockbackVector.x, 0.03, knockbackVector.z)
                val source = this.damageSources().source(damageType, this, getOwner())
                if(nearby.hurtServer(serverLevel, source, (damage/direction.length()*distance).toFloat())) {
                    val knockbackDirection = calculateHorizontalHurtKnockbackDirection(nearby, source)
                    nearby.knockback(knockback, -knockbackDirection.leftDouble(), -knockbackDirection.rightDouble())
                    if (nearby is ServerPlayer) nearby.connection.send(ClientboundSetEntityMotionPacket(nearby))
                }
            }
        }
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
             inertia = if (ignoreWaterDrag()) 1f else 0.8f
        } else inertia = 1.00f// No air drag so projectile calculations can be accurate

        this.deltaMovement = m.scale(inertia.toDouble())
    }

    open fun ignoreWaterDrag(): Boolean = false

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
        if ((entity is Plant && entityOwner is Plant) || (entity is Enemy && entityOwner is Enemy)) return false
        return if (this.hasSameRootOwner(entity)) false
        else entity !is Projectile && super.canHitEntity(entity) && !this.piercingIgnoreEntityIds.contains(entity.id)
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


