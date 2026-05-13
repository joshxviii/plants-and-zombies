package joshxviii.plantz.effect

import joshxviii.plantz.ElectricArcParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.hasSameRootOwner
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.phys.Vec3

class ElectrifyMobEffect(
    category: MobEffectCategory,
    color: Int,
    particleOptions: ParticleOptions
) : MobEffect(category, color, particleOptions) {
    companion object {
        const val ZAP_INTERVAL: Int = 25
        const val ZAP_RANGE: Double = 3.0
    }

    // chain lightening effect to nearby mobs
    override fun onMobHurt(level: ServerLevel, mob: LivingEntity, amplifier: Int, source: DamageSource, damage: Float) {
        super.onMobHurt(level, mob, amplifier, source, damage)
        // targeting conditions for nearby candidates to zap
        val rootCauseEntity = source.entity
        val targetConditions = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting().selector { entity, level ->
            val isRootSource = rootCauseEntity?.let { entity.hasSameRootOwner(it) || it.`is`(entity) } ?: false
            val canHurt = (rootCauseEntity as? LivingEntity)?.canAttack(entity) ?: true
            !entity.`is`(mob) && !entity.hurtMarked && !isRootSource && canHurt
        }
        val nearbyTargets = level.getNearbyEntities(LivingEntity::class.java, targetConditions, mob, mob.boundingBox.inflate(ZAP_RANGE))
        nearbyTargets.randomOrNull()?.let {
            zap(level, it, rootCauseEntity)
            level.sendParticles(
                ElectricArcParticleOptions(
                    Vec3(it.x, it.y + level.random.nextFloat() * it.boundingBox.ysize, it.z),
                    color = 0x88CCFF,
                    thickness = 0.15f
                ),
                mob.x, mob.y + level.random.nextFloat() * mob.boundingBox.ysize, mob.z,
                1, 0.0, 0.0, 0.0, 0.0
            )
        }
    }

    override fun applyEffectTick(level: ServerLevel, mob: LivingEntity, amplification: Int): Boolean {
        if (mob.isInWater) zap(level, mob)
        return true
    }

    private fun zap(level: ServerLevel, target: LivingEntity, causeEntity: Entity? = null) {
        val source = target.damageSources().source(PazDamageTypes.ZAP,null,causeEntity)
        target.hurtServer(level, source, 0.01f)
        level.sendParticles(
            PazServerParticles.ELECTRIFIED,
            target.x, target.y + target.boundingBox.ysize*0.5, target.z, 15,
            target.boundingBox.xsize*0.55,
            target.boundingBox.ysize*0.25,
            target.boundingBox.zsize*0.55,
            0.0
        )
        //TODO sound effect
    }

    override fun shouldApplyEffectTickThisTick(tickCount: Int, amplification: Int): Boolean {
        val interval = ZAP_INTERVAL shr amplification
        return if (interval > 0) tickCount % interval == 0 else true
    }

    override fun onEffectStarted(effectInstance: MobEffectInstance, entity: LivingEntity) {
        super.onEffectStarted(effectInstance, entity)
    }

    override fun onEffectRemoved(effectInstance: MobEffectInstance, entity: LivingEntity) {
        super.onEffectRemoved(effectInstance, entity)
    }
}