package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

class Spore(
    type: EntityType<out PlantProjectile> = PazEntities.SPORE,
    level: Level,
    owner: Plant? = null,
) : PlantProjectile(type, level, owner,
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

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(
            PazServerParticles.SPORE_HIT,
            amount = 18,
            speed = 0.6
        )
    }

}