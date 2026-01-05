package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball
import net.minecraft.world.level.Level

class IcePea(level: Level) : Plant(PazEntities.ICE_PEA, level) {

    override fun registerGoals() {
        super.registerGoals()

        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
        })
    }

    fun performRangedAttack(target: LivingEntity, velocity: Float) {
        val snowball = Snowball(EntityType.SNOWBALL, this.level())

        // Adjust spawning position to the "head" area of the plant
        val d0 = target.eyeY - 1.100000023841858
        val d1 = target.x - this.x
        val d2 = d0 - snowball.y
        val d3 = target.z - this.z
        val d4 = Math.sqrt(d1 * d1 + d3 * d3).toFloat() * 0.2f

        snowball.shoot(d1, d2 + d4.toDouble(), d3, 1.6f, 2.0f)

        this.playSound(SoundEvents.SNOWBALL_THROW, 1.0f, 0.4f / (this.getRandom().nextFloat() * 0.4f + 0.8f))
        this.level().addFreshEntity(snowball)
    }
}
