package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plant.Plant
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal

/**
 * Defines an action goal for plants.
 * Used for triggering animations and action timing
 * @param actionDelay amount of time in ticks before [doAction] is called from when the action started.
 * @param actionStartEffect Callback function used to add effects at the start of the action
 * @param actionEndEffect Callback function used to add effects at the end of the action
 */
abstract class PlantActionGoal(
    val usingEntity: PathfinderMob,
    val cooldownTime: Int = 20,
    val actionDelay: Int = 0,
    val actionStartEffect: () -> Unit = {},
    val actionEndEffect: () -> Unit = {}
): Goal() {
    var isDoingAction = false
    var actionTimer = -1

    override fun stop() {
        isDoingAction = false
        actionTimer = -1
    }

    final override fun requiresUpdateEveryTick(): Boolean = true
    final override fun canContinueToUse(): Boolean = canUse()

    override fun tick() {
        if (
            canDoAction()
            && !(usingEntity is Plant && usingEntity.cooldown > 0)
            && actionTimer == -1
        ) {
            (usingEntity as? Plant)?.cooldown = cooldownTime // start animation
            actionTimer = actionDelay.coerceAtLeast(0)
            actionStartEffect()
            isDoingAction = true
        }

        if (actionTimer > 0) --actionTimer
        if (actionTimer == 0) {// do action
            actionEndEffect()
            doAction()
            isDoingAction = false
            actionTimer = -1
        }
    }

    abstract fun canDoAction() : Boolean
    abstract fun doAction() : Boolean
}