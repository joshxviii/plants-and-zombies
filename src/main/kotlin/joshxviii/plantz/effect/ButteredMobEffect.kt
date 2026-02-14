package joshxviii.plantz.effect

import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob

/**
 *
 */
class ButteredMobEffect(
    category: MobEffectCategory,
    color: Int,
    particleOptions: ParticleOptions
) : MobEffect(category, color, particleOptions) {
    companion object {

    }

    override fun onEffectStarted(effectInstance: MobEffectInstance, entity: LivingEntity) {
        super.onEffectStarted(effectInstance, entity)
        if (entity is Mob) entity.isNoAi
    }

    override fun onEffectRemoved(effectInstance: MobEffectInstance, entity: LivingEntity) {
        super.onEffectRemoved(effectInstance, entity)
    }
}