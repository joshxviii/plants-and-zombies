package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import joshxviii.plantz.PazConfig
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import java.util.function.Consumer

/**
 * Sun storage component with adjustable capacity.
 * Used for Sun Battery.
 *
 * @property storedSun Current amount of sun stored.
 */
data class StoredSun(
    val storedSun: Int = 0,
) : TooltipProvider {

    val max: Int
        get() = PazConfig.SUN_BATTERY_MAX

    override fun addToTooltip(
        context: Item.TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        consumer.accept(Component.translatable("component.stored_sun.desc").withStyle(ChatFormatting.GRAY))
        consumer.accept(Component.translatable("component.stored_sun", storedSun, max).withStyle(ChatFormatting.YELLOW))
    }

    fun storagePercentage(): Float = (storedSun.toFloat() / max)
    fun hasSun(sun: Int = 1): Boolean = storedSun >= sun
    fun hasRoomForSun(sun: Int = 1): Boolean = storedSun + sun <= max
    fun isFull(): Boolean = storedSun == max
    fun addSun(sun: Int = 1): StoredSun = copy(storedSun = (storedSun + sun).coerceAtMost(max))
    fun removeSun(sun: Int = 1): StoredSun = copy(storedSun = (storedSun - sun).coerceAtLeast(0))

    companion object {
        val CODEC: Codec<StoredSun> = RecordCodecBuilder.create { inst ->
            inst.group(
                Codec.INT.fieldOf("stored_sun").forGetter { it.storedSun.coerceIn(0, it.max) },
            ).apply(inst, ::StoredSun)
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, StoredSun> = StreamCodec.composite(
            ByteBufCodecs.INT,
            StoredSun::storedSun,
            ::StoredSun
        )
    }
}