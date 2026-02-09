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
    @JvmField val NEWSPAPER_ZOMBIE_AMBIENT = register("entity.newspaper_zombie.ambient")
    @JvmField val NEWSPAPER_ZOMBIE_HURT = register("entity.newspaper_zombie.hurt")
    @JvmField val NEWSPAPER_ZOMBIE_DEATH = register("entity.newspaper_zombie.death")
    @JvmField val DIGGER_ZOMBIE_AMBIENT = register("entity.digger_zombie.ambient")
    @JvmField val DIGGER_ZOMBIE_HURT = register("entity.digger_zombie.hurt")
    @JvmField val DIGGER_ZOMBIE_DEATH = register("entity.digger_zombie.death")
    @JvmField val ZOMBIE_YETI_AMBIENT = register("entity.zombie_yeti.ambient")
    @JvmField val ZOMBIE_YETI_HURT = register("entity.zombie_yeti.hurt")
    @JvmField val ZOMBIE_YETI_DEATH = register("entity.zombie_yeti.death")
    @JvmField val DISCO_ZOMBIE_AMBIENT = register("entity.disco_zombie.ambient")
    @JvmField val DISCO_ZOMBIE_HURT = register("entity.disco_zombie.hurt")
    @JvmField val DISCO_ZOMBIE_DEATH = register("entity.disco_zombie.death")
    @JvmField val GARGANTUAR_AMBIENT = register("entity.gargantuar.ambient")
    @JvmField val GARGANTUAR_HURT = register("entity.gargantuar.hurt")
    @JvmField val GARGANTUAR_DEATH = register("entity.gargantuar.death")
    @JvmField val GARGANTUAR_DIG = register("entity.gargantuar.dig")

    @JvmField var GNOME_SOUNDS: Map<GnomeSoundVariant, GnomeSoundVariant.GnomeSoundSet> = registerGnomeSoundVariants()
    @JvmField var GNOME_JUMP = register("entity.gnome.jump")

    @JvmField val HYPNOTIZED = register("event.mob_effect.hypnotized")
    @JvmField val APPLY_ZOMBIE_OMEN = register("event.mob_effect.zombie_omen")

    @JvmField val SNOWCHUNK_HIT = registerForHolder("entity.snowchunk.hit")

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