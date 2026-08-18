package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class PeaElectric(
    level: Level,
    owner: LivingEntity? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
) : PazProjectile(PazEntities.PEA_ELECTRIC, level, owner, spawnOffset,
    PazDamageTypes.PLANT_ELECTRIC
) {

    override fun tick() {
        super.tick()
        if (tickCount % 2 == 0) spawnParticle(
            PazServerParticles.ELECTRIFIED,
            amount = 1,
            spread = Vec3(0.01,0.01,0.01),
            speed = 0.4
        )
    }

    override fun afterHitEntityEffect(target: LivingEntity) {
        super.afterHitEntityEffect(target)
    }

    override fun onHitEntity(hitResult: EntityHitResult) {
        val target = hitResult.entity
        (target as? LivingEntity)?.addEffect(MobEffectInstance(PazEffects.ELECTRIFIED, 50, 0))
        super.onHitEntity(hitResult)
    }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(PazServerParticles.ELECTRIC_PEA_HIT)
    }
}