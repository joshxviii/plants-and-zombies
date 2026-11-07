package joshxviii.plantz.raid

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import joshxviii.plantz.PazEffects
import joshxviii.plantz.raid.ZombieRaid.Companion.GARDEN_HERO_EFFECT_DURATION
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.monster.zombie.Zombie
import java.util.Optional
import java.util.UUID

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

/** Persisted snapshot of a raider, used to re-link zombies after their chunk unloads or the world reloads. */
data class RaiderRecord(val uuid: UUID, val wave: Int, val health: Float, val pos: BlockPos, val isLeader: Boolean, val isBoss: Boolean) {
    companion object {
        val CODEC: Codec<RaiderRecord> = RecordCodecBuilder.create { i ->
            i.group(
                UUIDUtil.CODEC.fieldOf("uuid").forGetter(RaiderRecord::uuid),
                Codec.INT.fieldOf("wave").forGetter(RaiderRecord::wave),
                Codec.FLOAT.fieldOf("health").forGetter(RaiderRecord::health),
                BlockPos.CODEC.fieldOf("pos").forGetter(RaiderRecord::pos),
                Codec.BOOL.fieldOf("is_leader").forGetter(RaiderRecord::isLeader),
                Codec.BOOL.fieldOf("is_boss").forGetter(RaiderRecord::isBoss)
            ).apply(i, ::RaiderRecord)
        }
        fun save(zombie: Zombie, wave: Int) = RaiderRecord(zombie.uuid, wave, zombie.getEffectiveRaidHealth(), zombie.blockPosition(), zombie.isLeader(), zombie.isBoss())
        fun loadInto(zombie: Zombie, record: RaiderRecord) {
            zombie.uuid = record.uuid
            if (zombie !is ZombieRaider) return
            zombie.`plantz$setIsFromRaid`(true)
            zombie.`plantz$setIsLeader`(record.isLeader)
            zombie.`plantz$setIsBoss`(record.isBoss)
        }
    }
}

class PazZombieGroupData(
    val isRaider: Boolean = false,
    val isCountedTowardRaidHealth: Boolean = true,
    val ignoreFlag: Boolean = false,
    isBaby: Boolean = false
) : Zombie.ZombieGroupData(
    isBaby,
    false
)

interface ZombieRaider {
    fun `plantz$getIsFromRaid`(): Boolean
    fun `plantz$setIsFromRaid`(value: Boolean)
    fun `plantz$getAddsHealthToRaid`(): Boolean
    fun `plantz$setAddsHealthToRaid`(value: Boolean)
    fun `plantz$setIsLeader`(value: Boolean)
    fun `plantz$getIsLeader`(): Boolean
    fun `plantz$getIsBoss`(): Boolean
    fun `plantz$setIsBoss`(value: Boolean)
    fun `plantz$getIgnoreFlag`(): Boolean
    fun `plantz$setIgnoreFlag`(value: Boolean)
}

fun Zombie.getEffectiveRaidHealth(): Float {
    return if (this is ZombieRaider && this.`plantz$getAddsHealthToRaid`()) this.health else 0f
}

fun Zombie.setCountHealth(value: Boolean) {
    if (this is ZombieRaider) this.`plantz$setAddsHealthToRaid`(value)
}

fun Zombie.setIgnoreFlag(value: Boolean) {
    if (this is ZombieRaider) this.`plantz$setIgnoreFlag`(value)
}

fun Zombie.ignoresFlag(): Boolean = this is ZombieRaider && this.`plantz$getIgnoreFlag`()

fun Zombie.isActiveInRaid(): Boolean = this is ZombieRaider && this.`plantz$getIsFromRaid`() && this.isAlive

fun Zombie.isBoss(): Boolean = this is ZombieRaider && this.`plantz$getIsBoss`()

fun Zombie.isLeader(): Boolean = this is ZombieRaider && this.`plantz$getIsLeader`()