package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueOutput

class GardenGnomeBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(PazBlocks.GARDEN_GNOME_ENTITY, worldPosition, blockState) {
    companion object {
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)

    }
}