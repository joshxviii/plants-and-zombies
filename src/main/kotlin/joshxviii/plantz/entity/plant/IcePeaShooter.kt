package joshxviii.plantz.entity.plants

import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.projectile.PeaIce
import joshxviii.plantz.ai.goal.ProjectileAttackPlantGoal
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level

class IcePeaShooter(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.ICE_PEA_SHOOTER, level) {
    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackPlantGoal(
            plantEntity = this,
            projectileFactory =  { PeaIce(level= this.level(), owner=this) },
            cooldownTime = 20,
            actionDelay = 3))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
            && target !is Creeper
            && target !is Plant
        })
    }
}
