package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plant.Plant
import net.minecraft.util.Mth
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import java.util.function.Predicate

/**
 * Defines an action goal for plants.
 * Used for triggering animations and action timing
 * @param actionDelay amount of time in ticks before [doAction] is called from when the action started.
 * @param actionStartEffect Callback function used to add effects at the start of the action
 * @param actionSuccessEffect Callback function used to add effects at the end of the action
 */
abstract class ActionGoal(
    val usingEntity: PathfinderMob,
    val cooldownTime: Int = 20,
    val actionDelay: Int = 0,
    val actionStartEffect: () -> Unit = {},
    val actionSuccessEffect: () -> Unit = {},
    val actionEndEffect: () -> Unit = {},
    val actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    val delayedEffectDelay: Int = 0,
    val delayedEffect: () -> Unit = {},
    val cooldownVariationRange: IntRange = 0..0
): Goal() {
    var isDoingAction = false
    var actionTimer = -1

    init {
        if (startOnCooldown()) (usingEntity as? Plant)?.cooldown = cooldownTime
    }

    override fun stop() {
        isDoingAction = false
        actionTimer = -1
    }

    final override fun requiresUpdateEveryTick(): Boolean = true
    final override fun canContinueToUse(): Boolean = canUse()

    override fun tick() {
        if (
            canDoAction()
            && !(usingEntity is Plant && usingEntity.cooldown > -1)
            && actionTimer == -1
        ) {
            (usingEntity as? Plant)?.cooldown = Mth.floor(
                (cooldownTime+cooldownVariationRange.random()) *
                        if (usingEntity.poweredUp) 0.8 else 1.0
            ).coerceAtLeast(actionDelay)
            actionTimer = actionDelay.coerceAtLeast(0)
            actionStartEffect()
            isDoingAction = true
        }

        if (actionTimer > 0) --actionTimer
        if (actionTimer == delayedEffectDelay) delayedEffect()
        if (actionTimer == 0) {// do action
            if (actionPredicate.test(usingEntity)) if (doAction()) actionSuccessEffect()
            isDoingAction = false
            actionTimer = -1
            actionEndEffect()
        }
    }

    open fun startOnCooldown(): Boolean = true
    abstract fun canDoAction() : Boolean
    abstract fun doAction() : Boolean
}