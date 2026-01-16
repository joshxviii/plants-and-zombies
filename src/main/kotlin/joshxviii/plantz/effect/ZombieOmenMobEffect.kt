package joshxviii.plantz.effect

import joshxviii.plantz.createOrExtendZombieRaid
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity

class ZombieOmenMobEffect(
    category: MobEffectCategory,
    color: Int,
    particleOptions: ParticleOptions
) : MobEffect(category, color, particleOptions) {
    override fun shouldApplyEffectTickThisTick(remainingDuration: Int, amplification: Int): Boolean {
        return remainingDuration == 1
    }

    override fun applyEffectTick(level: ServerLevel, mob: LivingEntity, amplification: Int): Boolean {
        if (mob is ServerPlayer && !mob.isSpectator) {
            val raidOmenPosition = mob.raidOmenPosition
            if (raidOmenPosition != null) {
                level.getRaids().createOrExtendZombieRaid(mob, raidOmenPosition)
                mob.clearRaidOmenPosition()
                return false
            }
        }

        return true
    }
}