package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.Plant
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import kotlin.math.sqrt

class RangedPlantAttackGoal(
    val plantEntity: Plant,
    val projectileFactory: () -> Projectile,
    val attackRadius: Float = 16f,
    val cooldownTime: Int = 20,
    val actionDelay: Int = 0,
    var target: LivingEntity? = null,
    var seeTime : Int = 0,
    val attackRadiusSqr: Float = 0f
) : Goal() {
    private var attackTimer = -1

    override fun canUse(): Boolean {
        target = plantEntity.target
        return target?.isAlive == true
    }

    override fun canContinueToUse(): Boolean = canUse()

    override fun stop() {
        target = null
        seeTime = 0
        attackTimer = -1
    }

    override fun requiresUpdateEveryTick(): Boolean = true

    override fun tick() {
        val target = this.target ?: return
        if (!target.isAlive) return

        val distanceSqr = plantEntity.distanceToSqr(target)
        val hasLineOfSight = plantEntity.sensing.hasLineOfSight(target)

        if (hasLineOfSight) seeTime++
        else seeTime = 0

        plantEntity.lookControl.setLookAt(target, 30f, 30f)

        if (plantEntity.cooldown <= 0 && attackTimer == -1 && hasLineOfSight && distanceSqr <= (attackRadius * attackRadius)) {
            plantEntity.cooldown = cooldownTime // start animation
            attackTimer = actionDelay.coerceAtLeast(0)
        }

        if (attackTimer > 0) --attackTimer
        if (attackTimer == 0) {// attack
            val power = calculatePower(distanceSqr)
            rangedAttack(target, power)

            attackTimer = -1
        }
    }

    private fun calculatePower(targetDistSqr: Double): Float {
        val dist = sqrt(targetDistSqr).toFloat() / attackRadius
        return Mth.clamp(dist, 0.2f, 1.0f)
    }

    private fun rangedAttack(target: LivingEntity, power: Float) {
        val speed = 1.5f
        val xd = target.x - plantEntity.x
        val yd = target.eyeY - (plantEntity.y + plantEntity.eyeHeight)
        val zd = target.z - plantEntity.z
        val yo = sqrt(xd * xd + zd * zd) * 0.2f

        if (plantEntity.level() is ServerLevel) {
            val level = plantEntity.level() as ServerLevel
            val projectile = projectileFactory()

            Projectile.spawnProjectile(projectile, level, ItemStack.EMPTY) {
                it.shoot(xd, yd + yo - it.y + plantEntity.eyeHeight, zd, power * speed, 1.0f)
            }
        }

        plantEntity.playSound(SoundEvents.BUBBLE_POP, 3.0f, 0.4f / (plantEntity.random.nextFloat() * 0.4f + 0.8f))
    }
}