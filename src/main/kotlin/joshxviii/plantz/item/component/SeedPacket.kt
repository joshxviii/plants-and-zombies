package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.Optional
import java.util.function.Consumer

/**
 * SeedPacket component used in the [SeedPacketItem]
 * Saves an entityId that is used to spawn in different plant types.
 */
data class SeedPacket(
    val entityId: Identifier? = null
) : TooltipProvider {

    override fun addToTooltip(
        context: TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        if (entityId != null) {
            consumer.accept(CommonComponents.space())
            consumer.accept(
                Component.translatable("entity.${entityId.namespace}.${entityId.path}")
                    .withStyle(ChatFormatting.GRAY)
            )
        }
    }

    companion object {
        val CODEC: Codec<SeedPacket> = RecordCodecBuilder.create { inst ->
            inst.group(
                Identifier.CODEC.optionalFieldOf("id").forGetter { Optional.ofNullable(it.entityId) }
            ).apply(inst) { idOpt: Optional<Identifier> ->
                SeedPacket(entityId = idOpt.orElse(null))
            }
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, SeedPacket> = StreamCodec.composite(
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), { Optional.ofNullable(it.entityId) },
            { idOpt: Optional<Identifier> ->
                SeedPacket(entityId = idOpt.orElse(null))
            }
        )
    }
}