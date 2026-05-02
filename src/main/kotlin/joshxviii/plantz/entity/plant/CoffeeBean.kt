package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazTags.BlockTags.PLANTABLE
import joshxviii.plantz.ai.goal.WakeUpSleepingPlantsGoal
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState

class CoffeeBean(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.COFFEE_BEAN, level) {

    companion object {
        fun checkCoffeeBeanSpawnRules(
            type: EntityType<out Plant>,
            level: LevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val blockBelow = level.getBlockState(pos.below())
            return checkValidSpawn(level, pos)
                    && (blockBelow.`is`(PLANTABLE) || blockBelow.`is`(BlockTags.LEAVES))
        }
    }

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, WakeUpSleepingPlantsGoal(
            this,
            actionDelay = 9,
            actionEndEffect = {
                addParticlesAroundSelf(
                    level(),
                    DustParticleOptions(8606770, 1f),
                    amount = 12..14,
                    horizontalSpreadScale = 0.1,
                    verticalSpreadScale = 0.3,
                    height = 0.25f,
                    speed = 0.02
                )
                discard()
            }
        ))
    }
    override fun attackGoals() {}

    override fun canSurviveOn(block: BlockState): Boolean {
        return super.canSurviveOn(block) || block.`is`(BlockTags.LEAVES)
    }
}