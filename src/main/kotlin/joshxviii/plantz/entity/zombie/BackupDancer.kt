package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazSounds
import joshxviii.plantz.ai.ZombieState
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class BackupDancer(type: EntityType<out BackupDancer>, level: Level) : PazZombie(type, level) {

    init {

    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.BACKUP_DANCER_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.BACKUP_DANCER_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.BACKUP_DANCER_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    override fun handleAttributes(difficultyModifier: Float, spawnReason: EntitySpawnReason) {}

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun isBaby(): Boolean = false
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {}
    override fun canPickUpLoot(): Boolean = false
    override fun randomizeReinforcementsChance() {}

    override fun tick() {
        super.tick()
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        state = ZombieState.EMERGING
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))
        setCanBreakDoors(false)

        return data
    }
}