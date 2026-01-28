package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.function.Consumer

class BlocksProjectileDamage(
    val breakChance: Float = 0.15f
) : TooltipProvider {

    override fun addToTooltip(
        context: TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        consumer.accept(Component.translatable("component.blocks_damage.desc").withStyle(ChatFormatting.GRAY))
        consumer.accept(Component.translatable("component.blocks_damage", (breakChance * 100).toInt()).withStyle(ChatFormatting.BLUE))
    }

    companion object {
        lateinit var TYPE: DataComponentType<BlocksProjectileDamage>

        val CODEC = Codec.FLOAT.xmap(
            { BlocksProjectileDamage(it) },
            { it.breakChance }
        )

        val STREAM_CODEC = ByteBufCodecs.FLOAT.map(
            { BlocksProjectileDamage(it) },
            { it.breakChance }
        )
    }
}