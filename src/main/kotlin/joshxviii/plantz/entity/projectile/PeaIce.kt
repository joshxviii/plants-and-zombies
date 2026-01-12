package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult

class PeaIce(
    type: EntityType<out PlantProjectile> = PazEntities.PEA_ICE,
    level: Level,
    owner: Plant? = null,
) : PlantProjectile(type, level, owner,
    PazDamageTypes.FREEZE,
) {
    override fun onHitEntity(hitResult: EntityHitResult) {
        val target = hitResult.entity
        if (target is LivingEntity) {
            target.addEffect(MobEffectInstance(MobEffects.SLOWNESS, 100, 0))
            target.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 100, 0))
        }
        super.onHitEntity(hitResult)
    }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(PazServerParticles.ICE_PEA_HIT)
    }
}