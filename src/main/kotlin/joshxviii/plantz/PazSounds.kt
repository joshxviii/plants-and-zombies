package joshxviii.plantz

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent

object PazSounds {

    // TODO INCREASE THE VOLUME
    @JvmField val ZOMBIE_YETI_AMBIENT = register("entity.zombie_yeti.ambient")
    @JvmField val ZOMBIE_YETI_HURT = register("entity.zombie_yeti.hurt")
    @JvmField val ZOMBIE_YETI_DEATH = register("entity.zombie_yeti.death")

    @JvmField val BROWNCOAT_AMBIENT = register("entity.browncoat.ambient")
    @JvmField val BROWNCOAT_HURT = register("entity.browncoat.hurt")
    @JvmField val BROWNCOAT_DEATH = register("entity.browncoat.death")

    fun register(name: String): SoundEvent {
        val soundId = pazResource(name)
        return Registry.register(BuiltInRegistries.SOUND_EVENT, soundId, SoundEvent.createVariableRangeEvent(soundId))
    }

    fun initialize() {}
}