package joshxviii.plantz.entity.plant

import joshxviii.plantz.*
import joshxviii.plantz.PazTags.BlockTags.PLANTABLE
import joshxviii.plantz.ai.goal.ExplodeGoal
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.AreaEffectCloud
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

class DoomShroom(type: EntityType<out Mushroom>, level: Level) : Mushroom(PazEntities.DOOM_SHROOM, level) {


    companion object {
        fun checkDoomShroomSpawnRules(
            type: EntityType<out Plant>,
            level: LevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val blockBelow = level.getBlockState(pos.below())
            return checkValidSpawn(level, pos)
                    && (blockBelow.`is`(PLANTABLE) || blockBelow.`is`(Blocks.GRAVEL))
        }
    }

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, ExplodeGoal(
            plantEntity = this,
            radius = 20f
        ))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && target is Zombie
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame)
        })
    }

    override fun canSurviveOn(block: BlockState): Boolean {
        return super.canSurviveOn(block) || block.`is`(Blocks.GRAVEL)
    }
}