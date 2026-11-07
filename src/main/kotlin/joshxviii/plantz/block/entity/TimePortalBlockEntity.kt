package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class TimePortalBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(PazBlocks.TIME_PORTAL_ENTITY, worldPosition, blockState) {
    var tickCount: Int = 0

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: TimePortalBlockEntity) {
            blockEntity.tickCount++
        }
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
    }

    fun playSound(event: SoundEvent, pitch: Float = 1.0f) {
        level?.playSound(null, blockPos, event, SoundSource.BLOCKS, 1.0f, pitch)
    }
}