package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazSounds
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.particles.ExplosionParticleInfo
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.random.WeightedList
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageSources
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.SimpleExplosionDamageCalculator
import net.minecraft.world.level.gameevent.GameEvent
import java.util.*
import java.util.function.Predicate

class ExplodeGoal(
    private val plantEntity: Plant,
    val actionPredicate: Predicate<PathfinderMob> = Predicate { true },
) : Goal() {

    companion object {
        val EXPLOSION_DAMAGE_CALCULATOR: ExplosionDamageCalculator = SimpleExplosionDamageCalculator(false, true, Optional.of<Float>(1f), Optional.ofNullable(null))
        private const val DISTANCE_SQR = 49.0
    }

    private var target: LivingEntity? = null

    init {
        setFlags(EnumSet.of<Flag>(Flag.MOVE))
    }

    override fun canUse(): Boolean {
        if (!actionPredicate.test(plantEntity)) return false
        val target = plantEntity.target
        return (target != null && !target.isDeadOrDying && plantEntity.distanceToSqr(target) < 9.0) || plantEntity.swell > 0
    }

    override fun start() {
        plantEntity.getNavigation().stop()
        target = plantEntity.target
    }

    override fun stop() {
        target = null
    }

    override fun requiresUpdateEveryTick(): Boolean {
        return true
    }

    override fun tick() {
        val currentTarget = target ?: return

        plantEntity.swellDir = when {
            currentTarget.isDeadOrDying -> -1
            plantEntity.distanceToSqr(currentTarget) > DISTANCE_SQR -> -1
            !plantEntity.sensing.hasLineOfSight(currentTarget) -> -1
            else -> 1
        }

        if (plantEntity.swellDir > 0 && plantEntity.swell == 0) {
            plantEntity.playSound(SoundEvents.CREEPER_PRIMED, 1.0f, 0.5f)
            plantEntity.gameEvent(GameEvent.PRIME_FUSE)
        }

        if (plantEntity.swell == plantEntity.getMaxSwell()) explode()
    }

    fun explode() {
        val level = plantEntity.level()
        level.explode(
            plantEntity,
            level.damageSources().source(PazDamageTypes.EXPLODE),
            EXPLOSION_DAMAGE_CALCULATOR,
            plantEntity.x,
            plantEntity.y,
            plantEntity.z,
            3.0f,
            false,
            Level.ExplosionInteraction.MOB,
            ParticleTypes.SMOKE,
            ParticleTypes.EXPLOSION,
            WeightedList.of<ExplosionParticleInfo>(),
            PazSounds.SNOWCHUNK_HIT
        )
        plantEntity.discard()
    }

}