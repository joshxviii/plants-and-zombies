package joshxviii.plantz.entity.zombie

import joshxviii.plantz.*
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.entity.zombie.Gargantuar.SmashAttackGoal.Companion.ATTACK_DELAY_TIME
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.util.random.WeightedList
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.vehicle.boat.AbstractBoat
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.SimpleExplosionDamageCalculator
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3
import java.util.*

class Gargantuar(type: EntityType<out Gargantuar>, level: Level) : PazZombie(type, level) {

    companion object {
        val SMASH_ATTACK_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Gargantuar::class.java, EntityDataSerializers.INT)
        val THROW_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Gargantuar::class.java, EntityDataSerializers.INT)
        val HAS_IMP_ID: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(Gargantuar::class.java, EntityDataSerializers.BOOLEAN)
    }

    init {
        xpReward = 200
    }

    val smashAttackAnimation : AnimationState = AnimationState()
    val throwImpAnimation : AnimationState = AnimationState()

    var smashAttackTime: Int
        get() = this.entityData.get(SMASH_ATTACK_TIME_ID)
        set(value) = this.entityData.set(SMASH_ATTACK_TIME_ID, value)
    var throwTime: Int
        get() = this.entityData.get(THROW_TIME_ID)
        set(value) = this.entityData.set(THROW_TIME_ID, value)

    var hasImp: Boolean
        get() = this.entityData.get(HAS_IMP_ID)
        set(value) = this.entityData.set(HAS_IMP_ID, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(SMASH_ATTACK_TIME_ID, 0)
        entityData.define(THROW_TIME_ID, 0)
        entityData.define(HAS_IMP_ID, true)
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(1, SmashAttackGoal(this))
        goalSelector.addGoal(2, ThrowImpGoal(this))
    }

    override fun updateControlFlags() {
        val noController = this.controllingPassenger !is Mob || this.controllingPassenger?.`is`(PazTags.EntityTypes.ZOMBIE_RAIDERS) == true
        val notInBoat = this.vehicle !is AbstractBoat
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, noController)
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, noController && notInBoat)
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, noController)
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, noController)
    }

    private val noMoveControl = object : MoveControl(this) { override fun getSpeedModifier(): Double = 0.0 }
    private val noLookControl = object : LookControl(this) {}

    override fun getLookControl(): LookControl =  if (smashAttackTime>0) noLookControl else super.getLookControl()
    override fun getMoveControl(): MoveControl = if (smashAttackTime>0) noMoveControl else super.getMoveControl()
    override fun isWithinMeleeAttackRange(target: LivingEntity): Boolean = if (smashAttackTime>0) false else super.isWithinMeleeAttackRange(target)

    override fun tick() {
        super.tick()
        val level = level()

        if (tickCount < 26) {
            if(tickCount % 9 == 1) playSound(SoundEvents.ROOTED_DIRT_BREAK, 1.0f, 0.4f)
            if (level is ServerLevel) level.sendParticles(
                BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockPosition().below())),
                x, y + 0.05, z, 10, 0.6, 0.0, 0.6, 0.4
            )
        }
        if (tickCount in 43..<55) {
            if(tickCount==46) playSound(SoundEvents.ROOTED_DIRT_BREAK, 1.0f, 0.7f)
            val direction = calculateViewVector(0f, yBodyRot-35).scale(2.5)
            val pos = Vec3(
                direction.x + x,
                direction.y + y,
                direction.z + z
            )
            if (level is ServerLevel) level.sendParticles(
                BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(BlockPos.containing(pos).below())),
                pos.x, pos.y, pos.z,
                5, 0.3, 0.0, 0.3, 0.0
            )
        }

        if(smashAttackTime>0) {
            smashAttackAnimation.startIfStopped(tickCount)
            smashAttackTime++
        }
        if (smashAttackTime>40) {
            smashAttackAnimation.stop()
            smashAttackTime=0
        }

        if (throwTime>0) {
            throwImpAnimation.startIfStopped(tickCount)
            throwTime++
        }
        if (throwTime>60) {
            throwImpAnimation.stop()
            throwTime=0
        }
    }

    override fun emergingTime(): Int = 80
    override fun getSoundVolume(): Float =  2.5f

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.GARGANTUAR_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.GARGANTUAR_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.GARGANTUAR_DEATH
    }
    override fun playStepSound(pos: BlockPos, blockState: BlockState) {
        this.playSound(SoundEvents.WARDEN_STEP, 6.0f, 0.9f)
    }

    override fun isBaby(): Boolean = false
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {}
    override fun canPickUpLoot(): Boolean = false
    override fun randomizeReinforcementsChance() {}

    override fun getPassengerRidingPosition(passenger: Entity): Vec3 {
        val direction = calculateViewVector(0f, yBodyRot-180).scale(0.8)
        val pos = super.getPassengerRidingPosition(passenger)
        return pos.add(direction)
    }

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
        state = ZombieState.EMERGING
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))

