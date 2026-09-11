package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.Pea
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level

class PeaShooter(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.PEA_SHOOTER, level) {
    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = { Pea(level(), this)},
            cooldownTime = 19,
            actionDelay = 3))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && target !is Creeper
                    && (target is Zombie
                    || (target is Enemy && isTame))
        })
    }

    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        super.actuallyHurt(level, source, damage)
        if (source.`is`(DamageTypes.LIGHTNING_BOLT)) {
            convertToPlantType(PazEntities.ELECTRIC_PEA_SHOOTER)
        }
    }
}
