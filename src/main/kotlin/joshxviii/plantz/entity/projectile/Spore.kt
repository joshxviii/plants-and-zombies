package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class Spore(
    level: Level,
    owner: Plant? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
) : PlantProjectile(PazEntities.SPORE, level, owner, spawnOffset,
    PazDamageTypes.PLANT,
) {

    override fun tick() {
        super.tick()
        spawnParticle(
            PazServerParticles.SPORE,
            spread = Vec3(0.01,0.01,0.01),
            speed = 0.1
        )
    }

    override fun afterHitEntityEffect(target: LivingEntity) {
        target.addEffect(MobEffectInstance(PazEffects.TOXIC, 50, 0))
    }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(
            PazServerParticles.SPORE_HIT,
            amount = 18,
            speed = 0.4
        )
    }

}