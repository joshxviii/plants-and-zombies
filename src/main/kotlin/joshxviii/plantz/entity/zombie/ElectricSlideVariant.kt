package joshxviii.plantz.entity.zombie

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

enum class ElectricSlideVariant(val suffix: String, val id: Int) : StringRepresentable {
    SLIDE("", 0),
    BOOGALOO("boogaloo", 1);

    override fun getSerializedName(): String = suffix

    companion object {
        fun getDefault(): ElectricSlideVariant = SLIDE
        fun pickRandomVariant(): ElectricSlideVariant = entries.random()

        val CODEC: Codec<ElectricSlideVariant> = StringRepresentable.fromEnum(ElectricSlideVariant::values)
        private val BY_ID: IntFunction<ElectricSlideVariant> = ByIdMap.continuous(ElectricSlideVariant::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, ElectricSlideVariant> = ByteBufCodecs.idMapper<ElectricSlideVariant>(BY_ID, ElectricSlideVariant::id)
    }
}