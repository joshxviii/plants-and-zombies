package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.Plant
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class Pea(
    type: EntityType<out PlantProjectile> = PazEntities.PEA,
    level: Level,
    owner: Plant? = null,
) : PlantProjectile(type, level, owner) {

}