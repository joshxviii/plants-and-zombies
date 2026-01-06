package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.projectile.Pea
import joshxviii.plantz.entity.projectile.PlantProjectile
import joshxviii.plantz.goal.RangedPlantAttackGoal
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import java.util.function.Consumer
import kotlin.math.sqrt

class PeaShooter(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.PEA_SHOOTER, level) {

    override fun createProjectile(): PlantProjectile? {
        return Pea(level= this.level(), owner=this)
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, RangedPlantAttackGoal(this, attackIntervalMin = 20))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
        })
    }
}
