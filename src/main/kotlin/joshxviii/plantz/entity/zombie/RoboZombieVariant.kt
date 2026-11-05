package joshxviii.plantz.entity.zombie

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

enum class RoboZombieVariant(val suffix: String, val id: Int) : StringRepresentable {
    ROBO("", 0),
    GENERAL("general", 1);

    override fun getSerializedName(): String = suffix

    companion object {
        fun getDefault(): RoboZombieVariant = ROBO
        fun pickRandomVariant(): RoboZombieVariant = entries.random()

        val CODEC: Codec<RoboZombieVariant> = StringRepresentable.fromEnum(RoboZombieVariant::values)
        private val BY_ID: IntFunction<RoboZombieVariant> = ByIdMap.continuous(RoboZombieVariant::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, RoboZombieVariant> = ByteBufCodecs.idMapper<RoboZombieVariant>(BY_ID, RoboZombieVariant::id)
    }
}