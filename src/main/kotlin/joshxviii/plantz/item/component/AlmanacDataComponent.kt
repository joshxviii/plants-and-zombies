package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.ChatFormatting
import net.minecraft.core.UUIDUtil
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.Optional
import java.util.UUID
import java.util.function.Consumer

data class AlmanacDataComponent(
    val tab: Int = 0,
    val page: Int = 0,
    val owner: UUID? = null,
    val ownerName: String = "",
) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        consumer.accept(
            if (owner != null && ownerName.isNotEmpty())
                Component.translatable("component.plantz.almanac", ownerName).withStyle(ChatFormatting.DARK_GRAY)
            else Component.empty()
        )
    }

    companion object {
        val CODEC: Codec<AlmanacDataComponent> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.intRange(0, 255).optionalFieldOf("tab", 0).forGetter(AlmanacDataComponent::tab),
                Codec.intRange(0, 255).optionalFieldOf("page", 0).forGetter(AlmanacDataComponent::page),
                UUIDUtil.CODEC.optionalFieldOf("owner").forGetter { Optional.ofNullable(it.owner) },
                Codec.STRING.optionalFieldOf("owner_name", "").forGetter(AlmanacDataComponent::ownerName)
            ).apply(instance) { tab, page, owner, name ->
                AlmanacDataComponent(tab, page, owner.orElse(null), name)
            }
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, AlmanacDataComponent> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            AlmanacDataComponent::tab,
            ByteBufCodecs.VAR_INT,
            AlmanacDataComponent::page,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional),
            { data: AlmanacDataComponent -> Optional.ofNullable(data.owner) },
            ByteBufCodecs.STRING_UTF8,
            AlmanacDataComponent::ownerName,
            { tab, page, owner, name -> AlmanacDataComponent(tab, page, owner.orElse(null), name) }
        )
    }
}

enum class AlmanacTab {
    SEED_PACKETS,
    ZOMBIES,
    INFO
}
