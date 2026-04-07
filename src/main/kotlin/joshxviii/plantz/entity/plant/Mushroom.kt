package joshxviii.plantz.entity.plant

import joshxviii.plantz.ai.goal.SleepGoal
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level

abstract class Mushroom(type: EntityType<out Mushroom>, level: Level) : Plant(type, level) {

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, SleepGoal(this, sleepDuringDay = true))
    }
}