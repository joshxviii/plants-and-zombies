package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import joshxviii.plantz.pazResource
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.phys.Vec3

class AllStar(type: EntityType<out AllStar>, level: Level) : PazZombie(type, level) {
    companion object {
        val CHARGE_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(AllStar::class.java, EntityDataSerializers.INT)
        val CHARGE_BOOST_ID: Identifier = pazResource("charge_boost")
    }

    init {
        xpReward = 12
    }

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
        entityData.define(CHARGE_TIME_ID, -1)
    }

    override fun tick() {
        super.tick()
        val level = level()
        if (chargingTime >= 0) {
            val direction = lookAngle
            val chargeSpeed = 0.4
            deltaMovement = Vec3(direction.x * chargeSpeed, deltaMovement.y, direction.z * chargeSpeed)
            chargeAnimation.startIfStopped(tickCount)
            if (level is ServerLevel) {
                level.sendParticles(
                    BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockPosition().below())),
                    x, y + 0.05, z, 1, 0.0, 0.0, 0.0, 0.6
                )
                if (tickCount%2==0) level.sendParticles(
                    ParticleTypes.WHITE_SMOKE,
                    x, eyeHeight+y, z, 2, 0.1, 0.2, 0.1, 0.05
                )
            }
            if (chargingTime==0) removeChargeBoost()
            chargingTime--
        }
        else chargeAnimation.stop()
    }

    override fun doPush(entity: Entity) {
        super.doPush(entity)
        if (chargingTime>=0 && entity is LivingEntity) {
            entity.knockback(
                0.7,
                position().x - entity.position().x,
                position().z - entity.position().z
            )
        }
    }

    fun removeChargeBoost() {
        getAttribute(Attributes.KNOCKBACK_RESISTANCE)!!.removeModifier(CHARGE_BOOST_ID)
//        getAttribute(Attributes.MOVEMENT_SPEED)!!.removeModifier(CHARGE_BOOST_ID)
        getAttribute(Attributes.ATTACK_KNOCKBACK)!!.removeModifier(CHARGE_BOOST_ID)
    }
    fun applyChargeBoost() {
        getAttribute(Attributes.KNOCKBACK_RESISTANCE)!!
            .addTransientModifier(AttributeModifier(CHARGE_BOOST_ID, 99.0, AttributeModifier.Operation.ADD_VALUE))
//        getAttribute(Attributes.MOVEMENT_SPEED)!!
//            .addTransientModifier(AttributeModifier(CHARGE_BOOST_ID, 0.12, AttributeModifier.Operation.ADD_VALUE))
        getAttribute(Attributes.ATTACK_KNOCKBACK)!!
            .addTransientModifier(AttributeModifier(CHARGE_BOOST_ID, 4.0, AttributeModifier.Operation.ADD_VALUE))
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

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))
        val random = level.random
        val difficultyModifier = difficulty.specialMultiplier
        if (spawnReason != EntitySpawnReason.CONVERSION) {
            setCanBreakDoors(true)
            setItemSlot(EquipmentSlot.HEAD, PazItems.FOOTBALL_HELMET.asItem().defaultInstance)
        }

        return data
    }


    private class ChargeGoal(
        val allStar: AllStar,
    ) : Goal() {
        companion object {
            const val CHARGE_DELAY_TIME = 80
        }
        var chargeDelayTime = allStar.random.nextInt(40,50)

        override fun canUse(): Boolean {
            allStar.target?.distanceTo(allStar)?.let { if (it < 1.5f) return false }
            return allStar.isAggressive && !allStar.isDeadOrDying
        }

        override fun stop() {
            super.stop()
            allStar.chargingTime = 0
        }

        override fun tick() {
            super.tick()
            if (--chargeDelayTime == 0) {
                allStar.applyChargeBoost()
                allStar.playSound(SoundEvents.WIND_CHARGE_BURST.value(), 1.5f, 1.3f)
                allStar.playSound(PazSounds.ALL_STAR_WHISTLE)
                allStar.chargingTime = 30
                chargeDelayTime = CHARGE_DELAY_TIME + allStar.random.nextInt(50)
            }
        }
    }

}