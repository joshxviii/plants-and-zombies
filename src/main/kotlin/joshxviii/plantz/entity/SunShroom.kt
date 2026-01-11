package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.GenerateSunGoal
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class SunShroom(
    type: EntityType<out Plant>,
    level: Level,
) : Plant(PazEntities.SUN_SHROOM, level) {
    override fun attackGoals() {}

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, GenerateSunGoal(
            plantEntity = this,
            actionDelay = 10
        ))
    }
}