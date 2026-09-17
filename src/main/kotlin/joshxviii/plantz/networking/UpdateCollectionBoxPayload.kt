package joshxviii.plantz.networking

import joshxviii.plantz.MailboxData
import joshxviii.plantz.block.entity.MailCollectionBoxEntity
import joshxviii.plantz.inventory.MailCollectionBoxMenu
import joshxviii.plantz.pazResource
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.networking.v1.context.PacketContext
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

data class UpdateCollectionBoxPayload(
    val collectionBoxPos: BlockPos,
    val selectedMailbox: BlockPos?
) : CustomPacketPayload {
    companion object {
        val ID: CustomPacketPayload.Type<UpdateCollectionBoxPayload> = CustomPacketPayload.Type(pazResource("update_collection_box"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, UpdateCollectionBoxPayload> =
            StreamCodec.composite(
                BlockPos.STREAM_CODEC, UpdateCollectionBoxPayload::collectionBoxPos,
                ByteBufCodecs.optional(BlockPos.STREAM_CODEC), { Optional.ofNullable(it.selectedMailbox) },
                { pos, targetPos -> UpdateCollectionBoxPayload(pos, targetPos.getOrNull()) }
            )

        fun handleUpdateCollectionBoxPacket(payload: UpdateCollectionBoxPayload, context: ServerPlayNetworking.Context) {
            val player = context.player()
            val level = player.level()

            val be = level.getBlockEntity(payload.collectionBoxPos) as? MailCollectionBoxEntity ?: return
            if (player.containerMenu !is MailCollectionBoxMenu) return

            be.selectedMailbox = payload.selectedMailbox
            be.setChanged()
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}