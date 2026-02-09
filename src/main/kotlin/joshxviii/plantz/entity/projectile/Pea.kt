package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2

class Pea(
    level: Level,
    owner: Plant? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
) : PlantProjectile(PazEntities.PEA, level, owner, spawnOffset,
    PazDamageTypes.PLANT
) {
    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(PazServerParticles.PEA_HIT)
    }
}