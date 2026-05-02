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
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.function.Consumer

/**
 * Represents a water storage component with adjustable capacity.
 * Used for the watering can.
 *
 * @property storedWater Current amount of water stored.
 * @property maxCapacity Maximum capacity of the storage.
 */
data class StoredWater(
    val storedWater: Int = 0,
    val maxCapacity: Int = 32
) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        consumer.accept(Component.translatable("component.stored_water", storedWater).withStyle(ChatFormatting.AQUA))
    }

    fun storagePercentage(): Float = (storedWater.toFloat() / maxCapacity)
    fun hasWater(water: Int = 1): Boolean = storedWater >= water
    fun isFull(): Boolean = storedWater == maxCapacity
    fun addWater(water: Int = 4): StoredWater = copy(storedWater = (storedWater + water).coerceAtMost(maxCapacity))
    fun removeWater(water: Int = 1): StoredWater = copy(storedWater = (storedWater - water).coerceAtLeast(0))

    companion object {
        val CODEC: Codec<StoredWater> = RecordCodecBuilder.create { inst ->
            inst.group(
                Codec.INT.fieldOf("stored_water").forGetter { it.storedWater.coerceIn(0,it.maxCapacity) },
                Codec.INT.fieldOf("max_capacity").forGetter { it.maxCapacity }
            ).apply(inst, ::StoredWater)
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, StoredWater> = StreamCodec.composite(
            ByteBufCodecs.INT,
            StoredWater::storedWater,
            ByteBufCodecs.INT,
            StoredWater::maxCapacity,
            ::StoredWater
        )
    }
}