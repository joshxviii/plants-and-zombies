package joshxviii.plantz.entity.projectile

import joshxviii.plantz.entity.Plant
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.InsideBlockEffectApplier
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

abstract class PeaProjectile(
    type: EntityType<out PeaProjectile>,
    level: Level,
    owner: Plant? = null,
    val damageType: ResourceKey<DamageType> = DamageTypes.GENERIC
) : Projectile(type, level) {

    companion object {
        val PIERCE_LEVEL: EntityDataAccessor<Byte> = SynchedEntityData.defineId<Byte>(PeaProjectile::class.java, EntityDataSerializers.BYTE)
    }

    init {
        if (owner != null) {
            this.setOwner(owner)
            this.setPos(
                owner.x,
                owner.y + owner.eyeHeight,
                owner.z
            )
            this.setRot(
                owner.xRot,
                owner.yRot
            )
        }
    }

    override fun tick() {
        this.handleFirstTickBubbleColumn()
        this.applyGravity()
        this.applyInertia()
        val result =
            ProjectileUtil.getHitResultOnMoveVector(this) { entity: Entity? -> this.canHitEntity(entity!!) }
        val newPosition =
            if (result.type != HitResult.Type.MISS) result.getLocation()
            else this.position().add(this.deltaMovement)

        this.setPos(newPosition)
        this.updateRotation()
        this.applyEffectsFromBlocks()
        super.tick()
        if (result.type != HitResult.Type.MISS && this.isAlive) {
            this.hitTargetOrDeflectSelf(result)
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
            inertia = 0.8f
        } else inertia = 0.99f

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

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        entityData.define(PIERCE_LEVEL, 0.toByte())
    }

    private fun setPierceLevel(pieceLevel: Byte) { this.entityData.set(PIERCE_LEVEL, pieceLevel) }
    fun getPierceLevel(): Byte =this.entityData.get(PIERCE_LEVEL)

    override fun onHitEntity(hitResult: EntityHitResult) {
        super.onHitEntity(hitResult)
        val serverLevel = this.level() as? ServerLevel
        if (serverLevel != null) {
            val target = hitResult.entity
            val owner = this.getOwner() as? LivingEntity
            owner?.setLastHurtMob(target)

            // get damage from attribute
            val damage : Float = if (owner?.attributes?.hasAttribute(Attributes.ATTACK_DAMAGE)!=null)
                owner.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat()
            else
                1.0f

            val source = this.damageSources().source(damageType, target, owner)
            if(target.hurtServer(serverLevel, source, damage)) {
                owner?.let {// apply knockback
                    val knockbackDir = target.position().subtract(it.position()).normalize()
                    val strength = 0.6
                    target.push(knockbackDir.x * strength, 0.3, knockbackDir.z * strength)
                    target.hurtMarked = true
                }
            }
        }
    }
    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        if (!this.level().isClientSide) {
            this.playSound(SoundEvents.HONEY_BLOCK_PLACE, 0.4f, 1.8f)
            this.discard()
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
        return if (entity is Plant) false
        else super.canHitEntity(entity)
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

    override fun getDefaultGravity(): Double { return 0.03 }
}


