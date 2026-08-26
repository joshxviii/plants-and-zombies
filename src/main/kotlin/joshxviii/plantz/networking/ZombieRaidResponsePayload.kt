package joshxviii.plantz.networking

import joshxviii.plantz.pazResource
import joshxviii.plantz.raid.WaveType
import joshxviii.plantz.raid.ZombieRaid
import net.minecraft.core.UUIDUtil
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import java.util.UUID

class ZombieRaidResponsePayload(
    val data: ZombieRaidClientData,
    val terminate: Boolean = false
): CustomPacketPayload {
    companion object {
        val ID: CustomPacketPayload.Type<ZombieRaidResponsePayload> = CustomPacketPayload.Type(pazResource("zombie_raid_response"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ZombieRaidResponsePayload> =
            StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, { it.data.id },
                ByteBufCodecs.VAR_INT, { it.data.status.ordinal },
                ByteBufCodecs.VAR_INT, { it.data.currentWaveType.ordinal },
                ByteBufCodecs.VAR_INT, { it.data.wavesSpawned },
                ByteBufCodecs.VAR_INT, { it.data.numWaves },
                ByteBufCodecs.VAR_INT, { it.data.waveTimer },
                ByteBufCodecs.VAR_INT, { it.data.activeTime },
                ByteBufCodecs.FLOAT,   { it.data.zombieHealth },
                ByteBufCodecs.FLOAT,   { it.data.zombieHealthMax },
                ByteBufCodecs.FLOAT,   { it.data.flagHealth },
                ByteBufCodecs.BOOL,    { it.data.seenCredits },
                ByteBufCodecs.BOOL,    { it.terminate },
                { id, status, waveType, waves, num, timer, activeTime, zombieH, zombieMax, flagH, credits, terminate ->
                    ZombieRaidResponsePayload(
                        ZombieRaidClientData(
                            id,
                            ZombieRaid.ZombieRaidStatus.entries[status],
                            WaveType.entries[waveType],
                            waves, num, timer, activeTime, zombieH, zombieMax, flagH, credits
                        ),
                        terminate
                    )
                }
            )
    }

    override fun type() = ID

}

data class ZombieRaidClientData(
    val id: UUID = UUID.randomUUID(),
    val status: ZombieRaid.ZombieRaidStatus = ZombieRaid.ZombieRaidStatus.STOPPED,
    val currentWaveType: WaveType = WaveType.DEFAULT,
    val wavesSpawned: Int = 0,
    val numWaves: Int = 0,
    val waveTimer: Int = 0,
    val activeTime: Int = 0,
    val zombieHealth: Float = 0f,
    val zombieHealthMax: Float = 0f,
    val flagHealth: Float = 0f,
    val seenCredits: Boolean = false
)