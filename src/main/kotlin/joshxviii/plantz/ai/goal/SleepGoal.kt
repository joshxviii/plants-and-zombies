package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plant.Plant
import net.minecraft.world.entity.ai.goal.Goal

class SleepGoal(
    val plantEntity: Plant,
    val sleepDuringDay: Boolean = false
) : Goal() {
    private var countdownAfter = 0
    private var countdownBefore = 0

    companion object {
        private val SLEEP_WAIT_TIME = reducedTickDelay(140)
    }

    override fun canUse(): Boolean {
        return ( this.canSleep() || plantEntity.isAsleep )
    }

    override fun canContinueToUse(): Boolean {
        return this.canSleep()
    }

    fun canSleep(): Boolean {
        return if (this.countdownBefore-- > 0) false
        else if (this.countdownAfter-- > 0) true
        else if (sleepDuringDay) plantEntity.sunIsVisible() else !plantEntity.sunIsVisible()
    }

    override fun stop() {
        this.countdownBefore = plantEntity.random.nextInt(SLEEP_WAIT_TIME)
        plantEntity.isAsleep = false
    }

    override fun start() {
        this.countdownAfter = plantEntity.random.nextInt(SLEEP_WAIT_TIME)
        plantEntity.isAsleep = true
    }
}