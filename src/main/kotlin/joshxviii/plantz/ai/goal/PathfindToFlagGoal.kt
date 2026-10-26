package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazBlocks.PLANTZ_FLAG
import joshxviii.plantz.PazEffects
import joshxviii.plantz.lookAtBlockPos
import joshxviii.plantz.raid.ZombieRaid
import joshxviii.plantz.raid.getZombieRaids
import joshxviii.plantz.withinAttackRange
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import java.util.*

class PathfindToFlagGoal(
    val mob: PathfinderMob,
    var targetFlagPos: BlockPos? = null,
    var minimumDistance: Double = 4.0
) : Goal() {

    companion object {
        const val SEARCH_COOLDOWN = 40
        const val REPATH_BASE = 20
    }

    private var searchCooldown = SEARCH_COOLDOWN
    private var repathCooldown = 0

    init {
        flags = EnumSet.of(Flag.MOVE)
    }

    override fun canUse(): Boolean {
        mob.target?.let {
            if (it.isAlive && mob.withinAttackRange(it, 4.0)) return false
        }
        if (mob.isAggressive || mob.hasEffect(PazEffects.HYPNOTIZE)) return false

        if (searchCooldown-- <= 0) {
            searchCooldown = SEARCH_COOLDOWN
            val level = mob.level() as? ServerLevel ?: return false
            val raid = level.getZombieRaids()
                .getNearbyRaid(mob.blockPosition(), ZombieRaid.RAID_DISTANCE)
            targetFlagPos = raid?.center
        }

        val target = targetFlagPos ?: return false
        return mob.level().getBlockState(target).`is`(PLANTZ_FLAG) && target.distSqr(mob.blockPosition()) > minimumDistance * minimumDistance
    }

    override fun canContinueToUse(): Boolean {
        mob.target?.let {
            if (it.isAlive && mob.withinAttackRange(it)) return false
        }
        val target = targetFlagPos ?: return false
        if (!mob.level().getBlockState(target).`is`(PLANTZ_FLAG)) return false
        if (mob.isAggressive || mob.hasEffect(PazEffects.HYPNOTIZE)) return false

        return target.distSqr(mob.blockPosition()) > minimumDistance * minimumDistance
                && !mob.navigation.isDone
    }

    private fun pathTarget(flagPos: BlockPos): BlockPos {
        val from = mob.blockPosition()
        val distSq = flagPos.distSqr(from)
        val maxRange = when {
            distSq > 1024.0 -> 16
            distSq > 256.0  -> 24
            else            -> 48
        }
        if (distSq <= maxRange.toDouble() * maxRange) return flagPos

        val dist = kotlin.math.sqrt(distSq)
        val t = maxRange / dist
        return BlockPos.containing(
            from.x + (flagPos.x - from.x) * t,
            from.y + (flagPos.y - from.y) * t,
            from.z + (flagPos.z - from.z) * t
        )
    }

    private fun moveTowardFlag(flagPos: BlockPos) {
        val target = pathTarget(flagPos)
        val path = mob.navigation.createPath(target, 1) ?: return
        mob.navigation.moveTo(path, 1.0)
    }

    override fun start() {
        val target = targetFlagPos ?: return
        moveTowardFlag(target)
        repathCooldown = staggeredRepathDelay()
    }

    override fun stop() {
        mob.navigation.stop()
    }

    override fun tick() {
        val flagPos = targetFlagPos ?: return
        if (mob.tickCount % 3 == 0) mob.lookControl.lookAtBlockPos(flagPos)

        if (repathCooldown > 0) {
            repathCooldown--
            return
        }

        val distSqr = flagPos.distSqr(mob.blockPosition())
        repathCooldown = when {
            distSqr > 1024.0 -> staggeredRepathDelay() + 30
            distSqr > 256.0 -> staggeredRepathDelay() + 15
            else -> staggeredRepathDelay()
        }

        val nav = mob.navigation
        if (nav.isDone || nav.path == null) {
            moveTowardFlag(flagPos)
        }
    }

    // spread repathing across ticks so 40 zombies don't pathfind on the same tick.
    private fun staggeredRepathDelay(): Int = REPATH_BASE + (mob.id and 15) // 20..35

    override fun requiresUpdateEveryTick(): Boolean = true
}
