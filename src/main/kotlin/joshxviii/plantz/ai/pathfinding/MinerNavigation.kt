package joshxviii.plantz.ai.pathfinding

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation
import net.minecraft.world.level.Level
import net.minecraft.world.level.pathfinder.NodeEvaluator
import net.minecraft.world.level.pathfinder.Path
import net.minecraft.world.level.pathfinder.PathFinder
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator
import kotlin.compareTo
import kotlin.io.path.moveTo

class MinerNavigation(entity: PathfinderMob, level: Level) : GroundPathNavigation(entity, level) {

    override fun createPathFinder(maxVisitedNodes: Int): PathFinder {
        this.nodeEvaluator = MinerNodeEvaluator()
        val pathFinder = PathFinder(this.nodeEvaluator, maxVisitedNodes)
        return pathFinder
    }

    override fun createPath(
        targets: Set<BlockPos>,
        radiusOffset: Int,
        above: Boolean,
        reachRange: Int,
        maxPathLength: Float
    ): Path? {
        val testPath = super.createPath(targets, radiusOffset, above, reachRange, maxPathLength)
        val canReachTarget = testPath?.endNode?.let {
            targets.last().distSqr(Vec3i(it.x, it.y, it.z)) <= 2.25
        }?: false

        (this.nodeEvaluator as? MinerNodeEvaluator)?.userMiner = (!canReachTarget && mob.isAggressive)

        return testPath
    }
}