package joshxviii.plantz

import joshxviii.plantz.effect.ButteredMobEffect
import joshxviii.plantz.effect.HypnotizedMobEffect
import joshxviii.plantz.effect.ToxicMobEffect
import joshxviii.plantz.effect.ZombieOmenMobEffect
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.item.alchemy.Potion

object PazEffects {

    @JvmField val TOXIC: Holder<MobEffect> = register("toxic",
        ToxicMobEffect(MobEffectCategory.HARMFUL, 10762143))
    @JvmField val HYPNOTIZE: Holder<MobEffect> = register("hypnotize",
        HypnotizedMobEffect(MobEffectCategory.NEUTRAL, 15841255))
    @JvmField val ZOMBIE_OMEN : Holder<MobEffect> = register("zombie_omen",
        ZombieOmenMobEffect(MobEffectCategory.NEUTRAL, 1297708, PazServerParticles.ZOMBIE_OMEN)
            .withSoundOnAdded(PazSounds.APPLY_ZOMBIE_OMEN))
    @JvmField val BUTTERED: Holder<MobEffect> = register("buttered",
        ButteredMobEffect(MobEffectCategory.HARMFUL, 13416767, PazServerParticles.BUTTER_DRIP)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, pazResource("effect.buttered"), -999.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.JUMP_STRENGTH, pazResource("effect.buttered"), -999.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, pazResource("effect.buttered"), -999.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    )


    fun register(name: String, mobEffect: MobEffect): Holder<MobEffect> {
        return Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            pazResource(name),
            mobEffect
        )
    }

    @JvmField val HYPNOTIZE_POTION: Holder<Potion> = registerPotion("hypnotize", MobEffectInstance(HYPNOTIZE, 3600))
    @JvmField val BUTTERED_POTION: Holder<Potion> = registerPotion("buttered", MobEffectInstance(BUTTERED, 100))
    fun registerPotion(name: String, effects: MobEffectInstance): Holder<Potion> {
        val potion = Potion(name, effects)
        return Registry.registerForHolder(
            BuiltInRegistries.POTION,
            pazResource(name),
            potion
        )
    }

    val HYPNOTIZED_GOAL_ATTACHMENT: AttachmentType<Goal> =
        AttachmentRegistryImpl.builder<Goal>().buildAndRegister(pazResource("hypnotized_goal"))

    fun initialize() {

    }
}