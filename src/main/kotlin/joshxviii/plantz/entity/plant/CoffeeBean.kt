package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.WakeUpSleepingPlantsGoal
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class CoffeeBean(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.COFFEE_BEAN, level) {
    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, WakeUpSleepingPlantsGoal(
            this,
            actionDelay = 7,
            actionEndEffect = {
                discard()
            }
        ))
    }
    override fun attackGoals() {}
}