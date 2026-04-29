package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.plant.PlantGrowNeeds
import net.minecraft.sounds.SoundEvents.FOX_SLEEP
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

    fun canSleep(): Boolean {// sleep check priority order
        //if (plantEntity.isAttached()) return false

        when (plantEntity.testGrowConditions()) {
            PlantGrowNeeds.TIME -> return true
            PlantGrowNeeds.SUN -> return false
            else -> {}
        }

        if (plantEntity.coffeeBuff > 0) return false

        if (sleepDelay-- > 0) return false
        if (wakeUpDelay-- > 0) return true
        val sunIsVisible = plantEntity.sunIsVisible()
        return when {
            sleepDuringDay -> sunIsVisible
            sleepDuringNight -> !sunIsVisible
            else -> false
        }
    }

    override fun stop() {
        this.sleepDelay = plantEntity.random.nextInt(10,SLEEP_WAIT_TIME)
        plantEntity.isAsleep = false
    }

    override fun start() {
        this.wakeUpDelay = plantEntity.random.nextInt(10,SLEEP_WAIT_TIME)
        plantEntity.isAsleep = true
        plantEntity.playSound(FOX_SLEEP, 1.0f, 1.3f)// TODO custom sound
    }
}