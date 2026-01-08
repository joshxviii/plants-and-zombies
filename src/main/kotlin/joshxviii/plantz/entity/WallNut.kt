package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.projectile.arrow.AbstractArrow
import net.minecraft.world.level.Level
import java.util.*

class WallNut(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.WALL_NUT, level) {

    override fun attackGoals() {}
    override fun snapSpawnRotation(): Boolean = true

    // solid collision
    override fun canBeCollidedWith(other: Entity?): Boolean = this.isAlive

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        return if (source.directEntity is AbstractArrow) false
        else super.hurtServer(level, source, damage)
    }
}