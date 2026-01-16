package joshxviii.plantz.entity.plants

import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.arrow.AbstractArrow
import net.minecraft.world.entity.projectile.arrow.ThrownTrident
import net.minecraft.world.level.Level

class WallNut(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.WALL_NUT, level) {

    override fun attackGoals() {}

    // solid collision
    override fun canBeCollidedWith(other: Entity?): Boolean = this.isAlive

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        val entity = source.directEntity
        return if (entity is AbstractArrow && entity !is ThrownTrident) false
        else super.hurtServer(level, source, damage)
    }
}