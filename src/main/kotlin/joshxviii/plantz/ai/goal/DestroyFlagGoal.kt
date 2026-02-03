package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazBlocks.PLANTZ_FLAG
import joshxviii.plantz.PazBlocks.PLANTZ_FLAG_POI
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy
import net.minecraft.world.entity.ai.village.poi.PoiType
import java.util.*
import kotlin.math.min

class DestroyFlagGoal(
    val mob: PathfinderMob,
    val searchRange: Int = 64
) : Goal(){
    companion object {
        const val SEARCH_COOLDOWN = 60
    }

    var searchCooldown: Int = SEARCH_COOLDOWN
    var targetFlagPos: BlockPos? = null

    init {
        //flags = EnumSet.of<Flag>(Flag.MOVE)
    }

    override fun canUse(): Boolean {
        if (searchCooldown > 0) {
            searchCooldown--
            if (searchCooldown <= 0) targetFlagPos = findNearbyFlag()
        }
        return targetFlagPos!=null && !mob.isAggressive
    }

    override fun canContinueToUse(): Boolean {
        val targetFlag = targetFlagPos
        val isValid = mob.level().getBlockState(targetFlagPos!!).`is`(PLANTZ_FLAG)
        return targetFlag!=null && isValid
    }

    override fun stop() {
        targetFlagPos = null
        mob.navigation.stop()
    }

    override fun start() {

    }

    override fun tick() {
        super.tick()
        val targetFlag = targetFlagPos ?: return
        if (!mob.isPathFinding) {
            mob.navigation.moveTo(targetFlag.x.toDouble(),targetFlag.y.toDouble(),targetFlag.z.toDouble(), 1.0)
        }
    }



    fun findNearbyFlag(): BlockPos? {
        searchCooldown = SEARCH_COOLDOWN
        val followRange = mob.getAttribute(Attributes.FOLLOW_RANGE)?.value?.toInt()
        val poiManager = (mob.level() as ServerLevel).poiManager
        val flagPoi: BlockPos? = poiManager.findClosest(
            { p: Holder<PoiType> -> p.value() == PLANTZ_FLAG_POI },
            mob.blockPosition(),
            min(searchRange, followRange?:8),
            Occupancy.ANY
        ).orElse(null)
        return flagPoi
    }
}
