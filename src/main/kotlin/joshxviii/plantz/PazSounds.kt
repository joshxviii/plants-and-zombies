package joshxviii.plantz

import joshxviii.plantz.entity.gnome.GnomeSoundVariant
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents

object PazSounds {

    @JvmField val PROJECTILE_FIRE = register("entity.projectile.fire")
    @JvmField val PROJECTILE_HIT_CONE = register("entity.projectile.hit_cone")
    @JvmField val PROJECTILE_HIT_BUCKET = register("entity.projectile.hit_bucket")
    @JvmField val CHOMPER_ATTACK = register("entity.chomper.attack")

    @JvmField val ZOMBIE_EATS = register("entity.zombie.eat")

    @JvmField val BROWNCOAT_AMBIENT = register("entity.browncoat.ambient")
    @JvmField val BROWNCOAT_HURT = register("entity.browncoat.hurt")
    @JvmField val BROWNCOAT_DEATH = register("entity.browncoat.death")
    @JvmField val MINER_AMBIENT = register("entity.miner.ambient")
    @JvmField val MINER_HURT = register("entity.miner.hurt")
    @JvmField val MINER_DEATH = register("entity.miner.death")
    @JvmField val ZOMBIE_YETI_AMBIENT = register("entity.zombie_yeti.ambient")
    @JvmField val ZOMBIE_YETI_HURT = register("entity.zombie_yeti.hurt")
    @JvmField val ZOMBIE_YETI_DEATH = register("entity.zombie_yeti.death")

    @JvmField var GNOME_SOUNDS: Map<GnomeSoundVariant, GnomeSoundVariant.GnomeSoundSet> = registerGnomeSoundVariants()

    @JvmField val HYPNOTIZED = register("event.mob_effect.hypnotized")
    @JvmField val APPLY_ZOMBIE_OMEN = register("event.mob_effect.zombie_omen")

    private fun registerGnomeSoundVariants(): Map<GnomeSoundVariant, GnomeSoundVariant.GnomeSoundSet> {
        return GnomeSoundVariant.entries.associateWith { soundVariant ->
            val id = soundVariant.serializedName
            GnomeSoundVariant.GnomeSoundSet(
                ambientSound = registerForHolder("entity.gnome.$id.ambient"),
                deathSound   = registerForHolder("entity.gnome.$id.death"),
                hurtSound    = registerForHolder("entity.gnome.$id.hurt"),
                jumpSound    = registerForHolder("entity.gnome.$id.jump"),
                stepSound    = SoundEvents.WOLF_STEP
            )
        }
    }

    fun register(name: String): SoundEvent {
        val soundId = pazResource(name)
        return Registry.register(BuiltInRegistries.SOUND_EVENT, soundId, SoundEvent.createVariableRangeEvent(soundId))
    }

    fun registerForHolder(name: String): Holder.Reference<SoundEvent> {
        val soundId = pazResource(name)
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, soundId, SoundEvent.createVariableRangeEvent(soundId))
    }

    fun initialize() {}
}