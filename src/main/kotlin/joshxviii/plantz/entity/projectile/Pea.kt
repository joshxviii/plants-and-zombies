package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class Pea(
    type: EntityType<out PlantProjectile> = PazEntities.PEA,
    level: Level,
    owner: Plant? = null,
    spawnOffset: Vec2 = Vec2.ZERO
) : PlantProjectile(type, level, owner, spawnOffset,
    PazDamageTypes.PLANT
) {
    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(PazServerParticles.PEA_HIT)
    }
}