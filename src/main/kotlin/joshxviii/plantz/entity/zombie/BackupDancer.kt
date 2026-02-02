package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazSounds
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
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.phys.Vec3

class BackupDancer(type: EntityType<out BackupDancer> = PazEntities.BACKUP_DANCER, level: Level, position: Vec3? = null, rotation: Float? = null) : PazZombie(type, level) {

    init {
        if (position != null) setPos(position)
        if (rotation != null) {
            yRot = rotation
            yBodyRot = rotation
            yHeadRot = rotation
        }
    }

    private val noMoveControl = object : MoveControl(this) {
        override fun getSpeedModifier(): Double = 0.0
    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.DISCO_ZOMBIE_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.DISCO_ZOMBIE_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.DISCO_ZOMBIE_DEATH
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
    override fun isSunSensitive(): Boolean = false
    override fun convertsInWater(): Boolean = false
    override fun randomizeReinforcementsChance() {}

    override fun getMoveControl(): MoveControl {
        if (tickCount < 40) return noMoveControl
        return super.getMoveControl()
    }

    override fun tick() {
        super.tick()
        val level = level()
        if (tickCount < 15) {// dig out of ground animation
            if (level is ServerLevel) level.sendParticles(
                BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockPosition().below())),
                x, y + 0.05, z, 8, 0.25, 0.0, 0.25, 0.4
            )
        }
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))
        setCanBreakDoors(false)

        return data
    }
}