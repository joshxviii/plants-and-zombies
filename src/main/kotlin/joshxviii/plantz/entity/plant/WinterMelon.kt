package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.FrozenMelon
import joshxviii.plantz.entity.projectile.Melon
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec2

class WinterMelon(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.WINTER_MELON, level) {

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = { FrozenMelon(level(), this, spawnOffset = Vec2(-1f, 1f))},
            useHighArc = true,
            velocity = 1.0,
            cooldownTime = 80,
            actionDelay = 12))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, false, false) { target, level ->
            target !is Plant
                    && target !is Creeper
                    && (target is Zombie
                    || (target is Enemy && isTame))
        })
    }

    override fun getZenGrownSeedType(): EntityType<*> = if (random.nextFloat() < 0.65f) PazEntities.MELON_PULT else super.getZenGrownSeedType()
}