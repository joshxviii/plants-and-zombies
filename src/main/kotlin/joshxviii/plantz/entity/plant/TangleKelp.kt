package joshxviii.plantz.entity.plant

import joshxviii.plantz.*
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import kotlin.math.sqrt

class TangleKelp(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.TANGLE_KELP, level) {

    companion object {
        fun checkTangleKelpSpawnRules(
            type: EntityType<out Plant>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val isRaining = level.level.isRaining
            val inWater = level.getFluidState(pos).`is`(FluidTags.WATER)
            val rainBonus = if (isRaining) 2.25f else 1f

            return checkValidSpawn(level, pos, spawnReason)
                    && inWater && random.nextFloat() < (0.1 * rainBonus)
        }
        val TANGLE_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(TangleKelp::class.java, EntityDataSerializers.INT)
    }

    var tangleTime: Int
        get() = this.entityData.get(TANGLE_TIME_ID)
        set(value) = this.entityData.set(TANGLE_TIME_ID, value)

    override fun isPushable(): Boolean = false
    override fun tick() {
        super.tick()

        if (level() !is ServerLevel) return

        if (tangleTime > 0) tangleTime--

        val target = target ?: return
        if (!target.isAlive || !canTargetBePulled(target)) return

        applyTanglePull(target)
    }

    override fun doPush(entity: Entity) {
        if (isGrowingSeeds) return
        if (entity is Plant || (entity is Player && isTame) || this.hasSameRootOwner(entity)) return
        val level = level() as? ServerLevel?: return
        if (tickCount % 20 != 0) return
        val damage = getAttribute(Attributes.ATTACK_DAMAGE)?.value?.toFloat() ?: 1f
        entity.hurtServer(level, damageSources().source(PazDamageTypes.PLANT, this), damage)
    }

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, TangleKelpAttackGoal(this))

        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && (target is Zombie
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame))
        })
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(TANGLE_TIME_ID, 0)
    }

    override fun canBreatheUnderwater(): Boolean = true

    private fun canTargetBePulled(target: LivingEntity): Boolean {
        if (this.hurtTime > 0) return false
        if (target is Plant) return false
        if (target is Player && isTame) return false
        if (hasSameRootOwner(target)) return false
        val targetAttacker: LivingEntity? = target.lastHurtByMob
        if (targetAttacker?.`is`(PazEntities.TANGLE_KELP) == true && targetAttacker != this) {
            this.target = null
            return false
        }

        val pullRange = getAttribute(Attributes.FOLLOW_RANGE)?.value ?: 4.75
        return distanceToSqr(target) <= pullRange * pullRange && distanceToSqr(target) > 1
    }

    private fun applyTanglePull(target: LivingEntity) {
        if (level() !is ServerLevel) return
        val kelpCenter = Vec3(x, y + bbHeight * 0.5, z)
        val targetCenter = Vec3(target.x, target.y + target.bbHeight * 0.5, target.z)
        val pullVector = kelpCenter.subtract(targetCenter)
        val distanceSqr = pullVector.lengthSqr()
        if (distanceSqr <= 1.0E-4) return

        val distance = sqrt(distanceSqr)
        val pullStrength = Mth.clamp(0.35 / distanceSqr, 0.005, 0.03)
        val direction = pullVector.scale(1.0 / distance).scale(pullStrength)

        if (target is ServerPlayer) target.connection.send(ClientboundSetEntityMotionPacket(target))
        target.addDeltaMovement(direction)
        target.needsSync = true
        target.checkFallDistanceAccumulation()

        if (tickCount % 4 == 0) {
            (level() as? ServerLevel)?.sendParticles(
                ElectricArcParticleOptions(
                    Vec3(target.getRandomX(0.2), target.randomY, target.getRandomZ(0.2)),
                    color = 0x354023,
                    width = 0.12f
                ),
                x + direction.x, y + eyeHeight, z + direction.z,
                1, 0.0, 0.0, 0.0, 0.0
            )
        }

    }

    class TangleKelpAttackGoal(
        val tangleKelp: TangleKelp,
    ) : MeleeAttackActionGoal(
        usingEntity = tangleKelp,
        cooldownTime = 60,
        actionDelay = 10,
        damageType = PazDamageTypes.PLANT_CHOMP,
        actionStartEffect = {},
        actionPredicate = { tangleKelp.tangleTime <= 0 }
    ) {
        companion object {
            const val TANGLE_TIME = 140
        }

        override fun doAction() : Boolean {
            val success = super.doAction()
            if (success) {
                tangleKelp.playSound(SoundEvents.CREAKING_ACTIVATE, 1f, 1f)
                tangleKelp.tangleTime = TANGLE_TIME + tangleKelp.random.nextInt(-10,20)
                tangleKelp.target?.addEffect(MobEffectInstance(PazEffects.TANGLED, 80, 0), tangleKelp)
            }
            return success
        }
    }

    override fun canSurviveOn(block: BlockState): Boolean {
        return block.`is`(PazBlocks.ZEN_PLANT_POT) || level().getBlockState(blockPosition()).fluidState.`is`(FluidTags.WATER)
    }
}
