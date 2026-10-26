package joshxviii.plantz.networking

import joshxviii.plantz.pazResource
import joshxviii.plantz.raid.WaveType
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
                WaveType.STREAM_CODEC.apply(ByteBufCodecs.list()), { it.data.waveTypes },
                ByteBufCodecs.VAR_INT, { it.data.numWaves },
                ByteBufCodecs.VAR_INT, { it.data.waveTimer },
                ByteBufCodecs.VAR_INT, { it.data.activeTime },
                ByteBufCodecs.FLOAT,   { it.data.plantProgressBar },
                ByteBufCodecs.FLOAT,   { it.data.zombieProgressBar },
                ByteBufCodecs.FLOAT,   { it.data.bossProgressBar },
                ByteBufCodecs.BOOL,    { it.data.seenCredits },
                { id, status, waveTypes, num, timer, activeTime, plantProgress, zombieProgress, bossProgress, credits ->
                    ZombieRaidResponsePayload(
                        ZombieRaidClientData(
                            id,
                            ZombieRaid.ZombieRaidStatus.entries[status],
                            waveTypes,
                            num, timer, activeTime, plantProgress, zombieProgress, bossProgress, credits
                        )
                    )
                }
            )
    }

    override fun type() = ID

}

data class ZombieRaidClientData(
    val id: UUID = UUID.randomUUID(),
    val status: ZombieRaid.ZombieRaidStatus = ZombieRaid.ZombieRaidStatus.STOPPED,
    val waveTypes: List<WaveType> = listOf(WaveType.DEFAULT),
    val numWaves: Int = 0,
    val waveTimer: Int = 0,
    val activeTime: Int = 0,
    val plantProgressBar: Float = 0f,
    val zombieProgressBar: Float = 0f,
    val bossProgressBar: Float = 0f,
    val seenCredits: Boolean = false
) {
    fun currentWaveNumber(): Int = waveTypes.size
    fun currentWaveType(): WaveType? = waveTypes.lastOrNull()
    fun isFinalWave(): Boolean = waveTypes.size == numWaves
    fun bossIsAlive(): Boolean = bossProgressBar > 0f
}