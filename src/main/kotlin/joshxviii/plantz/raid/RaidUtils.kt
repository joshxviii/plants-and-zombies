package joshxviii.plantz.raid

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.storage.loot.LootTable

data class CompletedWave(
    val type: WaveType,
    val bossType: BossType? = null,
) {
    companion object {
        private val RECORD_CODEC = RecordCodecBuilder.create<CompletedWave> { instance ->
            instance.group(
                WaveType.CODEC.fieldOf("type").forGetter { it.type },
                BossType.CODEC.optionalFieldOf("boss_type").forGetter { java.util.Optional.ofNullable(it.bossType) },
            ).apply(instance) { type, bossType -> CompletedWave(type, bossType.orElse(null)) }
        }
        val CODEC: Codec<CompletedWave> = Codec.either(WaveType.CODEC, RECORD_CODEC).xmap(
            { either -> either.map({ CompletedWave(it) }, { it }) },
            { Either.right(it) },
        )
        val STREAM_CODEC: StreamCodec<ByteBuf, CompletedWave> = StreamCodec.composite(
            WaveType.STREAM_CODEC, { it.type },
            ByteBufCodecs.optional(BossType.STREAM_CODEC), { java.util.Optional.ofNullable(it.bossType) },
            { type, bossType -> CompletedWave(type, bossType.orElse(null)) },
        )
    }
}

data class HeroMailReward(
    val lootTable: ResourceKey<LootTable>,
    val bossWave: Boolean,
)

interface GardenHeroRewards {
    fun `plantz$getWaveList`(): MutableList<CompletedWave>
    fun `plantz$setWaveList`(value: MutableList<CompletedWave>)
    companion object {
        fun collectRewards(player: ServerPlayer): MutableList<HeroMailReward> {
            player as GardenHeroRewards
            val rewards = player.`plantz$getWaveList`().mapIndexed { waveNumber, wave ->
                HeroMailReward(wave.type.lootTableFn(waveNumber, player.seenCredits), wave.bossType != null)
            }.toMutableList()
            return rewards
        }
    }
}