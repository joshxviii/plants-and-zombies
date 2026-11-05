package joshxviii.plantz.raid

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import joshxviii.plantz.PazEffects
import joshxviii.plantz.raid.ZombieRaid.Companion.GARDEN_HERO_EFFECT_DURATION
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.level.saveddata.maps.MapItemSavedData.type
import net.minecraft.world.level.storage.loot.LootTable
import java.util.Optional

data class CompletedWave(
    val type: WaveType,
    val pos: BlockPos? = null,
    val boss: BossType? = null,
    val timeElapsed: Int? = null,
) {
    companion object {
        val CODEC: Codec<CompletedWave> = RecordCodecBuilder.create(
            { instance -> instance.group(
                WaveType.CODEC.fieldOf("type").forGetter { it.type },
                BlockPos.CODEC.optionalFieldOf("wave_pos").forGetter { Optional.ofNullable(it.pos) },
                Codec.INT.optionalFieldOf("time_elapsed").forGetter { Optional.ofNullable(it.timeElapsed) },
                BossType.CODEC.optionalFieldOf("boss_type").forGetter { Optional.ofNullable(it.boss) },
            ).apply(instance) { type, wavePos, timeElapsed, bossType -> CompletedWave(type, wavePos.orElse(null), bossType.orElse(null), timeElapsed.orElse(null)) } }
        )
        val STREAM_CODEC: StreamCodec<ByteBuf, CompletedWave> = StreamCodec.composite(
            WaveType.STREAM_CODEC, { it.type },
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC), { Optional.ofNullable(it.pos) },
            ByteBufCodecs.optional(ByteBufCodecs.INT), { Optional.ofNullable(it.timeElapsed) },
            ByteBufCodecs.optional(BossType.STREAM_CODEC), { Optional.ofNullable(it.boss) },
            { type, wavePos, timeElapsed, bossType -> CompletedWave(type, wavePos.orElse(null), bossType.orElse(null), timeElapsed.orElse(null)) },
        )
    }
}

interface GardenHeroRewards {
    fun `plantz$getWaveList`(): MutableList<CompletedWave>
    fun `plantz$setWaveList`(value: MutableList<CompletedWave>)
    companion object {
        fun collectRewards(player: ServerPlayer): MutableList<CompletedWave> {
            player as GardenHeroRewards
            return player.`plantz$getWaveList`()
        }
        fun setPlayerRewards(player: ServerPlayer, waves: List<CompletedWave>) {
            player as GardenHeroRewards
            val effect = MobEffectInstance(PazEffects.GARDEN_HERO, GARDEN_HERO_EFFECT_DURATION, (waves.size-1).coerceAtLeast(0), false, true)
            val filter =  waves.map { CompletedWave(
                type = it.type,
                boss = it.boss,
            ) }.toMutableList()
            player.`plantz$setWaveList`(filter)
            player.addEffect(effect)
        }
    }
}