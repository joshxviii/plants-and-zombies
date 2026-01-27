package joshxviii.plantz.ai.pathfinding

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation
import net.minecraft.world.level.Level
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.level.pathfinder.Path
import net.minecraft.world.level.pathfinder.PathFinder

class MinerNavigation(entity: PathfinderMob, level: Level) : GroundPathNavigation(entity, level) {

    override fun createPathFinder(maxVisitedNodes: Int): PathFinder {
        this.nodeEvaluator = MinerNodeEvaluator()
        val pathFinder = PathFinder(this.nodeEvaluator, maxVisitedNodes)
        return pathFinder
    }

    override fun canUpdatePath(): Boolean {
        return true
    }

    override fun createPath(
        targets: Set<BlockPos>,
        radiusOffset: Int,
        above: Boolean,
        reachRange: Int,
        maxPathLength: Float
    ): Path? {
        val testPath = super.createPath(targets, radiusOffset, above, reachRange, maxPathLength)
        val canBreakBlocks = (mob.level() as? ServerLevel)?.gameRules?.get(GameRules.MOB_GRIEFING)?: false
        val canReachTarget = testPath?.endNode?.let {
            targets.last().distSqr(Vec3i(it.x, it.y, it.z)) <= 2.25
        }?: false

        (this.nodeEvaluator as? MinerNodeEvaluator)?.userMiner = (!canReachTarget && mob.isAggressive && canBreakBlocks)

        return testPath
    }
}