package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.RangedPlantAttackGoal
import joshxviii.plantz.entity.projectile.Pea
import joshxviii.plantz.entity.projectile.PeaProjectile
import joshxviii.plantz.entity.projectile.Spore
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level

class PuffShroom(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.PUFF_SHROOM, level) {

    override fun createProjectile(): PeaProjectile? {
        return Spore(level=this.level(), owner=this)
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, RangedPlantAttackGoal(this, attackIntervalMin = 20))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
        })
    }
}