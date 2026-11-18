package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazDataSerializers.BROWN_COAT_VARIANT
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazTags
import joshxviii.plantz.entity.ZombieEquipment.equipZombieEquipment
import joshxviii.plantz.raid.BossType
import joshxviii.plantz.raid.WaveType
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.StructureTags
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrDefault

class BrownCoat(type: EntityType<out BrownCoat>, level: Level) : PazZombie(type, level) {
    companion object {
        val DATA_VARIANT_ID: EntityDataAccessor<BrownCoatVariant> = SynchedEntityData.defineId(BrownCoat::class.java, BROWN_COAT_VARIANT)
    }

    init {
        xpReward = 3
    }

    var transformationTrap: Boolean = false
        set(value) {
            if (value == field) return
            field = value
            if (value) goalSelector.addGoal(1, transformationTrapGoal)
            else goalSelector.removeGoal(transformationTrapGoal)
        }
    private val transformationTrapGoal = TransformationTrapGoal()

    private inner class TransformationTrapGoal : Goal() {
        override fun canUse(): Boolean = level().hasNearbyAlivePlayer(x, y, z, 10.0)
        override fun tick() {
            val level = level() as? ServerLevel ?: return
            transformationTrap = false
            EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED)?.let { bolt -> bolt.snapTo(x, y, z); bolt.setVisualOnly(true); level.addFreshEntity(bolt) }
            convertToSuperBrainz()
        }
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

    override fun thunderHit(level: ServerLevel, lightningBolt: LightningBolt) {
        super.thunderHit(level, lightningBolt)
        convertToSuperBrainz()
    }

    fun convertToSuperBrainz() {
        convertTo(PazEntities.SUPER_BRAINZ, ConversionParams.single(this, true, true)) {
            it.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED)// TODO custom sounds
            it.heal(it.maxHealth)
        }
    }

    override fun isBaby(): Boolean = isBabyZombie()
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {
        randomEquip(random, difficulty)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("variant", BrownCoatVariant.CODEC, variant)
        output.putBoolean("transformation_trap", transformationTrap)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        variant = input.read<BrownCoatVariant>("variant", BrownCoatVariant.CODEC).getOrDefault(BrownCoatVariant.getDefault())
        transformationTrap = input.getBooleanOr("transformation_trap", false)
    }

    override fun canFreeze(): Boolean {
        return if (variant == BrownCoatVariant.SNOWCOAT) false
        else super.canFreeze()
    }

    fun spawnWithBalloons() {
        var balloonCount = random.nextIntBetweenInclusive(2,3)
        if (variant == BrownCoatVariant.BROWNCOAT && random.nextFloat() < 0.33) {
            variant = BrownCoatVariant.BALLOON
            balloonCount+=2
        }
        val balloonColors = when (variant) {
            BrownCoatVariant.DESERT -> listOf(DyeColor.ORANGE, DyeColor.BROWN)
            BrownCoatVariant.SNOWCOAT -> listOf(DyeColor.LIGHT_BLUE, DyeColor.WHITE)
            BrownCoatVariant.BUCCANEER -> listOf(DyeColor.BLACK, DyeColor.RED)
            else -> listOf(DyeColor.RED, DyeColor.YELLOW, DyeColor.BLUE)
        }
        spawnBalloons(balloonCount, balloonColors)
    }

    override fun onSpawnedFromItem() {
        if (variant == BrownCoatVariant.BALLOON) spawnWithBalloons()
        if (variant == BrownCoatVariant.BUCCANEER) WaveType.configurePirateInvasion(this)
    }

    override fun getTypeName(): Component {
        return if (variant != BrownCoatVariant.BROWNCOAT) Component.translatable("entity.plantz.browncoat.variant.${variant.name.lowercase()}")
        else super.getTypeName()
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        if (spawnReason == EntitySpawnReason.NATURAL && level.random.nextFloat() < 0.001f + difficulty.specialMultiplier * 0.01f) {
            transformationTrap = true// super brainz trap chance
        }
        val random = level.random
        setCanBreakDoors(true)
        val structureManager = (level as ServerLevel).structureManager()
        val isShipwreckSpawn = structureManager.getStructureWithPieceAt(blockPosition(), StructureTags.SHIPWRECK).isValid

        if (spawnReason != EntitySpawnReason.EVENT) equipZombieEquipment(random, difficulty.specialMultiplier + 0.1f)

        if (spawnReason != EntitySpawnReason.SPAWN_ITEM_USE) {
            val spawnBalloons = (spawnReason != EntitySpawnReason.EVENT && spawnReason != EntitySpawnReason.REINFORCEMENT && y > level.seaLevel && random.nextFloat() < 0.02)
            if (spawnBalloons) spawnWithBalloons()

            variant = BrownCoatVariant.pickForBiome(
                level.getBiome(blockPosition()).`is`(PazTags.Biomes.HAS_BROWNCOAT_SNOW),
                level.getBiome(blockPosition()).`is`(PazTags.Biomes.HAS_BROWNCOAT_DESERT),
                level.getBiome(blockPosition()).`is`(PazTags.Biomes.HAS_BROWNCOAT_BEACH),
                isShipwreckSpawn,
                random
            )
        }

        return data
    }


}
