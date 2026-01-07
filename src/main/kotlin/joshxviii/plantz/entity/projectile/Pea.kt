package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

class Pea(
    type: EntityType<out PlantProjectile> = PazEntities.PEA,
    level: Level,
    owner: Plant? = null,
) : PlantProjectile(type, level, owner,
    DamageTypes.MOB_PROJECTILE,
    PazParticles.PEA_HIT
) {

}