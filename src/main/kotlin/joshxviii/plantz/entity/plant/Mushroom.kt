package joshxviii.plantz.entity.plant

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level

abstract class Mushroom(type: EntityType<out Mushroom>, level: Level) : Plant(type, level) {

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, MushroomSleepGoal(this))
    }

    private class MushroomSleepGoal(
        val mushroomEntity: Mushroom,
    ) : Goal() {
        companion object {
            private val WAIT_TIME_BEFORE_SLEEP = Goal.reducedTickDelay(140)
        }

        override fun canUse(): Boolean {
            return ( this.canSleep() || mushroomEntity.isAsleep )
        }

        override fun canContinueToUse(): Boolean {
            return this.canSleep()
        }

        fun canSleep(): Boolean {
            return mushroomEntity.sunIsVisible()
        }

        override fun stop() {
            mushroomEntity.isAsleep = false
        }

        override fun start() {
            mushroomEntity.isAsleep = true
        }
    }

}