package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.Spore
import net.minecraft.core.BlockPos
import net.minecraft.tags.FluidTags
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState

class SeaShroom(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.SEA_SHROOM, level) {

    companion object {
        fun checkSeaShroomSpawnRules(
            type: EntityType<out Plant>,
            level: LevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val blockBelow = level.getBlockState(pos.below())
            val block = level.getBlockState(pos)
            return checkValidSpawn(level, pos)
                    && (blockBelow.`is`(PazBlocks.ZEN_PLANT_POT) || block.fluidState.`is`(FluidTags.WATER))
        }
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = { Spore(level(), this) },
            cooldownTime = 20))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && target !is Creeper
                    && (target is Zombie
                    || (target is Enemy && isTame))
        })
    }

    override fun canBreatheUnderwater(): Boolean = true

    override fun canSurviveOn(block: BlockState): Boolean {
        val blockAbove = this.level().getBlockState(this.blockPosition().above())
        return block.`is`(PazBlocks.ZEN_PLANT_POT) || blockAbove.fluidState.`is`(FluidTags.WATER)
    }
}