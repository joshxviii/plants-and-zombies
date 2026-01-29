package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.item.equipment.Equippable
import java.util.Optional
import java.util.function.Consumer

class BlocksProjectileDamage(
    val slot: EquipmentSlotGroup = EquipmentSlotGroup.HEAD,
    val breakChance: Float = 0.15f,
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

        val CODEC: Codec<BlocksProjectileDamage> = RecordCodecBuilder.create { inst ->
            inst.group(
                EquipmentSlotGroup.CODEC.fieldOf("slot").forGetter { it.slot },
                Codec.FLOAT.fieldOf("break_chance").forGetter { it.breakChance }
            ).apply(inst, ::BlocksProjectileDamage)
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, BlocksProjectileDamage> = StreamCodec.composite(
            EquipmentSlotGroup.STREAM_CODEC,
            BlocksProjectileDamage::slot,
            ByteBufCodecs.FLOAT,
            BlocksProjectileDamage::breakChance,
            ::BlocksProjectileDamage
        )

    }
}