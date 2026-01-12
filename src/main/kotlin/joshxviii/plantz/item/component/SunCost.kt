package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.function.Consumer

data class SunCost(
    val sunCost: Int = 0
) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        consumer.accept(Component.translatable("component.sun_cost", sunCost).withStyle(ChatFormatting.GOLD))
    }

    companion object {
        lateinit var TYPE: DataComponentType<SunCost>

        val CODEC = Codec.INT.xmap(
            { SunCost(it) },
            { it.sunCost }
        )

        val STREAM_CODEC = ByteBufCodecs.INT.map(
            { SunCost(it) },
            { it.sunCost }
        )
    }
}