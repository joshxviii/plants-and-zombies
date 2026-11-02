package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import joshxviii.plantz.PazBlocks
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import java.util.function.Consumer

data class DoorShield(
    val doorType: Block = PazBlocks.BRAINZ_ALLOY_DOOR,
) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        //consumer.accept(Component.translatable("component.door_shield", doorType.name).withStyle(ChatFormatting.GRAY))
    }


    companion object {
        val CODEC: Codec<DoorShield> = BuiltInRegistries.BLOCK.byNameCodec().xmap(
            ::DoorShield,
            DoorShield::doorType
        )

        val STREAM_CODEC = ByteBufCodecs.registry(Registries.BLOCK).map(
            ::DoorShield,
            DoorShield::doorType
        )
    }
}