package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.Spore
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

class ScaredyShroom(type: EntityType<out Mushroom>, level: Level) : Mushroom(PazEntities.SCAREDY_SHROOM, level) {

    companion object {
        val HIDING_FLAG: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(ScaredyShroom::class.java, EntityDataSerializers.BOOLEAN)
    }

    var isHiding: Boolean
        get() = this.entityData.get(HIDING_FLAG)
        set(value) = this.entityData.set(HIDING_FLAG, value)

    private val noLookControl = object : LookControl(this) {}
    override fun getLookControl(): LookControl =  if (isHiding) noLookControl else super.getLookControl()

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(HIDING_FLAG, false)
    }

    override fun tick() {
        super.tick()
        if (level().isClientSide) {
            if (isHiding) specialAnimation.startIfStopped(0)
            else specialAnimation.stop()
        }
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = { Spore(level(), this) },
            cooldownTime = 20,
            actionDelay = 6,
            actionPredicate = {
                !isHiding
            }))
        this.goalSelector.addGoal(3, HideGoal(this, Zombie::class.java))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, _ ->
            target is Enemy
            && target !is Creeper
            && target !is Plant
        })
    }


    class HideGoal <T: LivingEntity> (
        val shroom: ScaredyShroom,
        val targetType: Class<T>,
        val selector: TargetingConditions.Selector? = null,
        var target: LivingEntity? = null,
    ) : Goal() {
        companion object {
            const val HIDE_DISTANCE: Double = 3.5
        }
        val targetConditions: TargetingConditions = TargetingConditions.forCombat().range(HIDE_DISTANCE).selector(selector)

        override fun start() {
            shroom.isHiding = true
        }
        override fun stop() {
            shroom.isHiding = false
        }

        override fun canUse(): Boolean {
            findTarget()
            return target != null && shroom.isAlive && !shroom.isAsleep
        }

        fun findTarget() {
            val level = getServerLevel(shroom)
            target = level.getNearestEntity<T>(
                shroom.level().getEntitiesOfClass<T>(targetType, getTargetSearchArea(HIDE_DISTANCE)) { true },
                targetConditions,
                shroom, shroom.x, shroom.eyeY, shroom.z
            )
        }

        fun getTargetSearchArea(distance: Double): AABB {
            return shroom.boundingBox.inflate(distance, distance, distance)
        }
    }

}