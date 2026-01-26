package joshxviii.plantz.ai.pathfinding

import joshxviii.plantz.PazTags
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.pathfinder.*

class MinerNodeEvaluator : WalkNodeEvaluator() {
    var userMiner = false

    override fun getPathType(context: PathfindingContext, x: Int, y: Int, z: Int): PathType {
        val pos = BlockPos(x, y, z)
        var type = super.getPathType(context, x, y, z)

        if(userMiner && isBreakable(context, pos)) type = PathType.BREACH

        return type
    }

    private fun isBreakable(context: PathfindingContext, pos: BlockPos): Boolean {
        val blockState = context.getBlockState(pos)
        return blockState.`is`(PazTags.BlockTags.MINER_BREAKABLE)
    }

    override fun getPathType(mob: Mob, pos: BlockPos): PathType {
        return super.getPathType(mob, pos)
    }

    override fun getNeighbors(neighbors: Array<out Node>, pos: Node): Int {
        return super.getNeighbors(neighbors, pos)
    }
}