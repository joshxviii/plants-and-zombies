package joshxviii.plantz.goal

import joshxviii.plantz.entity.Plant
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import kotlin.math.sqrt

class RangedPlantAttackGoal(
    val plantEntity: Plant,
    val attackRadius: Float = 16f,
    val attackIntervalMin: Int = 20,
    val attackIntervalMax: Int = attackIntervalMin,
    var target: LivingEntity? = null,
    var attackTime : Int = -1,
    var seeTime : Int = 0,
    val attackRadiusSqr: Float = 0f
) : Goal() {
    override fun canUse(): Boolean {
        val bestTarget: LivingEntity? = this.plantEntity.target
        if (bestTarget != null && bestTarget.isAlive) {
            this.target = bestTarget
            return true
        } else {
            return false
        }
    }

    override fun canContinueToUse(): Boolean {
        return this.canUse() || this.target!!.isAlive && !this.plantEntity.getNavigation().isDone
    }

    override fun stop() {
        this.target = null
        this.seeTime = 0
        this.attackTime = -1
    }

    override fun requiresUpdateEveryTick(): Boolean {
        return true
    }

    override fun tick() {
        val targetDistSqr: Double =
            this.plantEntity.distanceToSqr(this.target!!.x, this.target!!.y, this.target!!.z)
        val hasLineOfSight: Boolean = this.plantEntity.sensing.hasLineOfSight(this.target!!)
        if (hasLineOfSight) {
            this.seeTime++
        } else {
            this.seeTime = 0
        }

        this.plantEntity.getLookControl().setLookAt(this.target!!, 30.0f, 30.0f)
        if (--this.attackTime == 0) {
            if (!hasLineOfSight) return

            val dist = sqrt(targetDistSqr).toFloat() / this.attackRadius
            val power = Mth.clamp(dist, 0.1f, 1.0f)
            this.plantEntity.performRangedAttack(this.target!!, power)
            this.attackTime =
                Mth.floor(dist * (this.attackIntervalMax - this.attackIntervalMin) + this.attackIntervalMin)
        } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(
                Mth.lerp(
                    sqrt(targetDistSqr) / this.attackRadius,
                    this.attackIntervalMin.toDouble(),
                    this.attackIntervalMax.toDouble()
                )
            )
        }
    }
}