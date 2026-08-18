package joshxviii.plantz.entity.zombie

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

enum class GargantuarVariant(val suffix: String, val id: Int) : StringRepresentable {
    GARG("", 0),
    PIRATE("pirate", 1);

    override fun getSerializedName(): String = suffix

    companion object {
        fun getDefault(): GargantuarVariant = GARG
        fun pickRandomVariant(): GargantuarVariant = entries.random()

        val CODEC: Codec<GargantuarVariant> = StringRepresentable.fromEnum(GargantuarVariant::values)
        private val BY_ID: IntFunction<GargantuarVariant> = ByIdMap.continuous(GargantuarVariant::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, GargantuarVariant> = ByteBufCodecs.idMapper<GargantuarVariant>(BY_ID, GargantuarVariant::id)
    }
}