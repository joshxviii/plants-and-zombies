package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import joshxviii.plantz.PazEntities
import joshxviii.plantz.item.SeedPacketItem
import joshxviii.plantz.pazResource
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.*
import java.util.function.Consumer

/**
 * SeedPacket component used in the [SeedPacketItem]
 * Saves an entityId that is used to spawn in different plant types.
 */
data class SeedPacket(
    val entityId: Identifier = pazResource("sunflower"),
    private val sunCost: Int? = null
) : TooltipProvider {

    fun getSunCost(): Int {
        return sunCost ?: PazEntities.getSunCostFromType(BuiltInRegistries.ENTITY_TYPE.getOptional(entityId).orElse(null))
    }

    override fun addToTooltip(
        context: TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        consumer.accept(CommonComponents.space())
        consumer.accept(
            Component.translatable("entity.${entityId.namespace}.${entityId.path}")
                .withStyle(ChatFormatting.GRAY)
        )
        consumer.accept(Component.translatable("component.sun_cost", getSunCost()).withStyle(ChatFormatting.GOLD))
    }

    companion object {
        val CODEC: Codec<SeedPacket> = RecordCodecBuilder.create { inst ->
            inst.group(
                Identifier.CODEC.fieldOf("id").forGetter { it.entityId },
                Codec.INT.optionalFieldOf("sun_cost").forGetter { Optional.ofNullable(it.sunCost) }
            ).apply(inst) { entityId, sunCost -> SeedPacket(entityId, sunCost.orElse(null)) }
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, SeedPacket> = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            SeedPacket::entityId,
            ByteBufCodecs.INT.apply(ByteBufCodecs::optional),
            { Optional.ofNullable(it.sunCost) },
            {entityId, sunCost -> SeedPacket(entityId, sunCost.orElse(null)) }
        )
    }
}