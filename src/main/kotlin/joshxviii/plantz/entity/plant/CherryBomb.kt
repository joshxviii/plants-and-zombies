package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazTags.BlockTags.PLANTABLE
import joshxviii.plantz.ai.goal.ExplodeGoal
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

class CherryBomb(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.CHERRY_BOMB, level) {

    companion object {
        fun checkCherryBombSpawnRules(
            type: EntityType<out Plant>,
            level: LevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val blockBelow = level.getBlockState(pos.below())
            return checkValidSpawn(level, pos)
                    && (blockBelow.`is`(PLANTABLE) || !blockBelow.`is`(BlockTags.AIR))
        }
    }

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, ExplodeGoal(
            plantEntity = this,
        ))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target !is Plant
                && target is Enemy
        })
    }

    override fun canSurviveOn(block: BlockState): Boolean {
        return super.canSurviveOn(block) || !block.`is`(BlockTags.AIR)
    }
}