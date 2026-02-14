package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.plant.ScaredyShroom.Companion.HIDING_FLAG
import joshxviii.plantz.entity.projectile.Butter
import joshxviii.plantz.entity.projectile.Kernel
import joshxviii.plantz.entity.projectile.Melon
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec2

class KernelPult(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.KERNEL_PULT, level) {

    companion object {
        val HAS_BUTTER: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(KernelPult::class.java, EntityDataSerializers.BOOLEAN)
    }

    var hasButterShot: Boolean
        get() = this.entityData.get(HAS_BUTTER)
        set(value) = this.entityData.set(HAS_BUTTER, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(HAS_BUTTER, false)
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = {
                if (hasButterShot) Butter(level(), this, spawnOffset = Vec2(-1f, 1f))
                else Kernel(level(), this, spawnOffset = Vec2(-1f, 1f))
            },
            velocity = 0.7,
            useHighArc = true,
            cooldownTime = 30,
            actionDelay = 12,
            actionStartEffect = { hasButterShot = random.nextFloat() < 0.25 }))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, false, false) { target, level ->
            target is Enemy
            && target !is Creeper
            && target !is Plant
        })
    }
}