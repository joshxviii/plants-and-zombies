package joshxviii.plantz.networking

import joshxviii.plantz.pazResource
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

object OpenOptionsScreenPayload : CustomPacketPayload {
    val ID: CustomPacketPayload.Type<OpenOptionsScreenPayload> = CustomPacketPayload.Type(pazResource("open_options_screen"))
    val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, OpenOptionsScreenPayload> = StreamCodec.unit(OpenOptionsScreenPayload)

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
