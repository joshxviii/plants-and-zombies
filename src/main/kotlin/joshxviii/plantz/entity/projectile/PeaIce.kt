package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

class PeaIce(
    type: EntityType<out PeaProjectile> = PazEntities.PEA_ICE,
    level: Level,
    owner: Plant? = null,
) : PeaProjectile(type, level, owner,
    DamageTypes.FREEZE,
    PazParticles.ICE_PEA_HIT
) {
    override fun onHitEntity(hitResult: EntityHitResult) {
        val target = hitResult.entity
        if (target is LivingEntity && target !is Player) {
            target.addEffect(MobEffectInstance(MobEffects.SLOWNESS, 100, 0))
            target.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 100, 0))
        }
        super.onHitEntity(hitResult)
    }
}