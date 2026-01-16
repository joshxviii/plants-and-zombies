package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.FurthestAttackableTargetGoal
import joshxviii.plantz.ai.goal.ProjectileAttackPlantGoal
import joshxviii.plantz.entity.projectile.Needle
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class Cactus(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.CACTUS, level) {
    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackPlantGoal(
            plantEntity = this,
            projectileFactory = { Needle(level= this.level(), owner=this) },
            velocity = 1.4,
            cooldownTime = 40,
            actionDelay = 6))
        this.targetSelector.addGoal(4, FurthestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
            && target !is Creeper
            && target !is Plant
        })
    }

    override fun canSurviveOn(block: BlockState): Boolean {
        return super.canSurviveOn(block) || block.`is`(BlockTags.SAND)
    }
}