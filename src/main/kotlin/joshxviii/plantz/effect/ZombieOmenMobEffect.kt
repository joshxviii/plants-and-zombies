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
    particleOptions: ParticleOptions,
    var flagPoi: BlockPos? = null
) : MobEffect(category, color, particleOptions) {
    companion object {
        const val MAX_FLAG_DISTANCE = 24
    }

    override fun shouldApplyEffectTickThisTick(remainingDuration: Int, amplification: Int): Boolean {
        return remainingDuration == 1
    }

    override fun applyEffectTick(level: ServerLevel, mob: LivingEntity, amplification: Int): Boolean {
        if (mob !is ServerPlayer || mob.isSpectator) return false

        if (flagPoi == null) {
            flagPoi = level.poiManager.findClosest(
                { p: Holder<PoiType> -> p.value() == PLANTZ_FLAG_POI },
                mob.blockPosition(),
                MAX_FLAG_DISTANCE,
                PoiManager.Occupancy.HAS_SPACE
            ).orElse(null)
        }

        level.getZombieRaids().createOrExtendZombieRaid(mob, flagPoi)

        return true
    }


}