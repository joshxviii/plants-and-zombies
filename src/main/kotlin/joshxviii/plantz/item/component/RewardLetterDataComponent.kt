package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import joshxviii.plantz.raid.WaveType
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier

data class RewardLetterDataComponent(
    val wave: WaveType,
    val lootTables: List<Identifier>,
) {
    companion object {
        val CODEC: Codec<RewardLetterDataComponent> = RecordCodecBuilder.create { instance ->
            instance.group(
                WaveType.CODEC.fieldOf("wave").forGetter(RewardLetterDataComponent::wave),
                Identifier.CODEC.listOf().fieldOf("loot_tables").forGetter(RewardLetterDataComponent::lootTables),
            ).apply(instance, ::RewardLetterDataComponent)
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, RewardLetterDataComponent> = StreamCodec.composite(
            WaveType.STREAM_CODEC, RewardLetterDataComponent::wave,
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), RewardLetterDataComponent::lootTables,
            ::RewardLetterDataComponent,
        )
    }
}
