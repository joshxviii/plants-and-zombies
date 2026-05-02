package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import joshxviii.plantz.entity.Sun
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.function.Consumer

data class StoredSun(
    val storedSun: Int = 0,
    val maxCapacity: Int = 512
) : TooltipProvider {
    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        consumer.accept(Component.translatable("component.stored_sun", storedSun).withStyle(ChatFormatting.AQUA))
    }

    fun storagePercentage(): Float = (storedSun.toFloat() / maxCapacity)
    fun hasSun(sun: Int = 1): Boolean = storedSun >= sun

    companion object {
        val CODEC: Codec<StoredSun> = RecordCodecBuilder.create { inst ->
            inst.group(
                Codec.INT.fieldOf("stored_sun").forGetter { it.storedSun.coerceIn(0,it.maxCapacity) },
                Codec.INT.fieldOf("max_capacity").forGetter { it.maxCapacity }
            ).apply(inst, ::StoredSun)
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, StoredSun> = StreamCodec.composite(
            ByteBufCodecs.INT,
            StoredSun::storedSun,
            ByteBufCodecs.INT,
            StoredSun::maxCapacity,
            ::StoredSun
        )
    }
}