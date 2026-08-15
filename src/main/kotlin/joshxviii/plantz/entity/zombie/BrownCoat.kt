package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazDataSerializers.BROWN_COAT_VARIANT
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.StructureTags
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrDefault

class BrownCoat(type: EntityType<out BrownCoat>, level: Level) : PazZombie(type, level) {
    companion object {
        val DATA_VARIANT_ID: EntityDataAccessor<BrownCoatVariant> = SynchedEntityData.defineId(BrownCoat::class.java, BROWN_COAT_VARIANT)
    }

    var variant: BrownCoatVariant
        get() = this.entityData.get(DATA_VARIANT_ID)
        set(value) = this.entityData.set(DATA_VARIANT_ID, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(DATA_VARIANT_ID, BrownCoatVariant.getDefault())
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun isBaby(): Boolean = isBabyZombie()
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {
        randomEquip(random, difficulty)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("variant", BrownCoatVariant.CODEC, variant)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        variant = input.read<BrownCoatVariant>("variant", BrownCoatVariant.CODEC).getOrDefault(BrownCoatVariant.getDefault())
    }

    override fun canFreeze(): Boolean {
        return if (variant == BrownCoatVariant.SNOW) false
        else super.canFreeze()
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
        setCanPickUpLoot(true)
        setCanBreakDoors(true)
        val structureManager = (level as ServerLevel).structureManager()
        val isShipwreckSpawn = structureManager.getStructureWithPieceAt(blockPosition(), StructureTags.SHIPWRECK).isValid
        variant = BrownCoatVariant.pickForBiome(
            level.getBiome(blockPosition()).`is`(PazTags.Biomes.HAS_BROWNCOAT_SNOW),
            level.getBiome(blockPosition()).`is`(PazTags.Biomes.HAS_BROWNCOAT_DESERT),
            level.getBiome(blockPosition()).`is`(PazTags.Biomes.HAS_BROWNCOAT_BEACH),
            isShipwreckSpawn,
            random
        )

        if (getItemBySlot(EquipmentSlot.HEAD).isEmpty){
            if (random.nextFloat() < 0.25) {
                setItemSlot(EquipmentSlot.HEAD, PazBlocks.CONE.asItem().defaultInstance)
                setDropChance(EquipmentSlot.HEAD, 0.2f)
            }
            else if (random.nextFloat() < 0.1 && getItemBySlot(EquipmentSlot.HEAD).isEmpty) {
                setItemSlot(EquipmentSlot.HEAD, Items.BUCKET.defaultInstance)
            }
        }

        return data
    }
}
