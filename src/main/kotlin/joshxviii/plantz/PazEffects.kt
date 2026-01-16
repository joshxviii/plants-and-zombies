package joshxviii.plantz

import joshxviii.plantz.effect.ToxicMobEffect
import joshxviii.plantz.effect.ZombieOmenMobEffect
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory

object PazEffects {

    @JvmField val TOXIC: Holder<MobEffect> = register("toxic",
        ToxicMobEffect(MobEffectCategory.HARMFUL, 10762143))
    @JvmField val ZOMBIE_OMEN : Holder<MobEffect> = register("zombie_omen",
        ZombieOmenMobEffect(MobEffectCategory.NEUTRAL, 1297708, PazServerParticles.ZOMBIE_OMEN).withSoundOnAdded(SoundEvents.APPLY_EFFECT_RAID_OMEN))

    fun register(name: String, mobEffect: MobEffect): Holder<MobEffect> {
        return Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            pazResource(name),
            mobEffect
        )
    }

    fun initialize() {}
}