package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazDataSerializers.ELECTRIC_SLIDE_VARIANT
import joshxviii.plantz.PazDataSerializers.ROBO_ZOMBIE_VARIANT
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Unit
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrDefault

class ElectricSlide(type: EntityType<out ElectricSlide>, level: Level) : PazZombie(type, level) {

    companion object {
        val DATA_VARIANT_ID: EntityDataAccessor<ElectricSlideVariant> = SynchedEntityData.defineId(ElectricSlide::class.java, ELECTRIC_SLIDE_VARIANT)
    }

    init {
        xpReward = 120
    }

    var variant: ElectricSlideVariant
        get() = this.entityData.get(DATA_VARIANT_ID)
        set(value) = this.entityData.set(DATA_VARIANT_ID, value)

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

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(DATA_VARIANT_ID, ElectricSlideVariant.getDefault())
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("variant", ElectricSlideVariant.CODEC, variant)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        variant = input.read("variant", ElectricSlideVariant.CODEC).getOrDefault(ElectricSlideVariant.getDefault())
    }

    override fun registerGoals() {
        super.registerGoals()
    }

    override fun addBehaviourGoals() {
        super.addBehaviourGoals()
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun getTypeName(): Component {
        return if (variant != ElectricSlideVariant.SLIDE) Component.translatable("entity.plantz.electric_slide.variant.${variant.name.lowercase()}")
        else super.getTypeName()
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        val skates = PazItems.ROLLER_SKATES.defaultInstance.apply { set(DataComponents.UNBREAKABLE, Unit.INSTANCE) }
        setItemSlot(EquipmentSlot.FEET, skates)
        setDropChance(EquipmentSlot.FEET, 0f)
        return data
    }
}