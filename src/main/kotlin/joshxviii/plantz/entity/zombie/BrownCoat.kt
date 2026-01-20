package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazSounds
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class BrownCoat(type: EntityType<out BrownCoat>, level: Level) : Zombie(type, level) {
    override fun isSunSensitive(): Boolean {
        return false
    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.BROWNCOAT_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.BROWNCOAT_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.BROWNCOAT_DEATH
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
        var groupData = groupData
        val random = level.random
        groupData = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        val difficultyModifier = difficulty.specialMultiplier
        if (spawnReason != EntitySpawnReason.CONVERSION) {
            setCanPickUpLoot(random.nextFloat() < 0.55f * difficultyModifier)

            if (getItemBySlot(EquipmentSlot.HEAD).isEmpty){
                if (random.nextFloat() < 0.25) {
                    setItemSlot(EquipmentSlot.HEAD, PazBlocks.CONE.asItem().defaultInstance)
                }
                if (random.nextFloat() < 0.1 && getItemBySlot(EquipmentSlot.HEAD).isEmpty) {
                    setItemSlot(EquipmentSlot.HEAD, Items.BUCKET.defaultInstance)
                }
            }
        }

        return groupData
    }
}