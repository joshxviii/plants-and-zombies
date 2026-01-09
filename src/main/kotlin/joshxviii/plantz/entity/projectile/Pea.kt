package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult

class Pea(
    type: EntityType<out PeaProjectile> = PazEntities.PEA,
    level: Level,
    owner: Plant? = null,
) : PeaProjectile(type, level, owner,
    DamageTypes.MOB_PROJECTILE,
) {
    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(PazParticles.PEA_HIT)
    }
}