package joshxviii.plantz

import joshxviii.plantz.PazClientNetwork.ZombieRaidClientCache
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
import net.minecraft.client.resources.sounds.AbstractSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource

object RaidMusicManager {
    private val random = RandomSource.create()
    val minecraft = Minecraft.getInstance()
    var activeLayers: MutableMap<Int, RaidMusicSoundInstance> = mutableMapOf()
    val raidMusicLayer = arrayOf(
        RAID_MUSIC_LOW,
        RAID_MUSIC_MEDIUM,
        RAID_MUSIC_HIGH,
        RAID_MUSIC_BUCKET,
        RAID_MUSIC_HALFTIME,
        RAID_MUSIC_WINTER,
        RAID_MUSIC_PIRATE,
        RAID_MUSIC_ARMY,
        RAID_MUSIC_LEAGUE,
        RAID_MUSIC_ZOMBOSS
    )
    var targetIndex: Int = 0
    var raidEvent: ZombieRaidClientData? = null

    fun tick() {
        raidEvent = ZombieRaidClientCache.active.values.firstOrNull() ?: return
        val event = raidEvent?: return
        if (event.status != ZombieRaid.ZombieRaidStatus.ONGOING && event.status != ZombieRaid.ZombieRaidStatus.NEXT_WAVE) {
            stop()
            return
        }
        minecraft.musicManager.stopPlaying()

        if (shouldUpdateMusic()) {
            targetIndex = getLayerIndex(event.currentWaveType, event.wavesSpawned)
            activeLayers.forEach { (index, layer) ->
                if (index == targetIndex) layer.volume = 1.0f
                else layer.volume = 0.0f
            }
        }
    }

    fun start() {
        raidMusicLayer.forEachIndexed { index, layer ->
            val layer = RaidMusicSoundInstance(layer.value())
            activeLayers[index] = layer
            minecraft.soundManager.play(layer)
        }
    }

    fun stop() {
        activeLayers.forEach { (_, layer) ->
            minecraft.soundManager.stop(layer)
        }
        activeLayers.clear()
        targetIndex = 0
    }

    fun shouldUpdateMusic(): Boolean {
        targetIndex
        raidEvent?.let {
            if (getLayerIndex(it.currentWaveType, it.wavesSpawned) != targetIndex) return true
        }
        return false
    }

    private fun getLayerIndex(type: WaveType, waveNum: Int = 0): Int = when (type) {
        WaveType.DEFAULT -> when (waveNum) {
            in 11..Int.MAX_VALUE -> 2
            in 6..10             -> 1
            else                       -> 0
        }
        WaveType.BUCKET_BRIGADE -> 3
        WaveType.HALFTIME_SHOWDOWN -> 4
        WaveType.WINTER_WONDERLAND -> 5
        WaveType.PIRATE_INVASION -> 6
        WaveType.ROBO_ARMY -> 7
        WaveType.LEAGUE_OF_AWESOME -> 8
    }.coerceIn(0, raidMusicLayer.size - 1)
}

class RaidMusicSoundInstance(
    val layer: SoundEvent,
): AbstractSoundInstance(layer.location, SoundSource.MUSIC, SoundInstance.createUnseededRandom()) {

    init {
        volume = 1.0f
        looping = true
        delay = 0
        attenuation = Attenuation.NONE
        relative = true
    }

    fun setVolume(volume: Float) {
        this.volume = volume
    }
}