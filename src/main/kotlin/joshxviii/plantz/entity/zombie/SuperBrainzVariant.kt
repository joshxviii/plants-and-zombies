package joshxviii.plantz.entity.zombie

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

enum class SuperBrainzVariant(val suffix: String, val id: Int, val beamColor: Int) : StringRepresentable {
    SUPER("", 0, 0xFF79E1),
    TOXIC("toxic", 1, 0x9CD62E),
    ELECTRO("electro", 2, 0xBDFDF5),;

    override fun getSerializedName(): String = suffix

    companion object {
        fun getDefault(): SuperBrainzVariant = SUPER
        fun pickRandomVariant(): SuperBrainzVariant = entries.random()

        val CODEC: Codec<SuperBrainzVariant> = StringRepresentable.fromEnum(SuperBrainzVariant::values)
        private val BY_ID: IntFunction<SuperBrainzVariant> = ByIdMap.continuous(SuperBrainzVariant::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, SuperBrainzVariant> = ByteBufCodecs.idMapper<SuperBrainzVariant>(BY_ID, SuperBrainzVariant::id)
    }
}