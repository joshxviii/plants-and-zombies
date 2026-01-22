package joshxviii.plantz

import PazDataSerializers.DATA_SLEEPING
import joshxviii.plantz.effect.HypnotizedMobEffect
import joshxviii.plantz.effect.ToxicMobEffect
import joshxviii.plantz.effect.ZombieOmenMobEffect
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.goal.Goal

object PazEffects {

    @JvmField val TOXIC: Holder<MobEffect> = register("toxic",
        ToxicMobEffect(MobEffectCategory.HARMFUL, 10762143))
    @JvmField val HYPNOTIZE: Holder<MobEffect> = register("hypnotize",
        HypnotizedMobEffect(MobEffectCategory.NEUTRAL, 15841255)
            .withSoundOnAdded(PazSounds.HYPNOTIZED))
    @JvmField val ZOMBIE_OMEN : Holder<MobEffect> = register("zombie_omen",
        ZombieOmenMobEffect(MobEffectCategory.NEUTRAL, 1297708, PazServerParticles.ZOMBIE_OMEN)
            .withSoundOnAdded(SoundEvents.APPLY_EFFECT_RAID_OMEN))

    fun register(name: String, mobEffect: MobEffect): Holder<MobEffect> {
        return Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            pazResource(name),
            mobEffect
        )
    }

    val HYPNOTIZED_GOAL_ATTACHMENT: AttachmentType<Goal> =
        AttachmentRegistryImpl.builder<Goal>()
            .buildAndRegister(pazResource("hypnotized_goal"))

    fun initialize() {

    }
}