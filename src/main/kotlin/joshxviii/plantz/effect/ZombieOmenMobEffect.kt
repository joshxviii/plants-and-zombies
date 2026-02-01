package joshxviii.plantz.effect

import joshxviii.plantz.PazBlocks.PLANTZ_FLAG_POI
import joshxviii.plantz.raid.getZombieRaids
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.entity.ai.village.poi.PoiType

class ZombieOmenMobEffect(
    category: MobEffectCategory,
    color: Int,
    particleOptions: ParticleOptions
) : MobEffect(category, color, particleOptions) {
    companion object {
        const val MAX_FLAG_DISTANCE = 24
    }

    override fun shouldApplyEffectTickThisTick(remainingDuration: Int, amplification: Int): Boolean {
        return true
    }

    override fun applyEffectTick(level: ServerLevel, mob: LivingEntity, amplification: Int): Boolean {
        if (mob !is ServerPlayer || mob.isSpectator) return false

        val flagPoi: BlockPos? = level.poiManager.findClosest(
            { p: Holder<PoiType> -> p.value() == PLANTZ_FLAG_POI },
            mob.blockPosition(),
            MAX_FLAG_DISTANCE,
            PoiManager.Occupancy.HAS_SPACE
        ).orElse(null)

        if (flagPoi != null) {
            level.getZombieRaids().createOrExtendZombieRaid(mob, flagPoi)
            return false
        }

        return true
    }
}