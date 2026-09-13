package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazTags.EntityTypes.WALLNUT_DEFLECTABLE
import joshxviii.plantz.applyImpulse
import joshxviii.plantz.entity.Sun
import net.minecraft.core.Direction
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf

open class WallNut(type: EntityType<out Plant>, level: Level) : Plant(type, level) {

    companion object {
        fun wallNutReducedDamage(entity: Entity?, damage: Float): Float {
            return if (entity is Zombie) damage*0.666f else damage
        }

        fun wallNutCollision(wallnut: Plant, other: Entity?): Boolean {
            if (other is Zombie && other.swingTime == 0) {// when colliding with a zombie, the zombie will attack the wallnut
                val level = other.level() as? ServerLevel
                if (level != null && other.isAlive) {
                    val damage = other.getAttribute(Attributes.ATTACK_DAMAGE)?.value?.toFloat() ?: 1f
                    if (wallnut.hurtServer(level, other.damageSources().mobAttack(other), damage)) {
                        other.swing(InteractionHand.MAIN_HAND)
                    }
                }
            }
            if (other is Sun) return false
            return wallnut.isAlive && other != wallnut.attachedEntity
        }

        val ROLLING: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(WallNut::class.java, EntityDataSerializers.BOOLEAN)

        private const val ROLL_FRICTION = 0.99
    }

    var isRolling: Boolean
        get() = this.entityData.get(ROLLING)
        private set(value) { this.entityData.set(ROLLING, value) }

    var stopTick = 0
    var rollRotation: Quaternionf = Quaternionf()

    fun roll(direction: Vec3 = Direction.fromYRot(yRot.toDouble()).unitVec3, power: Float = 0.51f) {
        isRolling = true
        applyImpulse(direction.normalize(), pow = power, uncertainty = 0.1f)
    }

    override fun clampToGrid(): Boolean = !isRolling

    override fun limitPistonMovement(vec: Vec3): Vec3 {
        val result = super.limitPistonMovement(vec)
        if (result.length() > 0.25) roll(result)
        return result
    }

    override fun shouldDiscardFriction(): Boolean {
        return isRolling || super.shouldDiscardFriction()
    }

    override fun tick() {
        super.tick()
        if (deltaMovement.horizontalDistance() > 0.075) stopTick = 4

        val motion = deltaMovement
        if (motion.horizontalDistance() > 1.0e-4) {
            setDeltaMovement(
                motion.x * ROLL_FRICTION,
                motion.y,
                motion.z * ROLL_FRICTION
            )
        }

        if (isRolling && --stopTick <= 0) {
            isRolling = false
            deltaMovement = Vec3.ZERO
            applyGridClamp()
        }

    }

    override fun isInvulnerable(): Boolean {
        return isRolling || super.isInvulnerable()
    }

    override fun doPush(entity: Entity) {
        super.doPush(entity)
        if (isRolling && entity is LivingEntity && entity !is Plant) {
            val level = level() as? ServerLevel?: return
            val source = this.damageSources().source(PazDamageTypes.PLANT, this, if (PazConfig.PLAYER_CREDIT_FOR_PLANT_KILLS) this.rootOwner else this)
            val damage = deltaMovement.horizontalDistance().toFloat() * 4.5f
            entity.hurtServer(level, source, damage)
            val vector = entity.position().subtract(position()).normalize()
            entity.applyImpulse(vector, pow = 1.25f, uncertainty = 0.3f)
        }
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(ROLLING, false)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putBoolean("plantz:IsRolling", isRolling)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        isRolling = input.getBooleanOr("plantz:IsRolling", false)
    }

    override fun attackGoals() {}

    override fun canBeCollidedWith(other: Entity?): Boolean = if(isRolling) super.canBeCollidedWith(other) else wallNutCollision(this, other)

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean{
        source.directEntity?.let {
            if (it.`is`(WALLNUT_DEFLECTABLE)) return false
        }
        return super.hurtServer(level, source, damage)
    }

    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        super.actuallyHurt(level, source,
            wallNutReducedDamage(source.entity, damage)
        )
    }

    override fun canSurviveOn(block: BlockState): Boolean {
        return super.canSurviveOn(block) || !block.getCollisionShape(level(), blockPosition().below()).isEmpty
    }
}