package joshxviii.plantz.effect

import joshxviii.plantz.ElectricArcParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.PazTags
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
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.phys.Vec3

class ElectrifyMobEffect(
    category: MobEffectCategory,
    color: Int,
    particleOptions: ParticleOptions
) : MobEffect(category, color, particleOptions) {
    companion object {
        const val ZAP_INTERVAL: Int = 12
        const val ZAP_RANGE: Double = 3.0
        const val ZAP_DAMAGE: Float = 2.5f
    }

    // chain lightening effect to nearby mobs
    override fun onMobHurt(level: ServerLevel, mob: LivingEntity, amplifier: Int, source: DamageSource, damage: Float) {
        super.onMobHurt(level, mob, amplifier, source, damage)
        if (!source.`is`(PazTags.DamageTypes.IS_ELECTRIC)) return
        // targeting conditions for nearby candidates to zap
        val rootCauseEntity = source.entity
        val targetConditions = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting().selector { entity, level ->
            val isRootSource = rootCauseEntity?.let { entity.hasSameRootOwner(it) || it.`is`(entity) } ?: false
            val onSameTeam = (entity is Enemy && mob is Enemy) || (entity !is Enemy && mob !is Enemy)
            val canHurt = (rootCauseEntity as? LivingEntity)?.canAttack(entity) ?: true
            !entity.`is`(mob) && !entity.hurtMarked && !isRootSource && canHurt && onSameTeam
        }
        val nearbyTargets = level.getNearbyEntities(LivingEntity::class.java, targetConditions, mob, mob.boundingBox.inflate(ZAP_RANGE))
        nearbyTargets.randomOrNull()?.let {
            mob.getEffect(PazEffects.ELECTRIFIED)?.let { effect ->
                if (effect.amplifier>0) {
                    val duration = if (effect.isInfiniteDuration) 300 else (effect.duration*0.25f).toInt()
                    it.addEffect(MobEffectInstance(PazEffects.ELECTRIFIED, duration.coerceAtMost(300), effect.amplifier-1))
                }
            }
            zap(level, it, causeEntity = rootCauseEntity)
            level.sendParticles(
                ElectricArcParticleOptions(// electric arc particle
                    Vec3(it.getRandomX(0.2), it.randomY, it.getRandomZ(0.2)),
                    color = 0xBDFDF5,
                    width = 0.15f
                ),
                mob.getRandomX(0.2), mob.randomY, mob.getRandomZ(0.2),
                1, 0.0, 0.0, 0.0, 0.0
            )
        }
    }

    override fun applyEffectTick(level: ServerLevel, mob: LivingEntity, amplification: Int): Boolean {
        if (mob.isInWater) zap(level, mob, 1.25f)
        return true
    }

    private fun zap(level: ServerLevel, target: LivingEntity, damage: Float = ZAP_DAMAGE, causeEntity: Entity? = null) {
        val source = target.damageSources().source(PazDamageTypes.ZAP,causeEntity,null)
        target.hurtServer(level, source, damage)
        level.sendParticles(
            PazServerParticles.ELECTRIFIED,
            target.x, target.y + target.boundingBox.ysize*0.5, target.z, 10,
            target.boundingBox.xsize*0.55,
            target.boundingBox.ysize*0.25,
            target.boundingBox.zsize*0.55,
            0.0
        )
        //TODO sound effect
    }

    override fun shouldApplyEffectTickThisTick(tickCount: Int, amplification: Int): Boolean {
        return tickCount % ZAP_INTERVAL == 0
    }

    override fun onEffectAdded(effectInstance: MobEffectInstance, entity: LivingEntity) {
        super.onEffectAdded(effectInstance, entity)
    }

    override fun onEffectStarted(effectInstance: MobEffectInstance, entity: LivingEntity) {
        super.onEffectStarted(effectInstance, entity)
    }

    override fun onEffectRemoved(effectInstance: MobEffectInstance, entity: LivingEntity) {
        super.onEffectRemoved(effectInstance, entity)
    }
}