package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.Spore
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level

class HypnoShroom(type: EntityType<out Mushroom>, level: Level) : Mushroom(PazEntities.HYPNOSHROOM, level) {
    override fun registerGoals() {
        super.registerGoals()

        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
            && target !is Creeper
            && target !is Plant
        })
    }
}