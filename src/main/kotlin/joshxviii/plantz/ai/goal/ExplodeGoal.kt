package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plant.Explosive
import net.minecraft.core.Holder
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.gameevent.GameEvent
import java.util.*
import java.util.function.Predicate

class ExplodeGoal(
    private val explosiveEntity: Explosive,
    val explosionRadius: Float = 2.5f,
    val sound: Holder.Reference<SoundEvent> = SoundEvents.GENERIC_EXPLODE,
    val destroyBlocks: Boolean = false,
    val activateRange: Double = 3.0,
    val actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    val actionEndEffect: () -> Unit = {},
    val discardOnExplode: Boolean = true,
) : Goal() {

    companion object {
        private const val DISTANCE_SQR = 49.0
    }

    private var target: LivingEntity? = null

    init {
        setFlags(EnumSet.of<Flag>(Flag.MOVE))
    }

    override fun canUse(): Boolean {
        if (explosiveEntity.swellDir!=0) return true
        if (!actionPredicate.test(explosiveEntity)) return false
        if ((explosiveEntity.isAsleep || explosiveEntity.isGrowingSeeds)) return false
        target = explosiveEntity.target
        target?.let {
            return (!it.isDeadOrDying && explosiveEntity.distanceToSqr(it) < activateRange * activateRange) || explosiveEntity.swell > 0
        }
        return false
    }

    override fun start() {
        explosiveEntity.getNavigation().stop()
    }

    override fun stop() {
        target = null
        explosiveEntity.swellDir = -1
    }

    override fun requiresUpdateEveryTick(): Boolean {
        return true
    }

    override fun tick() {
        val currentTarget = target

        if (explosiveEntity.swellDir != 2) explosiveEntity.swellDir = when {
            currentTarget == null -> -1
            currentTarget.isDeadOrDying -> -1
            explosiveEntity.distanceToSqr(currentTarget) > DISTANCE_SQR -> -1
            else -> 1
        }

        if (explosiveEntity.swellDir > 0 && explosiveEntity.swell == 0) {
            explosiveEntity.playSound(SoundEvents.CREEPER_PRIMED, 1.0f, 1f + (1-explosiveEntity.getMaxSwellTime() / 30))
            explosiveEntity.gameEvent(GameEvent.PRIME_FUSE)
        }

        if (explosiveEntity.swell == explosiveEntity.getMaxSwellTime()) {
            actionEndEffect()
            explosiveEntity.explode(
                radius = explosionRadius,
                sound = sound,
                destroyBlocks = destroyBlocks,
            )
            if (discardOnExplode) explosiveEntity.discard()
            explosiveEntity.swellDir = -1
        }
    }
}