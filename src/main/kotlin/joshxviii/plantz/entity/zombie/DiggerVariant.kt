package joshxviii.plantz.entity.zombie

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import joshxviii.plantz.SeasonalEvents.isHalloween
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

enum class DiggerVariant(val suffix: String, val id: Int) : StringRepresentable {
    DIGGER("", 0),
    EXPLORER("explorer", 1);

    override fun getSerializedName(): String = suffix

    companion object {
        fun getDefault(): DiggerVariant = DIGGER
        fun pickRandomVariant(): DiggerVariant = entries.random()

        fun pickForBiome(isDesert: Boolean, random: RandomSource): DiggerVariant {
            return when {
                isDesert -> EXPLORER
                else -> DIGGER
            }
        }

        val CODEC: Codec<DiggerVariant> = StringRepresentable.fromEnum(DiggerVariant::values)
        private val BY_ID: IntFunction<DiggerVariant> = ByIdMap.continuous(DiggerVariant::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, DiggerVariant> = ByteBufCodecs.idMapper<DiggerVariant>(BY_ID, DiggerVariant::id)
    }
}