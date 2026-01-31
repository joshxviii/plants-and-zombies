package joshxviii.plantz.networking

import joshxviii.plantz.pazResource
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs

data class SendMailResponsePayload(
    val message: Component
) : CustomPacketPayload {

    companion object {
        val ID: CustomPacketPayload.Type<SendMailResponsePayload> = CustomPacketPayload.Type(pazResource("send_mail_response"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SendMailResponsePayload> =
            StreamCodec.composite(
                ComponentSerialization.STREAM_CODEC,
                SendMailResponsePayload::message,
                ::SendMailResponsePayload
            )
    }

    override fun type() = ID
}