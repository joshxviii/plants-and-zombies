package joshxviii.plantz.ai.goal

import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.pathfinder.Path
import java.util.*
import kotlin.math.max
import kotlin.math.sqrt

class NavigateToTargetGoal(
    val mob: PathfinderMob,
    val speedModifier: Double = 1.0,
    val keepAwayDistance: Double = 0.0,
    val alwaysFaceTarget: Boolean = false,
    val followingTargetEvenIfNotSeen: Boolean = false,
) : Goal(){
    companion object {
        private const val COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L
        private const val DISTANCE_TOLERANCE = 0.75
    }

    var path: Path? = null
    var lastCanUseCheck = 0L
    private var ticksUntilNextPathRecalculation = 0
    private var pathedTargetX = 0.0
    private var pathedTargetY = 0.0
    private var pathedTargetZ = 0.0
    private var strafingTime = -1
    private var strafingClockwise = true

    init {
        flags = EnumSet.of(Flag.MOVE, Flag.LOOK)
    }

    override fun canUse(): Boolean {
        val time = mob.level().gameTime
        if (time - lastCanUseCheck < COOLDOWN_BETWEEN_CAN_USE_CHECKS) return false
        lastCanUseCheck = time

        val target = mob.target ?: return false
        if (!target.isAlive) return false

        if (keepAwayDistance > 0.0) {
            path = createPathToDesiredPosition(target)
            return true
        }
        path = createPathToDesiredPosition(target)
        return path != null || mob.isWithinMeleeAttackRange(target)
    }

    override fun canContinueToUse(): Boolean {
        val target = mob.target ?: return false
        if (!target.isAlive) return false
        if (!followingTargetEvenIfNotSeen && mob.navigation.isDone) {
            if (keepAwayDistance > 0.0) {
                val dist = mob.distanceTo(target)
                if (dist < keepAwayDistance - DISTANCE_TOLERANCE ||
                    dist > keepAwayDistance + DISTANCE_TOLERANCE) {
                    return true
                }
            }
            return false
        }
        if (!mob.isWithinHome(target.blockPosition())) return false
        return when (target) {
            is Player -> !target.isSpectator && !target.isCreative
            else -> true
        }
    }

    override fun start() {
        path?.let { mob.navigation.moveTo(it, speedModifier) }
        mob.isAggressive = true
        ticksUntilNextPathRecalculation = 0
    }
    
    override fun stop() {
        val target = mob.target
        if (target != null && !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) mob.target = null
        mob.isAggressive = false
        mob.navigation.stop()
        path = null
    }

    override fun requiresUpdateEveryTick(): Boolean = true


    override fun tick() {
        super.tick()

        val target = mob.target ?: return
        mob.lookControl.setLookAt(target, 30.0f, 30.0f)

        if (keepAwayDistance > 0.0) {
            val distance = mob.distanceTo(target)

            if (alwaysFaceTarget) {
                if (distance <= keepAwayDistance + DISTANCE_TOLERANCE) {
                    if (distance in keepAwayDistance - DISTANCE_TOLERANCE..keepAwayDistance + DISTANCE_TOLERANCE) mob.navigation.stop()
                    ++strafingTime
                    if (strafingTime % 20 == 0 && mob.random.nextFloat()<0.3) strafingClockwise = !strafingClockwise
                    mob.moveControl.strafe(-0.7f, if (strafingClockwise) 0.7f else -0.7f)
                    mob.lookAt(target, 30.0f, 30.0f)
                    return
                }
            }
            else if (distance in keepAwayDistance - DISTANCE_TOLERANCE..keepAwayDistance + DISTANCE_TOLERANCE){
                mob.navigation.stop()
                return
            }

            recalculatePath(target)

            val distSqr = distance * distance
            when {
                distSqr > 1024.0 -> ticksUntilNextPathRecalculation += 10
                distSqr > 256.0  -> ticksUntilNextPathRecalculation += 5
            }

            val desired = getKeepAwayPosition(target, keepAwayDistance)
            val success = mob.navigation.moveTo(desired.x, desired.y, desired.z, speedModifier)
            if (!success) ticksUntilNextPathRecalculation += 15
            ticksUntilNextPathRecalculation = adjustedTickDelay(ticksUntilNextPathRecalculation)
            return
        }

        recalculatePath(target)

        if (!mob.navigation.moveTo(target, speedModifier)) {
            ticksUntilNextPathRecalculation += 15
        }
        ticksUntilNextPathRecalculation = adjustedTickDelay(ticksUntilNextPathRecalculation)
    }

    private fun recalculatePath(target: LivingEntity) {
        strafingTime = -1
        ticksUntilNextPathRecalculation = max(ticksUntilNextPathRecalculation - 1, 0)

        val needsRecalc =
            (followingTargetEvenIfNotSeen || mob.sensing.hasLineOfSight(target)) &&
            ticksUntilNextPathRecalculation <= 0 &&
            (pathedTargetX == 0.0 && pathedTargetY == 0.0 && pathedTargetZ == 0.0 ||
            target.distanceToSqr(pathedTargetX, pathedTargetY, pathedTargetZ) >= 1.0 ||
            mob.random.nextFloat() < 0.05f)

        if (!needsRecalc) return

        pathedTargetX = target.x
        pathedTargetY = target.y
        pathedTargetZ = target.z

        ticksUntilNextPathRecalculation = 4 + mob.random.nextInt(7)
        val distSqr = mob.distanceToSqr(target)
        when {
            distSqr > 1024.0 -> ticksUntilNextPathRecalculation += 10
            distSqr > 256.0  -> ticksUntilNextPathRecalculation += 5
        }
    }

    private fun createPathToDesiredPosition(target: net.minecraft.world.entity.LivingEntity): Path? {
        return if (keepAwayDistance > 0.0) {
            val desired = getKeepAwayPosition(target, keepAwayDistance)
            mob.navigation.createPath(desired.x, desired.y, desired.z, 0)
        } else  mob.navigation.createPath(target, 0)
    }

    private fun getKeepAwayPosition(
        target: LivingEntity,
        distance: Double
    ): net.minecraft.world.phys.Vec3 {
        val mobPos = mob.position()
        val targetPos = target.position()
        val delta = mobPos.subtract(targetPos)
        val len = sqrt(delta.x * delta.x + delta.y * delta.y + delta.z * delta.z)

        return if (len < 1.0E-4) targetPos.add(distance, 0.0, 0.0)
        else {
            val scale = distance / len
            targetPos.add(delta.x * scale, delta.y * scale, delta.z * scale)
        }
    }
}
