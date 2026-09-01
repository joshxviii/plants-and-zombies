package joshxviii.plantz.ai.goal

import joshxviii.plantz.*
import joshxviii.plantz.PazBlocks.PLANTZ_FLAG
import joshxviii.plantz.PazBlocks.PLANTZ_FLAG_POI
import joshxviii.plantz.block.entity.FlagBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.pathfinder.Path
import net.minecraft.world.phys.HitResult
import java.util.*
import kotlin.math.max
import kotlin.math.min

class DestroyFlagGoal(
    val mob: PathfinderMob,
    val searchRange: Int = 64
) : Goal(){
    companion object {
        const val SEARCH_COOLDOWN = 20
    }

    var path: Path? = null
    var ticksUntilNextPathRecalculation = 0
    var searchCooldown: Int = SEARCH_COOLDOWN
    var targetFlagPos: BlockPos? = null

    init {
        flags = EnumSet.of<Flag>(Flag.MOVE, Flag.LOOK)
    }

    override fun canUse(): Boolean {
        mob.target?.let { if(it.isAlive && mob.withinAttackRange(it)) return false }
        if (searchCooldown > 0) {
            searchCooldown--
            if (searchCooldown <= 0) {
                targetFlagPos = findNearbyFlag()
                val flagPos = targetFlagPos ?: return false
                path = mob.getNavigation().createPath(flagPos, 0)
            }
        }
        return targetFlagPos!=null && !mob.hasEffect(PazEffects.HYPNOTIZE) && canSeeFlag()
    }

    override fun canContinueToUse(): Boolean {
        mob.target?.let { if(it.isAlive && mob.withinAttackRange(it)) return false }
        val flagPos = targetFlagPos?: return false
        val isValid = mob.level().getBlockState(flagPos).`is`(PLANTZ_FLAG)
        return isValid && canSeeFlag()
    }

    override fun stop() {
        mob.isAggressive = false
        targetFlagPos = null
        mob.navigation.stop()
    }

    override fun start() {
        mob.isAggressive = true
        targetFlagPos?.center?.let {
            mob.moveControl.setWantedPosition(it.x, it.y, it.z, 1.0)
        }
        mob.navigation.moveTo(path, 1.0)
    }

    override fun tick() {
        super.tick()
        val flagPos = targetFlagPos ?: return

        mob.getLookControl().lookAtBlockPos(flagPos)
        ticksUntilNextPathRecalculation = max(ticksUntilNextPathRecalculation - 1, 0)
        if (ticksUntilNextPathRecalculation <= 0 && mob.getRandom().nextFloat() < 0.05f) {

            ticksUntilNextPathRecalculation = 4 + mob.getRandom().nextInt(7)
            val targetDistanceSqr: Double = flagPos.distSqr(mob.blockPosition())
            if (targetDistanceSqr > 1024.0) ticksUntilNextPathRecalculation += 10
            else if (targetDistanceSqr > 256.0) ticksUntilNextPathRecalculation += 5

            if (!mob.getNavigation().moveToBlockPos(flagPos, 1.0)) ticksUntilNextPathRecalculation += 15

            ticksUntilNextPathRecalculation = adjustedTickDelay(ticksUntilNextPathRecalculation)
        }

        val reach = mob.attackRange()
        if (flagPos.distSqr(mob.blockPosition()) > reach) flagPos.center.let { mob.moveControl.setWantedPosition(it.x, it.y, it.z, 1.0) }
        if (flagPos.distSqr(mob.blockPosition()) < if (mob.navigation.path.canReachTarget(flagPos)) reach else reach*reach) {
            if (!mob.swinging) {
                damageFlag(flagPos)
                mob.swing(mob.usedItemHand)
            }
        }
    }

    fun damageFlag(flagPos: BlockPos) {
        val flag = mob.level().getBlockEntity(flagPos) as? FlagBlockEntity ?: return
        val damage = mob.getAttribute(Attributes.ATTACK_DAMAGE)?.value?.toFloat()?: return
        flag.hurt(damage)
    }

    fun findNearbyFlag(): BlockPos? {
        searchCooldown = SEARCH_COOLDOWN
        val followRange = mob.getAttribute(Attributes.FOLLOW_RANGE)?.value?.toInt()
        val poiManager = (mob.level() as ServerLevel).poiManager
        val flagPoi: BlockPos? = poiManager.findClosest(
            { it.value() == PLANTZ_FLAG_POI },
            mob.blockPosition(),
            min(searchRange, followRange?:8),
            Occupancy.ANY
        ).orElse(null)
        return flagPoi
    }

    fun canSeeFlag(): Boolean {
        targetFlagPos?.let {
            return mob.level().clip(ClipContext(mob.eyePosition, it.center, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob)).type == HitResult.Type.MISS
        }
        return false
    }
}
