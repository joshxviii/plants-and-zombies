package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazTags.EntityTypes.WALLNUT_DEFLECTABLE
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.projectile.arrow.AbstractArrow
import net.minecraft.world.entity.projectile.arrow.ThrownTrident
import net.minecraft.world.level.Level

class WallNut(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.WALL_NUT, level) {

    override fun attackGoals() {}

    // solid collision
    override fun canBeCollidedWith(other: Entity?): Boolean = this.isAlive && other != attachedEntity

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        source.directEntity?.let {
            if (it.`is`(WALLNUT_DEFLECTABLE)) return false
        }
        return super.hurtServer(level, source, damage)
    }

    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        val reducedDamage = if (source.entity is Zombie) damage*0.5f else damage
        super.actuallyHurt(level, source, reducedDamage)
    }
}