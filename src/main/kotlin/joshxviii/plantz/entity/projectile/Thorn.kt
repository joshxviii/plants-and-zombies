package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.Plant
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.arrow.AbstractArrow
import net.minecraft.world.level.Level

class Thorn(
    type: EntityType<out PlantProjectile> = PazEntities.THORN,
    level: Level,
    owner: Plant? = null,
) : PlantProjectile(
    type, level, owner,
    DamageTypes.MOB_PROJECTILE,
) {

}