package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.name
import joshxviii.plantz.raid.WaveType
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.function.Consumer

data class RewardLetterDataComponent(
    val wave: WaveType,
    val lootTables: List<Identifier>,
) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        return
        val data = components.get(PazComponents.REWARD_LETTER_DATA) ?: return
        val brains = data.wave.brains
        consumer.accept(Component.translatable("component.plantz.reward_letter.brains", brains.first, brains.last)
            .withStyle(ChatFormatting.GRAY))
    }

    companion object {
        val CODEC: Codec<RewardLetterDataComponent> = RecordCodecBuilder.create { instance ->
            instance.group(
                WaveType.CODEC.fieldOf("wave").forGetter(RewardLetterDataComponent::wave),
                Identifier.CODEC.listOf().fieldOf("loot_tables").forGetter(RewardLetterDataComponent::lootTables),
            ).apply(instance, ::RewardLetterDataComponent)
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, RewardLetterDataComponent> = StreamCodec.composite(
            WaveType.STREAM_CODEC, RewardLetterDataComponent::wave,
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), RewardLetterDataComponent::lootTables,
            ::RewardLetterDataComponent,
        )
    }
}
