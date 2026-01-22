package joshxviii.plantz

import joshxviii.plantz.entity.gnome.GnomeSoundVariant
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents

object PazSounds {

    //TODO hypno sound
    @JvmField val HYPNOTIZED = register("event.mob_effect.hypnotized")

    @JvmField val ZOMBIE_YETI_AMBIENT = register("entity.zombie_yeti.ambient")
    @JvmField val ZOMBIE_YETI_HURT = register("entity.zombie_yeti.hurt")
    @JvmField val ZOMBIE_YETI_DEATH = register("entity.zombie_yeti.death")

    @JvmField val BROWNCOAT_AMBIENT = register("entity.browncoat.ambient")
    @JvmField val BROWNCOAT_HURT = register("entity.browncoat.hurt")
    @JvmField val BROWNCOAT_DEATH = register("entity.browncoat.death")

    @JvmField var GNOME_SOUNDS: Map<GnomeSoundVariant, GnomeSoundVariant.GnomeSoundSet> = registerGnomeSoundVariants()

    fun register(name: String): SoundEvent {
        val soundId = pazResource(name)
        return Registry.register(BuiltInRegistries.SOUND_EVENT, soundId, SoundEvent.createVariableRangeEvent(soundId))
    }

    fun registerForHolder(name: String): Holder.Reference<SoundEvent> {
        val soundId = pazResource(name)
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, soundId, SoundEvent.createVariableRangeEvent(soundId))
    }

    private fun registerGnomeSoundVariants(): Map<GnomeSoundVariant, GnomeSoundVariant.GnomeSoundSet> {
        return GnomeSoundVariant.entries.associateWith { variant ->
            val id = variant.serializedName
            GnomeSoundVariant.GnomeSoundSet(
                ambientSound = registerForHolder("entity.$id.ambient"),
                deathSound   = registerForHolder("entity.$id.death"),
                hurtSound    = registerForHolder("entity.$id.hurt"),
                jumpSound    = registerForHolder("entity.$id.jump"),
                stepSound    = SoundEvents.WOLF_STEP
            )
        }
    }

    fun initialize() {}
}