package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazTags
import joshxviii.plantz.ai.pathfinding.MinerNodeEvaluator
import joshxviii.plantz.canReachTarget
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.level.pathfinder.PathType
import kotlin.math.abs
import kotlin.math.min

class MineBlocksToTargetGoal(
    val miner: PathfinderMob,
    private val maxBreakRange: Float = 2.25f,
    private val verticalBlockRange: Int = 3,
) : Goal() {

    companion object {
        private const val BREAK_COOLDOWN: Int = 8
    }

    private val level = miner.level() as ServerLevel
    var breakTime: Int = 0
    var breakCooldownTime: Int = 0
    var lastBreakProgress: Int = -1
    private var breakTargetPos: BlockPos? = null
    set(value) {
        if (value.equalPos(field)) return
        breakTime = 0
        field = value
    }

    override fun start() {
        super.start()
        this.breakTime = 0
    }

    override fun stop() {
        super.stop()
        val targetBlock = breakTargetPos
        if(targetBlock!=null) level.destroyBlockProgress(miner.id, targetBlock, -1)
        breakTargetPos = null
    }

    override fun canUse(): Boolean {
        if (--breakCooldownTime > 0) return false
        if (miner.isDeadOrDying) return false
        if (level.gameRules.get(GameRules.MOB_GRIEFING)==false) return false
        val targetPos = miner.target?.blockPosition() ?: return false

        breakTargetPos?.let {
            if (!miner.navigation.path.canReachTarget(targetPos)) return true
        }

        val minerPos = miner.blockPosition()
        if ( abs(minerPos.y - targetPos.y)>1
            && abs(minerPos.x - targetPos.x)<2
            && abs(minerPos.z - targetPos.z)<2
        ) {
            for (i in 0..min(abs(minerPos.y - targetPos.y), verticalBlockRange)) {
                val testBlock = if (minerPos.y < targetPos.y) minerPos.above(i) else minerPos.below(i)
                //level.levelEvent(LevelEvent.PARTICLES_ELECTRIC_SPARK, testBlock, 15)

                if (isBlockWithinRange(testBlock) && isBlockMineable(testBlock)) {
                    breakTargetPos = testBlock
                    return true
                }
            }
        }

        val path = miner.navigation.path ?: return false

        for (i in path.nextNodeIndex until min(path.nextNodeIndex + 2, path.nodeCount)) {
             val node = path.getNode(i)

             // Check vertical positions (middle, top then bottom)
            listOf(node.y+1, node.y+2, if(targetPos.y>miner.y) null else node.y ).forEach { y ->
                 if (y==null) return@forEach
                 val testBlock = BlockPos(node.x, y, node.z)
                 //level.levelEvent(LevelEvent.PARTICLES_ELECTRIC_SPARK, testBlock, 15)

                 if (isBlockWithinRange(testBlock) && isBlockMineable(testBlock)) {
                     breakTargetPos = testBlock
                     return true
                 }
             }
         }

        return false
    }

    override fun tick() {
        super.tick()
        val targetBlock = breakTargetPos ?: return
        if (!isBlockMineable(targetBlock)) return
        val targetBlockState = level.getBlockState(targetBlock)
        if (breakTime>=0) {
            //level.levelEvent(LevelEvent.PARTICLES_ELECTRIC_SPARK, targetBlock, 15)
            if (!miner.swinging) {
                level.playSound(null, targetBlock, targetBlockState.soundType.hitSound, SoundSource.BLOCKS, 0.3f,
                    level.random.nextFloat() * 0.2f + 0.5f)
                miner.swing(miner.usedItemHand)
            }
        }
        breakTime++

        val blockBreakTime = getBlockBreakTime(breakTargetPos)

        val progress = if (blockBreakTime>0) (this.breakTime * 10 / blockBreakTime) else -1
        if (progress != this.lastBreakProgress) {
            level.destroyBlockProgress(miner.id, targetBlock, progress)
            this.lastBreakProgress = progress
        }

        if (progress >= 10) {// break block
            breakCooldownTime = BREAK_COOLDOWN
            breakTargetPos = null
            breakTime=0
            level.destroyBlock(
                targetBlock,
                !targetBlockState.requiresCorrectToolForDrops() || miner.mainHandItem.isCorrectToolForDrops(targetBlockState),
                miner
            )
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, targetBlock, Block.getId(targetBlockState))
            (miner.navigation.nodeEvaluator as? MinerNodeEvaluator)?.userMiner = false
            miner.navigation.recomputePath()
        }
    }

    private fun BlockPos?.equalPos(pos: BlockPos?): Boolean {
        if (this == null || pos == null) return false
        return this.toMutable() == pos.toMutable()
    }

    private fun getBlockBreakTime(pos: BlockPos?): Int {
        if (pos == null) return -1
        val blockState = level.getBlockState(pos)
        val itemSpeed = miner.mainHandItem.getDestroySpeed(blockState)
        val modifier = if (miner.mainHandItem.isCorrectToolForDrops(blockState)) 0.5f else 1.0f
        val destroyTime = blockState.getDestroySpeed(level, pos)
        val finalBreakTime = ((destroyTime / itemSpeed)*75*modifier).toInt()
        return finalBreakTime
    }

    private fun isBlockWithinRange(pos: BlockPos?): Boolean {
        if (pos == null) return false
        val distance = miner.distanceToSqr(pos.x.toDouble(), miner.y, pos.z.toDouble())
        return distance < maxBreakRange
    }

    private fun isBlockMineable(pos: BlockPos?) : Boolean {
        if (pos == null) return false
        val blockState = miner.level().getBlockState(pos)
        return (blockState.`is`(PazTags.BlockTags.MINER_BREAKABLE))
    }

}