//        imp.snapTo(x, y, z, yRot, 0.0f)
//        imp.finalizeSpawn(level, level.getCurrentDifficultyAt(this.blockPosition()), EntitySpawnReason.JOCKEY, null)
//        imp.startRiding(this, true, false)
//        level.addFreshEntity(imp)

        return data
    }

    private class SmashAttackGoal(
        val gargantuar: Gargantuar,
    ) : Goal() {
        companion object {
            const val ATTACK_DELAY_TIME = 40
            val SMASH_DAMAGE_CALCULATOR: ExplosionDamageCalculator = SimpleExplosionDamageCalculator(false, true, Optional.of(2.5f), Optional.ofNullable(null))
        }
        var attackTime = gargantuar.random.nextInt(10,20)

        override fun canUse(): Boolean {
            if (attackTime <= 0) return true
            return gargantuar.isAggressive && !gargantuar.isDeadOrDying && gargantuar.target.let { it != null && it.isAlive && gargantuar.hasLineOfSight(it) && it.distanceTo(gargantuar) < 6.75f }
        }

        override fun canContinueToUse(): Boolean {
            return attackTime > 1
        }

        override fun tick() {
            super.tick()
            if (--attackTime == 0) {
                gargantuar.target.let { if (it!=null) gargantuar.lookAt(it, 10f, 10f) }
                gargantuar.smashAttackTime=1
            }
            if (attackTime<-6) smashAttack()
        }

        private fun smashAttack() {
            attackTime = ATTACK_DELAY_TIME + gargantuar.random.nextInt(10)
            val target = gargantuar.target
            val level = gargantuar.level()
            val direction = gargantuar.calculateViewVector(0f, gargantuar.yBodyRotO-20).scale(3.5)
            val pos = Vec3(
                direction.x + gargantuar.x,
                direction.y + (target?.y?.coerceIn(gargantuar.y..gargantuar.y+5) ?: gargantuar.y),
                direction.z + gargantuar.z
            )
            level.explode(
                    gargantuar,
                    DamageSource(level.registryAccess().get(PazDamageTypes.ZOMBIE_SMASH).get(), gargantuar),
                    SMASH_DAMAGE_CALCULATOR, pos.x, pos.y, pos.z,
                    3.5f,
                    false,
                    Level.ExplosionInteraction.MOB,
                    ParticleTypes.LARGE_SMOKE,
                    ParticleTypes.EXPLOSION,
                    WeightedList.of(),
                    SoundEvents.WIND_CHARGE_BURST
                )
            if (level is ServerLevel) level.sendParticles(
                BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(BlockPos.containing(pos).below())),
                pos.x, pos.y, pos.z,
                50, 1.2, 0.4, 1.2, 0.2
            )
            if (level is ServerLevel) level.sendParticles(
                ParticleTypes.DUST_PLUME,
                pos.x, pos.y+0.2, pos.z,
                45, 1.0, 0.4, 1.0, 0.01
            )
            gargantuar.playSound(SoundEvents.MACE_SMASH_GROUND_HEAVY, 1.0f, 0.9f)
        }
    }

    private class ThrowImpGoal(
        val gargantuar: Gargantuar,
    ) : Goal() {
        companion object {
            const val THROW_DELAY_TIME = 60
        }
        var throwTime = THROW_DELAY_TIME + gargantuar.random.nextInt(20,30)

        override fun canUse(): Boolean {
            if (!gargantuar.hasImp) return false
            if (throwTime <= 0) return true
            return (gargantuar.health <= gargantuar.maxHealth*.5) && gargantuar.isAggressive && !gargantuar.isDeadOrDying && gargantuar.target.let {
                it != null
                && it.distanceTo(gargantuar) > 4.5f
            }
        }

        override fun tick() {
            if (--throwTime == 0) {
                gargantuar.target.let { if (it!=null) gargantuar.lookAt(it, 10f, 10f) }
                gargantuar.throwTime=1
            }
            if (throwTime<-17) throwImp()
        }

        fun throwImp() {
            throwTime = ATTACK_DELAY_TIME + gargantuar.random.nextInt(10)
            gargantuar.hasImp = false
            val level = gargantuar.level() as ServerLevel

            val direction = gargantuar.calculateViewVector(0f, gargantuar.yBodyRot+35).scale(2.4)
            val pos = Vec3(
                direction.x + gargantuar.x,
                direction.y + gargantuar.y+2.5f,
                direction.z + gargantuar.z
            )
            val imp = PazEntities.IMP.create(level, EntitySpawnReason.MOB_SUMMONED)?: return
            imp.snapTo(pos, gargantuar.yRot, 0.0f)
            imp.applyImpulse(direction.x, direction.y, direction.z, 1.0f, 0f)
            gargantuar.target.let { if (it!=null) imp.setLastHurtMob(it) }
            level.addFreshEntity(imp)
            level.gameEvent(GameEvent.ENTITY_PLACE, pos, GameEvent.Context.of(gargantuar))

        }

    }

}