package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackPlantGoal
import joshxviii.plantz.entity.projectile.Melon
import joshxviii.plantz.entity.projectile.Pea
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class MelonPult(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.MELON_PULT, level) {

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackPlantGoal(
            plantEntity = this,
            projectileFactory = { Melon(level = level(), owner = this, spawnOffset = Vec2(-1f, 1f))},
            velocity = 1.0,
            directionOffset = Vec3(0.0, 1.0, 0.0),
            useHighArc = true,
            cooldownTime = 70,
            actionDelay = 12))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
        })
    }
}