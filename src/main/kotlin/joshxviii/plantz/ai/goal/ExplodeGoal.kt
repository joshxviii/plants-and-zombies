package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazSounds
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.random.WeightedList
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.SimpleExplosionDamageCalculator
import net.minecraft.world.level.gameevent.GameEvent
import java.util.*
import java.util.function.Predicate

class ExplodeGoal(
    private val plantEntity: Plant,
    val radius: Float = 2.5f,
    val detectRange: Double = 9.0,
    val actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    selector: TargetingConditions.Selector? = { target, level -> target is Enemy && target !is Plant }
) : Goal() {
    protected val targetConditions: TargetingConditions

    companion object {
        val EXPLOSION_DAMAGE_CALCULATOR: ExplosionDamageCalculator = SimpleExplosionDamageCalculator(false, true, Optional.of<Float>(1f), Optional.ofNullable(null))
        private const val DISTANCE_SQR = 49.0
    }

    private var target: LivingEntity? = null

    init {
        setFlags(EnumSet.of<Flag>(Flag.MOVE))
        targetConditions = TargetingConditions.forCombat().range(detectRange).selector(selector)
    }

    override fun canUse(): Boolean {
        if (!actionPredicate.test(plantEntity)) return false
        val level = plantEntity.level() as ServerLevel
        target = level.getNearestEntity(LivingEntity::class.java, targetConditions, plantEntity, plantEntity.x, plantEntity.y, plantEntity.z, plantEntity.boundingBox.inflate(detectRange))
        val t = target
        return (t != null && !t.isDeadOrDying && plantEntity.distanceToSqr(t) < detectRange) || plantEntity.swell > 0
    }

    override fun start() {
        plantEntity.getNavigation().stop()
    }

    override fun stop() {
        target = null
        plantEntity.swellDir = -1
    }

    override fun requiresUpdateEveryTick(): Boolean {
        return true
    }

    override fun tick() {
        val currentTarget = target

        plantEntity.swellDir = when {
            currentTarget == null -> -1
            currentTarget.isDeadOrDying -> -1
            plantEntity.distanceToSqr(currentTarget) > DISTANCE_SQR -> -1
            else -> 1
        }

        if (plantEntity.swellDir > 0 && plantEntity.swell == 0) {
            plantEntity.playSound(SoundEvents.CREEPER_PRIMED, 1.0f, 1f + (1-plantEntity.getMaxSwell() / 30))
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
            radius,
            false,
            Level.ExplosionInteraction.MOB,
            ParticleTypes.SMOKE,
            ParticleTypes.EXPLOSION,
            WeightedList.of(),
            PazSounds.PLANT_EXPLODE
        )
        plantEntity.discard()
    }

}