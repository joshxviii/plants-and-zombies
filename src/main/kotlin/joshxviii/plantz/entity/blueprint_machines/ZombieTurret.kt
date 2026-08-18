package joshxviii.plantz.entity.blueprint_machines

import it.unimi.dsi.fastutil.ints.IntList
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.LaserBullet
import joshxviii.plantz.entity.projectile.PaintBall
import joshxviii.plantz.entity.zombie.ZombieRobot
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.FireworkRocketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class ZombieTurret(type: EntityType<out ZombieTurret>, level: Level) : ZombieRobot(type, level) {

    companion object {

    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(1, ProjectileAttackGoal(
            usingEntity = this,
            velocity = 1.1,
            actionDelay = 10,
            inaccuracy = 0.15f,
            leadShots = false,
            projectileFactory = { LaserBullet(level(), this, spawnOffset = Vec2(0.2f, 0.5f)) }
        ))
    }

}