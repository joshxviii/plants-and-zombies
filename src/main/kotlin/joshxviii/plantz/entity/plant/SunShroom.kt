package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.ai.goal.GenerateSunGoal
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class SunShroom(
    type: EntityType<out Mushroom>,
    level: Level,
) : Mushroom(PazEntities.SUN_SHROOM, level) {
    override fun attackGoals() {}

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, GenerateSunGoal(
            usingEntity = this,
            actionDelay = 10,
            generatesAtNight = true,
            sunAmount = 2
        ))
    }

    override fun stateUpdated(state: PlantState) {
        if (state == PlantState.INIT) {
            isBaby = true
            age = -3400
        }
        super.stateUpdated(state)
    }
}