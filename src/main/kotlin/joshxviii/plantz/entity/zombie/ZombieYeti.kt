package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazSounds
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class ZombieYeti(type: EntityType<out ZombieYeti>, level: Level) : Zombie(type, level) {

    override fun isBaby(): Boolean = false
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {}
    override fun canPickUpLoot(): Boolean = false

    override fun isSunSensitive(): Boolean {
        return false
    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.ZOMBIE_YETI_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.ZOMBIE_YETI_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.ZOMBIE_YETI_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)

        return result
    }

    override fun convertsInWater(): Boolean {
        return false
    }

    override fun doUnderWaterConversion(level: ServerLevel) {
        this.convertToZombieType(level, EntityType.ZOMBIE)
        if (!this.isSilent) {
            level.levelEvent(null, 1041, this.blockPosition(), 0)
        }
    }

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

            if (random.nextFloat() < 0.08f) {
                this.setItemSlot(EquipmentSlot.HEAD, Items.BUCKET.defaultInstance)
            }

            if (random.nextFloat() < 0.05) {
                val polarBear = EntityType.POLAR_BEAR.create(this.level(), EntitySpawnReason.JOCKEY)
                if (polarBear != null) {
                    polarBear.snapTo(this.x, this.y, this.z, this.yRot, 0.0f)
                    polarBear.finalizeSpawn(level, difficulty, EntitySpawnReason.JOCKEY, null)
                    this.startRiding(polarBear, false, false)
                    level.addFreshEntity(polarBear)
                }
            }
        }

        return groupData
    }
}