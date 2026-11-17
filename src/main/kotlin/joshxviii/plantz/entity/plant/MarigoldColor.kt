package joshxviii.plantz.entity.plant

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

enum class MarigoldColor(val id: Int, val title: String, val color: Int) : StringRepresentable {
    NONE(0, "none", -1),
    WHITE(1, "white", -1),
    ORANGE(2, "orange", -1),
    MAGENTA(3, "magenta", -1),
    LIGHT_BLUE(4, "light_blue", -1),
    YELLOW(5, "yellow", -1),
    LIME(6, "lime", -1),
    PINK(7, "pink", -1),
    GRAY(8, "gray", -1),
    LIGHT_GRAY(9, "light_gray", -1),
    CYAN(10, "cyan", -1),
    PURPLE(11, "purple", -1),
    BLUE(12, "blue", -1),
    BROWN(13, "brown", -1),
    GREEN(14, "green", -1),
    RED(15, "red", -1),
    BLACK(16, "black", -1),
    RAINBOW(17, "rainbow", -1);

    override fun getSerializedName(): String = this.title
    companion object {

        fun getDefault(): MarigoldColor = NONE
        fun pickRandomVariant(): MarigoldColor = entries.random()
        fun fromName(name: String?): MarigoldColor = entries.firstOrNull { it.title == name } ?: NONE

        val CODEC: Codec<MarigoldColor> = StringRepresentable.fromEnum(MarigoldColor::values)
        private val BY_ID: IntFunction<MarigoldColor> = ByIdMap.continuous(MarigoldColor::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, MarigoldColor> = ByteBufCodecs.idMapper<MarigoldColor>(BY_ID, MarigoldColor::id)
    }
}