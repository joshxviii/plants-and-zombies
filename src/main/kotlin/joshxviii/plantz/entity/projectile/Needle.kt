package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.plants.Plant
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2

class Needle(
    type: EntityType<out PlantProjectile> = PazEntities.NEEDLE,
    level: Level,
    spawnOffset: Vec2 = Vec2.ZERO,
    owner: Plant? = null,
) : PlantProjectile(type, level, owner, spawnOffset,
    PazDamageTypes.PLANT
) {
    override fun stickInGroundTime(): Int = 100
    override fun getPierceLevel(): Byte = 4

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
    }
}