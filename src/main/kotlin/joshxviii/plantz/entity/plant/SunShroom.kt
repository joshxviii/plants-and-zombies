package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazTags
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.ai.goal.GenerateSunGoal
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class SunShroom(
    type: EntityType<out Plant>,
    level: Level,
) : Plant(type, level) {
    override fun attackGoals() {}

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, GenerateSunGoal(
            cooldownTime = 1300,
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

    override fun canSurviveOn(block: BlockState): Boolean {
        return super.canSurviveOn(block) || block.`is`(PazTags.BlockTags.CAVE_SPAWNABLE_ON)
    }

    override fun canPlaceOn(block: BlockState): Boolean = super.canSurviveOn(block)
}