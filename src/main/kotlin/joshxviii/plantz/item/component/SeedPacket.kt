package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.function.Consumer
import joshxviii.plantz.item.SeedPacketItem

/**
 * SeedPacket component used in the [SeedPacketItem]
 * Saves an entityId that is used to spawn in different plant types.
 */
data class SeedPacket(
    val entityId: Identifier?
) : TooltipProvider {
    override fun addToTooltip(
        context: TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        val id = entityId ?: return
        val translationKey = "entity.${id.namespace}.${id.path}"
        consumer.accept(CommonComponents.space())
        consumer.accept(Component.translatable(translationKey).withStyle(ChatFormatting.GRAY))
    }

    companion object {
        val CODEC: Codec<SeedPacket> =
            com.mojang.serialization.codecs.RecordCodecBuilder.create { inst ->
                inst.group(
                    Identifier.CODEC.optionalFieldOf("entity").forGetter { java.util.Optional.ofNullable(it.entityId) }
                ).apply(inst) { opt -> SeedPacket(opt.orElse(null)) }
            }

        val STREAM_CODEC: StreamCodec<ByteBuf, SeedPacket> = object : StreamCodec<ByteBuf, SeedPacket> {
            override fun encode(buf: ByteBuf, value: SeedPacket) {
                val id = value.entityId
                buf.writeBoolean(id != null)
                if (id != null) Identifier.STREAM_CODEC.encode(buf, id)
            }

            override fun decode(buf: ByteBuf): SeedPacket {
                val has = buf.readBoolean()
                return if (has) SeedPacket(Identifier.STREAM_CODEC.decode(buf))
                else SeedPacket(null)
            }
        }
    }
}