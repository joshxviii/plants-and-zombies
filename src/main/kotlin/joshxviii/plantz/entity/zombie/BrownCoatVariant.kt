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

enum class BrownCoatVariant(val suffix: String, val id: Int) : StringRepresentable {
    BROWN("", 0),
    BALLOON("balloon", 1),
    SNOW("snow", 2),
    DESERT("desert", 3),
    BUCCANEER("buccaneer", 4),
    VAMPIRE("vampire", 5);

    override fun getSerializedName(): String = suffix

    companion object {
        fun getDefault(): BrownCoatVariant = BROWN

        fun pickRandomVariant(): BrownCoatVariant = entries.random()

        fun pickForBiome(isSnowy: Boolean, isDesert: Boolean, isBeach: Boolean, isShipwreck: Boolean, random: RandomSource): BrownCoatVariant {
            return when {
                isShipwreck -> BUCCANEER
                isSnowy -> if (random.nextFloat() < 0.75f) SNOW else BROWN
                isDesert -> if (random.nextFloat() < 0.75f) DESERT else BROWN
                isBeach -> if (random.nextFloat() < 0.6f) BUCCANEER else BROWN
                isHalloween() -> if (random.nextFloat() < 0.08f) VAMPIRE else BROWN
                else -> BROWN
            }
        }

        val CODEC: Codec<BrownCoatVariant> = StringRepresentable.fromEnum(BrownCoatVariant::values)
        private val BY_ID: IntFunction<BrownCoatVariant> = ByIdMap.continuous(BrownCoatVariant::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO)
        val STREAM_CODEC: StreamCodec<ByteBuf, BrownCoatVariant> = ByteBufCodecs.idMapper<BrownCoatVariant>(BY_ID, BrownCoatVariant::id)
    }
}
