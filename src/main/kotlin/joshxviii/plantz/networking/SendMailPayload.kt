package joshxviii.plantz.networking

import joshxviii.plantz.block.MailboxState
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.inventory.MailboxMenu
import joshxviii.plantz.pazResource
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.ItemStack

data class SendMailPayload(val targetPos: BlockPos) : CustomPacketPayload {

    companion object {
        val ID: CustomPacketPayload.Type<SendMailPayload> = CustomPacketPayload.Type(pazResource("send_mail"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SendMailPayload> =
            StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                SendMailPayload::targetPos,
                ::SendMailPayload
            )

        fun handleSendMailPacket(payload: SendMailPayload, context: ServerPlayNetworking.Context) {
            val player = context.player()
            val level = player.level()
            val targetPos = payload.targetPos

            val menu = player.containerMenu as? MailboxMenu ?: return

            val senderBE = level.getBlockEntity(menu.blockPos) as? MailboxBlockEntity ?: return
            val targetBE = level.getBlockEntity(targetPos) as? MailboxBlockEntity ?: return

            var stack = menu.mailSlot.item.copy()
            if (stack.isEmpty) return

            var success = false

            for (i in 0 until 5) {
                val existing = targetBE.getItem(i)

                if (existing.isEmpty) {
                    targetBE.setItem(i, stack)
                    success = true
                    break
                } else if (ItemStack.isSameItem(existing, stack) && existing.count < existing.maxStackSize) {
                    val space = existing.maxStackSize - existing.count
                    val toAdd = minOf(space, stack.count)
                    existing.grow(toAdd)
                    stack.shrink(toAdd)
                    targetBE.setItem(i, existing)

                    if (stack.isEmpty) {
                        success = true
                        break
                    }
                }
            }

            if (success) {
                menu.mailSlot.set(ItemStack.EMPTY)
                menu.broadcastChanges()
                senderBE.setChanged()
                targetBE.setChanged()
                targetBE.updateMailboxState(MailboxState.HAS_MAIL)
                level.playSound(null, menu.blockPos, SoundEvents.UI_LOOM_SELECT_PATTERN, SoundSource.BLOCKS, 0.3f, 1.2f)
            }
            else ServerPlayNetworking.send(player, SendMailResponsePayload(Component.translatable("container.plantz.mailbox_full")))
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}