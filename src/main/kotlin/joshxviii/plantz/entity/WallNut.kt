package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.level.Level
import java.util.*

class WallNut(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.WALL_NUT, level) {
    
    override fun registerGoals() {
        this.targetSelector.addGoal(1, HurtByTargetGoal(this).setAlertOthers())
    }

    override fun snapSpawnRotation(): Boolean = true

    // solid collision
    override fun canBeCollidedWith(other: Entity?): Boolean = this.isAlive
}