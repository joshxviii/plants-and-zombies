package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ExplodeGoal
import joshxviii.plantz.ai.goal.GenerateSunGoal
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level

class PotatoMine(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.POTATO_MINE, level) {
    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, ExplodeGoal(
            plantEntity = this,
        ))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
                    && target !is Plant
        })
    }

    override fun doPush(entity: Entity) {}
}