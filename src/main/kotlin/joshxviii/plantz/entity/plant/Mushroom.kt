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
        private var countdownAfter = 0
        private var countdownBefore = 0

        companion object {
            private val SLEEP_WAIT_TIME = reducedTickDelay(140)
        }

        override fun canUse(): Boolean {
            return ( this.canSleep() || mushroomEntity.isAsleep )
        }

        override fun canContinueToUse(): Boolean {
            return this.canSleep()
        }

        fun canSleep(): Boolean {
            return if (this.countdownBefore-- > 0) false
            else if (this.countdownAfter-- > 0) true
                else mushroomEntity.sunIsVisible()
        }

        override fun stop() {
            this.countdownBefore = mushroomEntity.random.nextInt(SLEEP_WAIT_TIME)
            mushroomEntity.isAsleep = false
        }

        override fun start() {
            this.countdownAfter = mushroomEntity.random.nextInt(SLEEP_WAIT_TIME)
            mushroomEntity.isAsleep = true
        }
    }

}