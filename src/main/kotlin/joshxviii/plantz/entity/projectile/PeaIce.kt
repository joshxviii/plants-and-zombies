package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2

class PeaIce(
    type: EntityType<out PlantProjectile> = PazEntities.PEA_ICE,
    level: Level,
    spawnOffset: Vec2 = Vec2.ZERO,
    owner: Plant? = null,
) : PlantProjectile(type, level, owner, spawnOffset,
    PazDamageTypes.FREEZE
) {
    override fun afterHitEntityEffect(target: LivingEntity) {
        target.addEffect(MobEffectInstance(MobEffects.SLOWNESS, 100, 0))
        target.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 100, 0))
    }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(PazServerParticles.ICE_PEA_HIT)
    }
}