package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazItems
import joshxviii.plantz.name
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

data class BrainzAlloyCost(
    private val alloyCost: Int = 0,
) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        val type = components.get(DataComponents.ENTITY_DATA)?.type()
        consumer.accept(Component.translatable("component.item_cost", PazItems.BRAINZ_ALLOY.name(), getAlloyCost(type)).withColor(0x7B44A3))
    }

    fun getAlloyCost(forType: EntityType<*>?): Int {
        return PazConfig.getAlloyCost(forType).let { if(it == -1) alloyCost else it }
    }

    companion object {

        val CODEC = Codec.INT.xmap(
            { BrainzAlloyCost(it) },
            { it.alloyCost }
        )

        val STREAM_CODEC = ByteBufCodecs.INT.map(
            { BrainzAlloyCost(it) },
            { it.alloyCost }
        )
    }
}