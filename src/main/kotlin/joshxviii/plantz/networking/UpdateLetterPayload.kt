package joshxviii.plantz.networking

import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.inventory.LetterMenu
import joshxviii.plantz.pazResource
import joshxviii.plantz.item.component.LetterDataComponent
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.component.DataComponents
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class UpdateLetterPayload(
    val hand: Int,
    val data: LetterDataComponent
) : CustomPacketPayload {
    companion object {
        val ID: CustomPacketPayload.Type<UpdateLetterPayload> = CustomPacketPayload.Type(pazResource("update_letter"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, UpdateLetterPayload> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            UpdateLetterPayload::hand,
            LetterDataComponent.STREAM_CODEC,
            UpdateLetterPayload::data,
            ::UpdateLetterPayload
        )

        fun handleUpdateLetterPacket(payload: UpdateLetterPayload, context: ServerPlayNetworking.Context) {
            val player = context.player()
            val menu = player.containerMenu as? LetterMenu ?: return
            if (menu.handIndex != payload.hand || !menu.stillValid(player)) return
            val letter = menu.letterStack
            if (!letter.`is`(PazItems.LETTER) || letter.get(DataComponents.WRITTEN_BOOK_CONTENT) != null) return

            val current = letter.get(PazComponents.LETTER_DATA) ?: LetterDataComponent()
            if (current.sign || payload.data.pages.size > 100 || payload.data.pages.any { it.length > 1024 }) return
            val updated = payload.data.copy(
                item = current.item.copy(),
                author = if (payload.data.sign) player.name.string else current.author
            )
            letter.set(PazComponents.LETTER_DATA, updated)
            letter.remove(DataComponents.WRITABLE_BOOK_CONTENT)
            player.containerMenu.broadcastChanges()
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
