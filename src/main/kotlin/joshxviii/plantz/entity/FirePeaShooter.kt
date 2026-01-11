package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.projectile.PeaFire
import joshxviii.plantz.ai.goal.ProjectileAttackPlantGoal
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level

class FirePeaShooter(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.FIRE_PEA_SHOOTER, level) {
    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackPlantGoal(
            plantEntity = this,
            projectileFactory = { PeaFire(level = this.level(), owner = this) },
            cooldownTime = 20,
            actionDelay = 3))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
        })
    }
}
