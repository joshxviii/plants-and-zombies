package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.WaterSpore
import net.minecraft.core.BlockPos
import net.minecraft.tags.FluidTags
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState

class TangleKelp(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.TANGLE_KELP, level) {

    companion object {
        fun checkTangleKelpSpawnRules(
            type: EntityType<out Plant>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val isRaining = level.level.isRaining
            val inWater = level.getFluidState(pos).`is`(FluidTags.WATER)
            val rainBonus = if (isRaining) 2.25f else 1f

            return EntitySpawnReason.isSpawner(spawnReason) ||
                (inWater && random.nextFloat() < (0.1 * rainBonus))
        }
    }

    override fun isPushable(): Boolean = false

    override fun registerGoals() {
        super.registerGoals()

        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && (target is Zombie
                    || (target is Enemy && isTame))
        })
    }

    override fun canBreatheUnderwater(): Boolean = true

    override fun canSurviveOn(block: BlockState): Boolean {
        return block.`is`(PazBlocks.ZEN_PLANT_POT) || level().getBlockState(blockPosition()).fluidState.`is`(FluidTags.WATER)
    }
}