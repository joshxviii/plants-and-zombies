package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.Pea
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level

class Repeater(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.REPEATER, level) {

    companion object {
        val ACTION_COUNT: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Repeater::class.java, EntityDataSerializers.INT)
    }

    var actionCount: Int
    get() = this.entityData.get(ACTION_COUNT)
    set(value) = this.entityData.set(ACTION_COUNT, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(ACTION_COUNT, 0)
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = { Pea(level(), this) },
            cooldownTime = 4,
            actionDelay = 3,
            actionEndEffect = {
                actionCount++
                if (actionCount >= 2) {
                    actionCount = 0
                    cooldown = 10
                }
            }))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && target !is Creeper
                    && (target is Zombie
                    || (target is Enemy && isTame))
        })
    }

    override fun getZenGrownSeedType(): EntityType<*> = if (random.nextFloat() < 0.6f) PazEntities.PEA_SHOOTER else super.getZenGrownSeedType()

}