package joshxviii.plantz.ai

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

/**
 * Zombie's state used for Animations and Behavior.
 */
enum class ZombieState(val title: String, val id: Int) : StringRepresentable {
    IDLE("idle", 0),
    EMERGING("emerging", 1);
    override fun getSerializedName(): String = this.title
    companion object {
        val CODEC: Codec<ZombieState> = StringRepresentable.fromEnum(ZombieState::values)
        private val BY_ID: IntFunction<ZombieState> = ByIdMap.continuous(ZombieState::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, ZombieState> = ByteBufCodecs.idMapper<ZombieState>(BY_ID, ZombieState::id)
    }
}