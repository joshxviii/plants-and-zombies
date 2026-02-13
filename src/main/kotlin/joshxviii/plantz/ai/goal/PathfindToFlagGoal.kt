package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazBlocks.PLANTZ_FLAG
import joshxviii.plantz.PazBlocks.PLANTZ_FLAG_POI
import joshxviii.plantz.PazEntities.PLANT_TEAM
import joshxviii.plantz.lookAtBlockPos
import joshxviii.plantz.moveToBlockPos
import joshxviii.plantz.raid.getZombieRaids
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.pathfinder.Path
import java.util.*
import kotlin.math.max
import kotlin.math.min

class PathfindToFlagGoal(
    val mob: PathfinderMob,
    var targetFlagPos: BlockPos? = null
) : Goal(){
    companion object {
        const val SEARCH_COOLDOWN = 20
    }

    var path: Path? = null
    var ticksUntilNextPathRecalculation = 0
    var navCooldown: Int = SEARCH_COOLDOWN

    init {
        flags = EnumSet.of<Flag>(Flag.MOVE, Flag.LOOK)
    }

    override fun canUse(): Boolean {
        if (navCooldown > 0) { navCooldown--
            if (navCooldown <= 0) {
                navCooldown = SEARCH_COOLDOWN
                val level = mob.level() as ServerLevel
                val zombieRaid = level.getZombieRaids().getNearbyRaid(mob.blockPosition(), 99999)
                if (zombieRaid != null && mob is Zombie) zombieRaid.joinRaid(level, mob)
                targetFlagPos = zombieRaid?.center
            }
        }
        val targetPos = targetFlagPos ?: return false
        path = mob.getNavigation().createPath(targetPos, 0)
        val isValid = mob.level().getBlockState(targetPos).`is`(PLANTZ_FLAG)
        return targetPos.distSqr(mob.blockPosition()) > 96 && isValid && !mob.isAggressive && path != null && mob.team != PLANT_TEAM
    }

    override fun stop() {
        //targetFlagPos = null
        //mob.isAggressive = false
        mob.navigation.stop()
    }

    override fun start() {
        //mob.isAggressive = true
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

    }

}
