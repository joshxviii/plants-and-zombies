package joshxviii.plantz.entity.zombie

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

enum class AllStarVariant(val suffix: String, val id: Int) : StringRepresentable {
    ALL_STAR("", 0),
    GIGA("giga", 1);

    override fun getSerializedName(): String = suffix

    companion object {
        fun getDefault(): AllStarVariant = ALL_STAR
        fun pickRandomVariant(): AllStarVariant = entries.random()

        val CODEC: Codec<AllStarVariant> = StringRepresentable.fromEnum(AllStarVariant::values)
        private val BY_ID: IntFunction<AllStarVariant> = ByIdMap.continuous(AllStarVariant::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, AllStarVariant> = ByteBufCodecs.idMapper<AllStarVariant>(BY_ID, AllStarVariant::id)
    }
}