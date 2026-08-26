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
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import kotlin.collections.set

object RaidMusicManager {
    const val FADE_IN_TIME = 60

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
    var targetIndex: Int = -1
    var raidEvent: ZombieRaidClientData? = null

    fun tick() {
        raidEvent = ZombieRaidClientCache.get().also { event ->
            if (event == null && activeLayers.isNotEmpty()) {// fade out when leaving raid
               activeLayers.values.forEach { it.volume -= 1f / (FADE_IN_TIME*2).coerceAtLeast(1) }
               activeLayers[targetIndex]?.let { if (it.volume <= 0.0f) stop() }
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
            targetIndex = getLayerIndex(event.currentWaveType, event.wavesSpawned)
            activeLayers.forEach { (index, layer) ->
                if (index == targetIndex) layer.volume += 1f / FADE_IN_TIME.coerceAtLeast(1)
                else layer.volume -= 1f / FADE_IN_TIME.coerceAtLeast(1)
            }
        }
    }

    fun start() {
        stop()
        raidMusicLayer.forEachIndexed { index, layer ->
            val layer = RaidMusicSoundInstance(layer.value())
            layer.volume = 0.0f
            activeLayers[index] = layer
            minecraft.soundManager.play(layer)
        }
    }

    fun stop() {
        activeLayers.forEach { (_, layer) ->
            layer.stopLayer()
        }
        activeLayers.clear()
        targetIndex = -1
    }

    fun shouldUpdateMusic(): Boolean {
        if (activeLayers.isEmpty() || activeLayers[targetIndex]?.isStopped == true) start()

        raidEvent?.let {
            if (getLayerIndex(it.currentWaveType, it.wavesSpawned) != targetIndex) return true
        }
        activeLayers[targetIndex]?.let {
            if (it.volume < 1.0f) return true
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