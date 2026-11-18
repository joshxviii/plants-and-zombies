package joshxviii.plantz.networking

import joshxviii.plantz.entity.blueprint_machines.LawnMower
import joshxviii.plantz.pazResource
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class ReleaseLawnMowerPayload(val mowerId: Int) : CustomPacketPayload {
    companion object {
        val ID: CustomPacketPayload.Type<ReleaseLawnMowerPayload> = CustomPacketPayload.Type(pazResource("release_lawn_mower"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ReleaseLawnMowerPayload> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ReleaseLawnMowerPayload::mowerId,
            ::ReleaseLawnMowerPayload
        )

        fun handleReleaseLawnMowerPacket(payload: ReleaseLawnMowerPayload, context: ServerPlayNetworking.Context) {
            val player = context.player()
            val mower = player.level().getEntity(payload.mowerId) as? LawnMower ?: return
            if (mower.grabberId == player.id) mower.release()
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
