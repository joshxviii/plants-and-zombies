package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.Plant
import net.minecraft.world.entity.ai.goal.Goal

/**
 * Defines an action goal for plants.
 * Used for triggering animations and action timing
 * @param actionDelay amount of time in ticks before [doAction] is called from when the action started.
 * @param actionStartEffect Callback function used to add effects at the start of the action
 * @param actionEndEffect Callback function used to add effects at the end of the action
 */
abstract class PlantActionGoal(
    val plantEntity: Plant,
    val cooldownTime: Int = 20,
    val actionDelay: Int = 0,
    val actionStartEffect: () -> Unit = {},
    val actionEndEffect: () -> Unit = {}
): Goal() {
    private var actionTimer = -1

    final override fun requiresUpdateEveryTick(): Boolean = true
    final override fun canContinueToUse(): Boolean = canUse()

    final override fun tick() {
        if (
            canDoAction()
            && plantEntity.cooldown <= 0
            && actionTimer == -1
        ) {
            plantEntity.cooldown = cooldownTime // start animation
            actionTimer = actionDelay.coerceAtLeast(0)
            actionStartEffect()
        }

        if (actionTimer > 0) --actionTimer
        if (actionTimer == 0) {// do action
            actionEndEffect()
            doAction()

            actionTimer = -1
        }
    }

    abstract fun canDoAction() : Boolean
    abstract fun doAction() : Boolean
}