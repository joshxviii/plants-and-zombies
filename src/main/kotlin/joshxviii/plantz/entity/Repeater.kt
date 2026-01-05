package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.goal.RangedPlantAttackGoal
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.RangedAttackGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.RangedAttackMob
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import kotlin.math.sqrt

class Repeater(level: Level) : Plant(PazEntities.REPEATER, level) {

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, RangedPlantAttackGoal(this, attackIntervalMin = 4))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
        })
    }

    override fun performRangedAttack(target: LivingEntity, power: Float) {
        val xd = target.x - this.x
        val yd = target.eyeY - 1.1f
        val zd = target.z - this.z
        val yo = sqrt(xd * xd + zd * zd) * 0.2f
        if (this.level() is ServerLevel) {
            val itemStack = ItemStack(Items.SNOWBALL)
            Projectile.spawnProjectile(Snowball(this.level(), this, itemStack), this.level() as ServerLevel, itemStack) { projectile: Snowball? ->
                projectile!!.shoot(xd, yd + yo - projectile.y, zd, 1.6f, 12.0f)
            }
        }

        this.playSound(SoundEvents.BUBBLE_POP, 3.0f, 0.4f / (this.getRandom().nextFloat() * 0.4f + 0.8f))
    }
}
