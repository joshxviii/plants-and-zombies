package joshxviii.plantz

import joshxviii.plantz.entity.gnome.GnomeSoundVariant
import joshxviii.plantz.entity.gnome.GnomeSoundVariants
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import java.util.stream.Collectors
import java.util.stream.Stream

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

    fun registerForHolder(name: String): Holder.Reference<SoundEvent> {
        val soundId = pazResource(name)
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, soundId, SoundEvent.createVariableRangeEvent(soundId))
    }

    var GNOME_SOUNDS: Map<GnomeSoundVariants.SoundSet, GnomeSoundVariant> = registerGnomeSoundVariants()

    private fun registerGnomeSoundVariants(): Map<GnomeSoundVariants.SoundSet, GnomeSoundVariant> {
        return GnomeSoundVariants.SoundSet.entries.associateWith { soundSet ->
            val id = soundSet.id
            val sounds = GnomeSoundVariant.GnomeSoundSet(
                ambientSound = registerForHolder("entity.$id.ambient"),
                deathSound   = registerForHolder("entity.$id.death"),
                hurtSound    = registerForHolder("entity.$id.hurt"),
                stepSound    = SoundEvents.WOLF_STEP
            )
            GnomeSoundVariant(sounds)
        }
    }

    fun initialize() {}
}