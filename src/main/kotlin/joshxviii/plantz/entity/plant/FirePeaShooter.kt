package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.PeaFire
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.chicken.Chicken
import net.minecraft.world.entity.animal.fish.AbstractFish
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class FirePeaShooter(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.FIRE_PEA_SHOOTER, level) {
    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = { PeaFire(level(), this) },
            cooldownTime = 20,
            actionDelay = 3))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && target !is Creeper
                    && target is Zombie
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame)
        })
    }

    override fun tick() {
        super.tick()

        if (tickCount % 3 == 0 && tickCount > 18 && isAlive) {

            val direction = calculateUpVector(this.xRot - 50, this.yHeadRot).scale(0.3)
            this.level().addParticle(
                PazServerParticles.EMBER,
                direction.x.toFloat() + this.getRandomX(0.2),
                direction.y.toFloat() + this.y + eyeHeight.toDouble() - 0.1,
                direction.z.toFloat() + this.getRandomZ(0.2),
                0.0, 0.0, 0.0,
            )

        }
    }
}
