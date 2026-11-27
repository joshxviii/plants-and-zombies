package joshxviii.plantz

import joshxviii.plantz.item.RollerSkatesItem
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity

class RailGrindSoundInstance(
    private val entity: LivingEntity,
) : AbstractTickableSoundInstance(PazSounds.ROLLER_SKATE_GRIND, SoundSource.PLAYERS, SoundInstance.createUnseededRandom()) {

    private companion object {
        const val MAX_SPEED = 0.4f
        const val MIN_VOLUME = 0.2f
        const val MAX_VOLUME = 0.5f
        const val MIN_PITCH = 0.9f
        const val MAX_PITCH = 1.3f
        const val VOLUME_SMOOTHING = 0.2f
        const val SILENT_THRESHOLD = 0.01f
    }

    private var grinding = true
    private var currentPitch = MIN_PITCH
    var finished = false
        private set

    init {
        looping = true
        delay = 0
        volume = 0.0f
        attenuation = SoundInstance.Attenuation.LINEAR
        x = entity.x
        y = entity.y
        z = entity.z
    }

    fun setGrinding(value: Boolean) {
        grinding = value
    }

    override fun canStartSilent(): Boolean = true

    override fun getPitch(): Float = currentPitch

    override fun tick() {
        if (entity.isRemoved) {
            finish()
            return
        }

        x = entity.x
        y = entity.y
        z = entity.z

        val speed = entity.deltaMovement.horizontalDistance().toFloat()
        val t = Mth.clamp(speed / MAX_SPEED, 0.0f, 1.0f)
        val targetVolume = if (grinding) Mth.lerp(t, MIN_VOLUME, MAX_VOLUME) else 0.0f
        volume = Mth.lerp(VOLUME_SMOOTHING, volume, targetVolume)
        currentPitch = Mth.lerp(t, MIN_PITCH, MAX_PITCH)

        if (!grinding && volume < SILENT_THRESHOLD) finish()
    }

    private fun finish() {
        finished = true
        stop()
    }
}

object RailGrindSoundManager {
    private val sounds = HashMap<LivingEntity, RailGrindSoundInstance>()

    fun tick(minecraft: Minecraft) {
        val level = minecraft.level
        if (level == null) {
            sounds.clear()
            return
        }

        sounds.entries.removeIf { (entity, sound) -> entity.isRemoved || sound.finished }

        val soundManager = minecraft.soundManager
        level.entitiesForRendering().forEach { entity ->
            if (entity !is LivingEntity) return@forEach
            val grinding = RollerSkatesItem.isRailGrinding(entity)
            val existing = sounds[entity]
            if (existing != null) existing.setGrinding(grinding)
            else if (grinding) {
                val sound = RailGrindSoundInstance(entity)
                sounds[entity] = sound
                soundManager.play(sound)
            }
        }
    }
}
