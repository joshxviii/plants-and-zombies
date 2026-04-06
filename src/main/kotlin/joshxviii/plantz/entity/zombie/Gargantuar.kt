package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazSounds
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.phys.Vec3

class Gargantuar(type: EntityType<out Gargantuar>, level: Level) : PazZombie(type, level) {

    val imp: Imp = Imp(PazEntities.IMP, level())

    init {
        xpReward = 200
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(2, ThrowImpGoal(this))
    }

    private val noMoveControl = object : MoveControl(this) { override fun getSpeedModifier(): Double = 0.0 }

    private val noLookControl = object : LookControl(this) {}

    override fun getLookControl(): LookControl =  if (tickCount < 60) noLookControl else super.getLookControl()
    override fun getMoveControl(): MoveControl = if (tickCount < 60) noMoveControl else super.getMoveControl()

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
//                ParticleTypes.CLOUD,
                BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(BlockPos.containing(pos).below())),
                pos.x, pos.y, pos.z,
                5, 0.3, 0.0, 0.3, 0.0
            )
        }
    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.GARGANTUAR_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.GARGANTUAR_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.GARGANTUAR_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.WARDEN_STEP
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
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))

//        imp.snapTo(x, y, z, yRot, 0.0f)
//        imp.finalizeSpawn(level, level.getCurrentDifficultyAt(this.blockPosition()), EntitySpawnReason.JOCKEY, null)
//        imp.startRiding(this, true, false)
//        level.addFreshEntity(imp)

        return data
    }

    private class ThrowImpGoal(
        val gargantuar: Gargantuar,
    ) : Goal() {
        override fun canUse(): Boolean {
            return gargantuar.firstPassenger == gargantuar.imp
        }

        override fun tick() {

        }

        fun throwImp() {

        }

    }

}