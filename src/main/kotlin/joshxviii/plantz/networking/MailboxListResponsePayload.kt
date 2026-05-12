package joshxviii.plantz.networking

import joshxviii.plantz.MailboxData
import joshxviii.plantz.pazResource
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level

data class MailboxListResponsePayload(
    val dimension: ResourceKey<Level>,
    val mailboxes: List<MailboxData>
) : CustomPacketPayload {
    companion object {
        val ID: CustomPacketPayload.Type<MailboxListResponsePayload> = CustomPacketPayload.Type(pazResource("mailbox_list_response"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MailboxListResponsePayload> = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            MailboxListResponsePayload::dimension,
            MailboxData.STREAM_CODEC.apply(ByteBufCodecs.list()),
            MailboxListResponsePayload::mailboxes,
            ::MailboxListResponsePayload
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}