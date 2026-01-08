package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.RangedPlantAttackGoal
import joshxviii.plantz.entity.projectile.Pea
import joshxviii.plantz.entity.projectile.PlantProjectile
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.arrow.Arrow
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level

class Cactus(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.CACTUS, level) {
    override fun createProjectile(): Projectile? {
        return Arrow(this.level(), this, ItemStack.EMPTY, null)
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, RangedPlantAttackGoal(this, attackIntervalMin = 20))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
        })
    }
}