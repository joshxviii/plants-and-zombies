package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.Plant
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.goal.Goal

/**
 * Defines an action goal for plants
 */
abstract class PlantActionGoal(
    val plantEntity: Plant,
    val cooldownTime: Int = 20,
    val actionDelay: Int = 0
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
        }

        if (actionTimer > 0) --actionTimer
        if (actionTimer == 0) {// do action
            doAction()

            actionTimer = -1
        }
    }

    abstract fun canDoAction() : Boolean
    abstract fun doAction()
}