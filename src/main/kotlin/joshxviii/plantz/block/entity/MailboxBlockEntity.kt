package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.MailboxBlock
import joshxviii.plantz.block.MailboxBlock.Companion.FACING
import joshxviii.plantz.block.MailboxBlock.Companion.STATE
import joshxviii.plantz.block.MailboxState
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.BarrelBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class MailboxBlockEntity(
    worldPosition: BlockPos, blockState: BlockState,
    val color : DyeColor = DyeColor.WHITE,
) : BlockEntity(
    PazBlocks.MAILBOX_ENTITY, worldPosition, blockState
) {
    val hasMail : Boolean = false
    val open : Boolean = false

    companion object {

    }

    fun tryToGetMail() {
        if (blockState.getValue(STATE)== MailboxState.INACTIVE) {
            updateMailboxState(blockState, MailboxState.HAS_MAIL)
        }
        else if (blockState.getValue(STATE)== MailboxState.HAS_MAIL) {
            updateMailboxState(blockState, MailboxState.EJECTING)
        }
        else if (blockState.getValue(STATE)== MailboxState.EJECTING) {
            updateMailboxState(blockState, MailboxState.INACTIVE)
        }
    }

    private fun updateMailboxState(state: BlockState, newState: MailboxState) {
        this.level!!.setBlock(this.blockPos, state.setValue(STATE, newState), 3)
    }

    private fun playSound(state: BlockState, event: SoundEvent) {
        val direction = (state.getValue(FACING) as Direction).unitVec3i
        val x = this.worldPosition.x + 0.5 + direction.x / 2.0
        val y = this.worldPosition.y + 0.5 + direction.y / 2.0
        val z = this.worldPosition.z + 0.5 + direction.z / 2.0
        this.level!!.playSound(
            null, x, y, z, event, SoundSource.BLOCKS, 0.5f, this.level!!.getRandom().nextFloat() * 0.1f + 0.9f
        )
    }

}