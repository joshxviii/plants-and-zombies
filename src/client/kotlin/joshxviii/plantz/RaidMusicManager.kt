package joshxviii.plantz

import joshxviii.plantz.PazNetwork.ZombieRaidClientCache
import joshxviii.plantz.PazSounds.RAID_MUSIC_ARMY
import joshxviii.plantz.PazSounds.RAID_MUSIC_BUCKET
import joshxviii.plantz.PazSounds.RAID_MUSIC_HALFTIME
import joshxviii.plantz.PazSounds.RAID_MUSIC_HIGH
import joshxviii.plantz.PazSounds.RAID_MUSIC_LEAGUE
import joshxviii.plantz.PazSounds.RAID_MUSIC_LOW
import joshxviii.plantz.PazSounds.RAID_MUSIC_MEDIUM
import joshxviii.plantz.PazSounds.RAID_MUSIC_PIRATE
import joshxviii.plantz.PazSounds.RAID_MUSIC_WINTER
import joshxviii.plantz.PazSounds.RAID_MUSIC_ZOMBOSS
import joshxviii.plantz.networking.ZombieRaidClientData
import joshxviii.plantz.raid.WaveType
import joshxviii.plantz.raid.ZombieRaid
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation
import net.minecraft.core.Holder
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import kotlin.collections.set

enum class RaidMusic(
    val music: Holder.Reference<SoundEvent>,
    val layerIndex: Int
) {
    LOW(RAID_MUSIC_LOW, 0),
    MEDIUM(RAID_MUSIC_MEDIUM, 1),
    HIGH(RAID_MUSIC_HIGH, 2),
    BUCKET(RAID_MUSIC_BUCKET, 3),
    HALFTIME(RAID_MUSIC_HALFTIME, 4),
    WINTER(RAID_MUSIC_WINTER, 5),
    PIRATE(RAID_MUSIC_PIRATE, 6),
    ARMY(RAID_MUSIC_ARMY, 7),
    LEAGUE(RAID_MUSIC_LEAGUE, 8),
    ZOMBOSS(RAID_MUSIC_ZOMBOSS, 9);
}

object RaidMusicManager {
    const val FADE_IN_TIME = 60

    private val random = RandomSource.create()
    val minecraft = Minecraft.getInstance()
    var activeLayers: MutableMap<RaidMusic, RaidMusicSoundInstance> = mutableMapOf()
    var targetMusic: RaidMusic? = null
    var raidEvent: ZombieRaidClientData? = null

    fun tick() {
        raidEvent = ZombieRaidClientCache.get().also { event ->
            if (event == null && activeLayers.isNotEmpty()) {// fade out when leaving raid
               activeLayers.values.forEach { it.volume -= 1f / (FADE_IN_TIME*2).coerceAtLeast(1) }
               activeLayers[targetMusic]?.let { if (it.volume <= 0.0f) stop() }
            }
        }
        val event = raidEvent?: return

        //stop music when raid is complete
        if (event.status != ZombieRaid.ZombieRaidStatus.ONGOING && event.status != ZombieRaid.ZombieRaidStatus.NEXT_WAVE) {
            stop()
            return
        }

        // transition between song layers
        minecraft.musicManager.stopPlaying()
        if (shouldUpdateMusic()) {
            targetMusic = getMusicFromRaidContext(event)
            activeLayers.forEach { (index, layer) ->
                if (index == targetMusic) layer.volume += 1f / FADE_IN_TIME.coerceAtLeast(1)
                else layer.volume -= 1f / FADE_IN_TIME.coerceAtLeast(1)
            }
        }
    }

    fun start() {
        stop()
        RaidMusic.entries.forEach {
            val layer = RaidMusicSoundInstance(it.music.value())
            layer.volume = 0.0f
            activeLayers[it] = layer
            minecraft.soundManager.play(layer)
        }
    }

    fun stop() {
        activeLayers.forEach { (_, layer) ->
            layer.stopLayer()
        }
        activeLayers.clear()
        targetMusic = null
    }

    fun getMusicFromRaidContext(raidEvent: ZombieRaidClientData): RaidMusic {
        val totalWaves = raidEvent.numWaves
        val currentWave = raidEvent.wavesSpawned
        val waveType = raidEvent.currentWaveType

        return when (waveType) {
            WaveType.DEFAULT -> when {
                (currentWave == totalWaves || currentWave in 12..ZombieRaid.MAXIMUM_WAVE_COUNT) ->
                    RaidMusic.HIGH
                currentWave in 6..11 ->
                    RaidMusic.MEDIUM
                else ->
                    RaidMusic.LOW
            }
            WaveType.BUCKET_BRIGADE -> RaidMusic.BUCKET
            WaveType.HALFTIME_SHOWDOWN -> RaidMusic.HALFTIME
            WaveType.WINTER_WONDERLAND -> RaidMusic.WINTER
            WaveType.PIRATE_INVASION -> RaidMusic.PIRATE
            WaveType.ROBO_ARMY -> RaidMusic.ARMY
            WaveType.LEAGUE_OF_AWESOME -> RaidMusic.LEAGUE
            WaveType.ZOMBOSS -> RaidMusic.ZOMBOSS
        }
    }

    private fun shouldUpdateMusic(): Boolean {
        if (activeLayers.isEmpty() || activeLayers[targetMusic]?.isStopped == true) start()
        raidEvent?.let {
            if (getMusicFromRaidContext(it) != targetMusic) return true
        }
        activeLayers[targetMusic]?.let {
            if (it.volume < 1.0f) return true
        }
        return false
    }
}

class RaidMusicSoundInstance(
    val layer: SoundEvent,
): AbstractTickableSoundInstance(layer, SoundSource.MUSIC, SoundInstance.createUnseededRandom()) {

    init {
        volume = 0.0f
        looping = true
        delay = 0
        attenuation = Attenuation.NONE
        relative = true
    }

    fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0.0f, 1.0f)
    }

    fun stopLayer() = this.stop()

    override fun tick() {}
}