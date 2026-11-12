package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.Optional
import java.util.UUID
import java.util.function.Consumer


//TODO: rather than saving the almanac data in the item stack, save it either on the player, or probably a Almanac Manager,
// A manager would allow other players to see the almanac data of other players. Have to find a way for books to be "owned" by a player tho
data class AlmanacDataComponent(
    val seedPackets: List<ItemStack> = emptyList(),
    val tab: Int = 0,
    val page: Int = 0,
    val owner: UUID? = null,
) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {}

    companion object {
        const val MAX_SEED_PACKETS = 512

        val CODEC: Codec<AlmanacDataComponent> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(MAX_SEED_PACKETS).optionalFieldOf("seed_packets", emptyList())
                    .forGetter(AlmanacDataComponent::seedPackets),
                Codec.intRange(0, 255).optionalFieldOf("tab", 0).forGetter(AlmanacDataComponent::tab),
                Codec.intRange(0, 255).optionalFieldOf("page", 0).forGetter(AlmanacDataComponent::page),
                UUIDUtil.CODEC.optionalFieldOf("owner").forGetter { Optional.ofNullable(it.owner) }
            ).apply(instance) { seedPackets, tab, page, owner ->
                AlmanacDataComponent(seedPackets, tab, page, owner.orElse(null))
            }
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, AlmanacDataComponent> = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(MAX_SEED_PACKETS)),
            AlmanacDataComponent::seedPackets,
            ByteBufCodecs.VAR_INT,
            AlmanacDataComponent::tab,
            ByteBufCodecs.VAR_INT,
            AlmanacDataComponent::page,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional),
            { data: AlmanacDataComponent -> Optional.ofNullable(data.owner) },
            { seedPackets, tab, page, owner -> AlmanacDataComponent(seedPackets, tab, page, owner.orElse(null)) }
        )
    }
}

enum class AlmanacTab {
    SEED_PACKETS,
    ZOMBIES,
    INFO
}