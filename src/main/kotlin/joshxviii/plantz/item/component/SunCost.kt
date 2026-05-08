package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import joshxviii.plantz.PazConfig
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.function.Consumer

data class SunCost(
    private val sunCost: Int = 0,
) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        val type = components.get(DataComponents.ENTITY_DATA)?.type()
        consumer.accept(Component.translatable("component.sun_cost", getSunCost(type)).withStyle(ChatFormatting.GOLD))
    }

    fun getSunCost(forType: EntityType<*>?): Int {
        return PazConfig.getSunCost(forType).let { if(it == -1) sunCost else it }
    }

    companion object {

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