package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class ZombieYeti(type: EntityType<out ZombieYeti>, level: Level) : Zombie(type, level) {

    companion object {
        fun checkZombieYetiSpawnRules(
            type: EntityType<out Mob>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val below = pos.below()
            return level.difficulty != Difficulty.PEACEFUL
                    && (EntitySpawnReason.ignoresLightRequirements(spawnReason) || isDarkEnoughToSpawn(level, pos, random))
                    && checkMobSpawnRules(type, level, spawnReason, pos, random) || level.getBlockState(below).`is`(PazTags.BlockTags.YETI_SPAWNABLE_ON)
                    && pos.y > level.seaLevel
        }
    }

    init {
       xpReward = 15
    }

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

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val random = level.random
        if (spawnReason != EntitySpawnReason.CONVERSION) {

            if (random.nextFloat() < 0.08 && getItemBySlot(EquipmentSlot.HEAD).isEmpty) {
                setItemSlot(EquipmentSlot.HEAD, Items.BUCKET.defaultInstance)
            }

            if (random.nextFloat() < 0.05) {
                val polarBear = EntityType.POLAR_BEAR.create(level(), EntitySpawnReason.JOCKEY)
                if (polarBear != null) {
                    polarBear.snapTo(x, y, z, yRot, 0.0f)
                    polarBear.finalizeSpawn(level, difficulty, EntitySpawnReason.JOCKEY, null)
                    startRiding(polarBear, false, false)
                    level.addFreshEntity(polarBear)
                }
            }
        }

        return groupData
    }
}