package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazTags.BlockTags.PLANTABLE
import joshxviii.plantz.ai.goal.FurthestAttackableTargetGoal
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.Needle
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

class Cactus(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.CACTUS, level) {

    companion object {
        fun checkCactusSpawnRules(
            type: EntityType<out Plant>,
            level: LevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val blockBelow = level.getBlockState(pos.below())
            return checkValidSpawn(level, pos)
                    && (blockBelow.`is`(PLANTABLE)|| blockBelow.`is`(BlockTags.SAND) || blockBelow.`is`(Blocks.SOUL_SAND))
        }
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = { Needle(level(), this) },
            velocity = 1.5,
            cooldownTime = 40,
            actionDelay = 6))
        this.targetSelector.addGoal(4, FurthestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && target !is Creeper
                    && target is Zombie
                    || (target is Enemy && isTame)
        })
    }

    override fun canSurviveOn(block: BlockState): Boolean {
        return super.canSurviveOn(block) || block.`is`(BlockTags.SAND) || block.`is`(Blocks.SOUL_SAND)
    }
}