package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.plants.Plant
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2

class PeaFire(
    type: EntityType<out PlantProjectile> = PazEntities.PEA_FIRE,
    level: Level,
    spawnOffset: Vec2 = Vec2.ZERO,
    owner: Plant? = null,
) : PlantProjectile(type, level, owner, spawnOffset,
    PazDamageTypes.FIRE,
) {
    override fun afterHitEntityEffect(target: LivingEntity) {
        target.igniteForSeconds(3.0f);
    }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(PazServerParticles.FIRE_PEA_HIT)
    }
}