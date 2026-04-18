package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazSounds
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.*
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class Imp(type: EntityType<out Imp> = PazEntities.IMP, level: Level) : PazZombie(type, level) {

    init {

    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.IMP_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.IMP_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.IMP_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    override fun isBaby(): Boolean = true
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {}
    override fun canPickUpLoot(): Boolean = false

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val wasHurt = super.doHurtTarget(level, target)
        if (wasHurt && target is LivingEntity) {
            val toxicTime = when (level().difficulty) {
                Difficulty.NORMAL -> 8
                Difficulty.HARD -> 15
                else -> 0
            }
            if (random.nextFloat() > 0.25) target.addEffect(MobEffectInstance(PazEffects.TOXIC, toxicTime * 20, 0), this)
        }
        return wasHurt
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        val random = level.random
        val difficultyModifier = difficulty.specialMultiplier
        if (spawnReason != EntitySpawnReason.CONVERSION) {
            setCanPickUpLoot(false)
            setCanBreakDoors(true)

            //getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE)?.addPermanentModifier(AttributeModifier(pazResource("browncoat"), this.random.nextDouble() * 10.25 + 0.5, AttributeModifier.Operation.ADD_VALUE))

            if (getItemBySlot(EquipmentSlot.HEAD).isEmpty){
                if (random.nextFloat() < 0.05) {
                    setItemSlot(EquipmentSlot.HEAD, PazBlocks.CONE.asItem().defaultInstance)
                }
                else if (random.nextFloat() < 0.01 && getItemBySlot(EquipmentSlot.HEAD).isEmpty) {
                    setItemSlot(EquipmentSlot.HEAD, Items.BUCKET.defaultInstance)
                }
            }
        }

        return data
    }
}