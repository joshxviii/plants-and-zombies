package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block.getId
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.math.floor

class FlagBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(PazBlocks.FLAG_BLOCK_ENTITY, worldPosition, blockState) {

    companion object {
        const val MAX_HEALTH: Float = 300f
        const val HEALTH_RESET_TIME = 100
    }

    var health : Float = MAX_HEALTH
    var resetTime : Int = 0

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        if (level.isClientSide) return
        if (resetTime > 0) {
            resetTime--
            if (resetTime == 0) {
                health = MAX_HEALTH
                level.destroyBlockProgress(0, pos, -1)
            }
        }

        if (health <= 0) {
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, getId(state))
            if (blockState.`is`(PazBlocks.PLANTZ_FLAG)) {
                val item = ItemEntity(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), PazBlocks.BRAINZ_FLAG.asItem().defaultInstance)
                level.addFreshEntity(item)
            }
            level.destroyBlock(pos, false)
        } else if (health < MAX_HEALTH) level.destroyBlockProgress(0, pos, healthToDestroyProgress())
    }

    fun hurt(amount: Float) {
        health -= amount
        health = health.coerceAtLeast(0f)
        resetTime = HEALTH_RESET_TIME
        setChanged()
        val l = level?: return
        l.playSound(null, blockPos, blockState.soundType.hitSound, SoundSource.BLOCKS, 1.0f, l.random.nextFloat() * 0.2f + 0.6f)
    }

    private fun healthToDestroyProgress(): Int = floor((1f - (health / MAX_HEALTH)) * 10f).toInt()

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putFloat("Health", health)
        output.putInt("ResetTime", resetTime)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        health = input.getFloatOr("Health", MAX_HEALTH)
        resetTime = input.getIntOr("ResetTime", 0)
    }
}