package joshxviii.plantz.ai

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

/**
 * The Plant's state used for Animations and Behavior.
 */
enum class PlantState(val title: String, val id: Int) : StringRepresentable {
    IDLE("idle", 0),
    ACTION("action", 1),
    COOLDOWN("cooldown", 2);
    override fun getSerializedName(): String = this.title
    companion object {
        val CODEC: Codec<PlantState> = StringRepresentable.fromEnum(PlantState::values)
        private val BY_ID: IntFunction<PlantState> = ByIdMap.continuous(PlantState::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, PlantState> = ByteBufCodecs.idMapper<PlantState>(BY_ID, PlantState::id)
    }
}