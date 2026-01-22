package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.projectile.Pea
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level

class PeaShooter(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.PEA_SHOOTER, level) {
    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = { Pea(level = level(), owner = this)},
            cooldownTime = 20,
            actionDelay = 3))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && target !is Creeper
                    && target is Enemy
        })
    }
}
