package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.plant.PlantGrowNeeds
import net.minecraft.world.entity.ai.goal.Goal

class SleepGoal(
    val plantEntity: Plant,
    val sleepDuringDay: Boolean = false,
    val sleepDuringNight: Boolean = false
) : Goal() {
    private var wakeUpDelay = 0
    private var sleepDelay = 0

    companion object {
        private val SLEEP_WAIT_TIME = reducedTickDelay(120)
    }

    override fun canUse(): Boolean {
        return ( this.canSleep() || plantEntity.isAsleep )
    }

    override fun canContinueToUse(): Boolean {
        return this.canSleep()
    }

    fun canSleep(): Boolean {
        val needs = plantEntity.testGrowConditions()
        return if (this.sleepDelay-- > 0) false
        else if (this.wakeUpDelay-- > 0) true
        else if (plantEntity.isAttached()) false
        else if (needs == PlantGrowNeeds.TIME) true
        else if (needs == PlantGrowNeeds.SUN) false
        else if (sleepDuringDay) plantEntity.sunIsVisible() else if (sleepDuringNight) !plantEntity.sunIsVisible() else false
    }

    override fun stop() {
        this.sleepDelay = plantEntity.random.nextInt(10,SLEEP_WAIT_TIME)
        plantEntity.isAsleep = false
    }

    override fun start() {
        this.wakeUpDelay = plantEntity.random.nextInt(10,SLEEP_WAIT_TIME)
        plantEntity.isAsleep = true
    }
}