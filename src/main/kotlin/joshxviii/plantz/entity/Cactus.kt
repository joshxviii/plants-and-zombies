package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.RangedPlantAttackGoal
import joshxviii.plantz.entity.projectile.Needle
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level

class Cactus(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.CACTUS, level) {
    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, RangedPlantAttackGoal(this,
            projectileFactory = { Needle(level= this.level(), owner=this) },
            cooldownTime = 50,
            actionDelay = 6))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
        })
    }
}