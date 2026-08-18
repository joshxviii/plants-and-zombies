package joshxviii.plantz.entity.blueprint_machines

import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.LaserBullet
import joshxviii.plantz.entity.zombie.ZombieRobot
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec2

class ZombieTurret(type: EntityType<out ZombieTurret>, level: Level) : ZombieRobot(type, level) {

    companion object {
    }

    override fun tick() {
        super.tick()

        if (actionTime>0) {
            actionAnimation.startIfStopped(tickCount)
            if (actionTime++>10) {
                actionAnimation.stop()
                actionTime=0
            }
        }
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(1, ProjectileAttackGoal(
            usingEntity = this,
            velocity = 1.1,
            actionDelay = 5,
            inaccuracy = 5.2f,
            leadShots = false,
            projectileFactory = { LaserBullet(level(), this, spawnOffset = Vec2(0.2f, 0.4f)) },
            usePredicate = { actionTime<=0 },
            actionStartEffect = { actionTime=1 }
        ))
    }

}