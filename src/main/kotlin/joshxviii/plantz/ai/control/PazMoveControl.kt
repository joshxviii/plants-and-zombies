package joshxviii.plantz.ai.control

import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.control.FlyingMoveControl
import net.minecraft.world.entity.ai.control.MoveControl

private const val MIN_TURN_DISTANCE_SQR = 0.0625 // 0.25 blocks

private fun MoveControl<*>.isWantedPosVertical(mob: Mob): Boolean {
    if (!hasWanted()) return false
    val xd = wantedX - mob.x
    val zd = wantedZ - mob.z
    return xd * xd + zd * zd < MIN_TURN_DISTANCE_SQR
}

/** Move control that only moves vertically (jumping / swimming up) when the wanted position is directly above or below. */
open class PazMoveControl(mob: Mob) : MoveControl<Mob>(mob) {
    override fun tick() {
        val vertical = isWantedPosVertical(mob)
        val yRot = mob.yRot
        super.tick()
        if (vertical) {
            mob.yRot = yRot
            mob.zza = 0f
        }
    }
}

/** Flying move control that only ascends / descends when the wanted position is directly above or below. */
open class PazFlyingMoveControl(mob: Mob, maxTurn: Int, hoversInPlace: Boolean) : FlyingMoveControl<Mob>(mob, maxTurn, hoversInPlace) {
    override fun tick() {
        val vertical = isWantedPosVertical(mob)
        val yRot = mob.yRot
        super.tick()
        if (vertical) {
            mob.yRot = yRot
            mob.zza = 0f
        }
    }
}
