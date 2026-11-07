package joshxviii.plantz.networking

import joshxviii.plantz.pazResource
import joshxviii.plantz.raid.CompletedWave
import joshxviii.plantz.raid.ZombieRaid
import net.minecraft.core.UUIDUtil
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import java.util.*

class ZombieRaidResponsePayload(
    val data: ZombieRaidClientData
): CustomPacketPayload {
    companion object {
        val ID: CustomPacketPayload.Type<ZombieRaidResponsePayload> = CustomPacketPayload.Type(pazResource("zombie_raid_response"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ZombieRaidResponsePayload> =
            StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, { it.data.id },
                ByteBufCodecs.VAR_INT, { it.data.status.ordinal },
                CompletedWave.STREAM_CODEC.apply(ByteBufCodecs.list()), { it.data.waves },
                ByteBufCodecs.VAR_INT, { it.data.numWaves },
                ByteBufCodecs.VAR_INT, { it.data.waveTimer },
                ByteBufCodecs.VAR_INT, { it.data.activeTime },
                ByteBufCodecs.FLOAT,   { it.data.plantProgressBar },
                ByteBufCodecs.FLOAT,   { it.data.zombieProgressBar },
                ByteBufCodecs.FLOAT,   { it.data.bossProgressBar },
                ByteBufCodecs.BOOL,    { it.data.seenCredits },
                ByteBufCodecs.FLOAT,   { it.data.bossChance },
                RaidDetails.STREAM_CODEC, { it.data.details },
                { id, status, waveTypes, num, timer, activeTime, plantProgress, zombieProgress, bossProgress, credits, bossChance, details ->
                    ZombieRaidResponsePayload(
                        ZombieRaidClientData(
                            id = id,
                            status = ZombieRaid.ZombieRaidStatus.entries[status],
                            waves = waveTypes,
                            numWaves = num,
                            waveTimer = timer,
                            activeTime = activeTime,
                            plantProgressBar = plantProgress,
                            zombieProgressBar = zombieProgress,
                            bossProgressBar = bossProgress,
                            seenCredits = credits,
                            bossChance = bossChance,
                            details = details,
                        )
                    )
                }
            )
    }

    override fun type() = ID

}

/**
 * Raid data for the client
 */
data class ZombieRaidClientData(
    val id: UUID = UUID.randomUUID(),
    val status: ZombieRaid.ZombieRaidStatus = ZombieRaid.ZombieRaidStatus.STOPPED,
    val waves: List<CompletedWave> = listOf(),
    val numWaves: Int = 0,
    val waveTimer: Int = 0,
    val activeTime: Int = 0,
    val plantProgressBar: Float = 0f,
    val zombieProgressBar: Float = 0f,
    val bossProgressBar: Float = 0f,
    val seenCredits: Boolean = false,
    val bossChance: Float = 0f,
    val details: RaidDetails = RaidDetails(),
) {
    fun currentWaveNumber(): Int = waves.size
    fun currentWaveType(): CompletedWave? = waves.lastOrNull()
    fun isFinalWave(): Boolean = waves.size == numWaves
    fun bossIsAlive(): Boolean = bossProgressBar > 0f
}

/**
 * More detailed information that is not necessary for rendering the main UI.
 */
data class RaidDetails(
    val flagHealth: Float = 0f,
    val flagMaxHealth: Float = 0f,
    val remainingRaiders: Int = 0,
    val remainingBossHealth: Float = 0f,
    val totalBossHealth: Float = 0f,
) {
    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, RaidDetails> = StreamCodec.composite(
            ByteBufCodecs.FLOAT, RaidDetails::flagHealth,
            ByteBufCodecs.FLOAT, RaidDetails::flagMaxHealth,
            ByteBufCodecs.VAR_INT, RaidDetails::remainingRaiders,
            ByteBufCodecs.FLOAT, RaidDetails::remainingBossHealth,
            ByteBufCodecs.FLOAT, RaidDetails::totalBossHealth,
            ::RaidDetails,
        )
    }
}