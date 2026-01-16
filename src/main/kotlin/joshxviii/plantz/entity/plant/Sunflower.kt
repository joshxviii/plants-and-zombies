package joshxviii.plantz.entity.plants

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.GenerateSunGoal
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class Sunflower(
    type: EntityType<out Plant>,
    level: Level,
) : Plant(PazEntities.SUNFLOWER, level) {
    override fun attackGoals() {}

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, GenerateSunGoal(
            plantEntity = this,
            actionDelay = 10
        ))
    }
}