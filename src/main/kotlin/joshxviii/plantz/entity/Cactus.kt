package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackPlantGoal
import joshxviii.plantz.entity.projectile.Needle
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks

class Cactus(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.CACTUS, level) {
    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackPlantGoal(
            plantEntity = this,
            projectileFactory = { Needle(level= this.level(), owner=this) },
            cooldownTime = 50,
            actionDelay = 6))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
        })
    }

    override fun canSurviveOn(blockPos: BlockPos): Boolean {
        return super.canSurviveOn(blockPos) || this.level().getBlockState(blockPos).`is`(BlockTags.SAND)
    }
}