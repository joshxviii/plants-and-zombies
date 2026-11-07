package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.function.Consumer

data class BlockBreakChanceComponent(val chance: Float) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        val percent = DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.ROOT)).format(chance * 100.0f)
        consumer.accept(
            Component.translatable("component.plantz.block_break_chance", percent)
                .withStyle(ChatFormatting.DARK_GRAY)
        )
    }

    companion object {
        val CODEC: Codec<BlockBreakChanceComponent> = Codec.floatRange(0.0f, 1.0f).xmap(
            ::BlockBreakChanceComponent,
            BlockBreakChanceComponent::chance
        )

        val STREAM_CODEC = ByteBufCodecs.FLOAT.map(
            ::BlockBreakChanceComponent,
            BlockBreakChanceComponent::chance
        )
    }
}
