package joshxviii.plantz.entity.plant

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import joshxviii.plantz.PazEntities
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.ItemStack
import java.util.function.IntFunction

enum class MarigoldColor(val id: Int, val title: String, val color: Int) : StringRepresentable {
    NONE(0, "none", -1),
    ORANGE(1, "orange", -1),
    MAGENTA(2, "magenta", -1),
    LIGHT_BLUE(3, "light_blue", -1),
    YELLOW(4, "yellow", -1),
    LIME(5, "lime", -1),
    PINK(6, "pink", -1),
    GRAY(7, "gray", -1),
    LIGHT_GRAY(8, "light_gray", -1),
    CYAN(9, "cyan", -1),
    PURPLE(10, "purple", -1),
    BLUE(11, "blue", -1),
    BROWN(12, "brown", -1),
    GREEN(13, "green", -1),
    RED(14, "red", -1),
    BLACK(15, "black", -1),
    WHITE(16, "white", -1),
    RAINBOW(17, "rainbow", -1);

    override fun getSerializedName(): String = this.title
    companion object {

        fun seedPackets() : List<ItemStack> = MarigoldColor.entries.drop(1).map { color -> SeedPacketItem.stackFor(PazEntities.MARIGOLD, CompoundTag().also { it.putString("color", color.title) }) }

        fun getDefault(): MarigoldColor = NONE
        fun pickRandomVariant(): MarigoldColor = entries.random()
        fun fromName(name: String?): MarigoldColor = entries.firstOrNull { it.title == name } ?: NONE

        val CODEC: Codec<MarigoldColor> = StringRepresentable.fromEnum(MarigoldColor::values)
        private val BY_ID: IntFunction<MarigoldColor> = ByIdMap.continuous(MarigoldColor::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, MarigoldColor> = ByteBufCodecs.idMapper<MarigoldColor>(BY_ID, MarigoldColor::id)
    }
}