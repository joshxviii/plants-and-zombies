package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class MailboxBlockEntity(
    worldPosition: BlockPos, blockState: BlockState
) : BlockEntity(
    PazBlocks.MAILBOX_ENTITY, worldPosition, blockState
) {

}