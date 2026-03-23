package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import joshxviii.plantz.entity.zombie.DiscoZombie.SummonBackupGoal
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.Goal.getServerLevel
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3
import kotlin.math.max
import kotlin.math.min

class AllStar(type: EntityType<out AllStar>, level: Level) : PazZombie(type, level) {
    companion object {
        val CHARGE_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(AllStar::class.java, EntityDataSerializers.INT)
    }

    init {
        xpReward = 12
    }

    override fun isSunSensitive(): Boolean = false

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.ALL_STAR_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.ALL_STAR_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.ALL_STAR_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    val chargeAnimation : AnimationState = AnimationState()
    var chargingTime: Int
        get() = this.entityData.get(CHARGE_TIME_ID)
        set(value) = this.entityData.set(CHARGE_TIME_ID, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(CHARGE_TIME_ID, 0)
    }

    override fun tick() {
        super.tick()
        val level = level()
        if(chargingTime>0) {
            chargeAnimation.startIfStopped(tickCount)
            if (level is ServerLevel) level.sendParticles(
                BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockPosition().below())),
                x, y + 0.05, z, 1, 0.0, 0.0, 0.0, 0.6
            )
            chargingTime++
        }
        if (chargingTime>30) {
            chargeAnimation.stop()
            chargingTime=0
        }
    }

    override fun getSpeed(): Float {
        return super.getSpeed() * if (chargingTime>0) 3 else 1
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(1, ChargeGoal(this))
    }

    override fun isBaby(): Boolean = false
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {}
    override fun canPickUpLoot(): Boolean = false

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun convertsInWater(): Boolean = false

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        var groupData = groupData
        val random = level.random
        groupData = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        val difficultyModifier = difficulty.specialMultiplier
        if (spawnReason != EntitySpawnReason.CONVERSION) {
            setCanBreakDoors(true)

            setItemSlot(EquipmentSlot.HEAD, PazItems.FOOTBALL_HELMET.asItem().defaultInstance)
        }

        return groupData
    }


    private class ChargeGoal(
        val allStar: AllStar,
    ) : Goal() {
        companion object {
            const val CHARGE_DELAY_TIME = 60
        }
        var chargeTime = allStar.random.nextInt(20,60)

        override fun canUse(): Boolean {
            if (allStar.chargingTime>0) return true
            allStar.target?.distanceTo(allStar)?.let { if (it < 2f) return false }
            return allStar.isAggressive && !allStar.isDeadOrDying
        }

        override fun tick() {
            super.tick()
            if (--chargeTime == 0) {
                allStar.playSound(SoundEvents.WIND_CHARGE_BURST.value(), 1.5f, 1.3f)
                allStar.chargingTime=1
            }
            if (chargeTime<-8) chargeTime = CHARGE_DELAY_TIME + allStar.random.nextInt(40)
        }
    }

}