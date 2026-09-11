package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazTags.EntityTypes.WALLNUT_DEFLECTABLE
import joshxviii.plantz.entity.Sun
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class WallNut(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.WALL_NUT, level) {

    companion object {
        fun wallNutCollision(wallnut: Plant, other: Entity?): Boolean {
            if (other is Zombie && other.swingTime == 0) {// when colliding with a zombie, the zombie will attack the wallnut
                val level = other.level() as? ServerLevel
                if (level != null && other.isAlive) {
                    val damage = other.getAttribute(Attributes.ATTACK_DAMAGE)?.value?.toFloat() ?: 1f
                    if (wallnut.hurtServer(level, other.damageSources().mobAttack(other), damage)) {
                        other.swing(InteractionHand.MAIN_HAND)
                    }
                }
            }
            if (other is Sun) return false
            return wallnut.isAlive && other != wallnut.attachedEntity
        }
    }

    override fun attackGoals() {}

    override fun canBeCollidedWith(other: Entity?): Boolean = wallNutCollision(this, other)

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        source.directEntity?.let {
            if (it.`is`(WALLNUT_DEFLECTABLE)) return false
        }
        return super.hurtServer(level, source, damage)
    }

    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        val reducedDamage = if (source.entity is Zombie) damage*0.666f else damage
        super.actuallyHurt(level, source, reducedDamage)
    }

    override fun canSurviveOn(block: BlockState): Boolean {
        return super.canSurviveOn(block) || !block.getCollisionShape(level(), blockPosition().below()).isEmpty
    }
}