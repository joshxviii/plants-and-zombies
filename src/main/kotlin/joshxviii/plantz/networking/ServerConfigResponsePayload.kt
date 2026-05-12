package joshxviii.plantz.networking

import joshxviii.plantz.MailboxData
import joshxviii.plantz.pazResource
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import java.util.stream.Stream

data class ServerConfigResponsePayload(
    val json: String
) : CustomPacketPayload {
    companion object {
        val ID: CustomPacketPayload.Type<ServerConfigResponsePayload> = CustomPacketPayload.Type(pazResource("server_config_response"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ServerConfigResponsePayload> =
            StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                ServerConfigResponsePayload::json,
                ::ServerConfigResponsePayload,
            )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}