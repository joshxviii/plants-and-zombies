package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazDataSerializers.DATA_DYE_COLOR
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.PaintBall
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.*
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class ElectricSlide(type: EntityType<out ElectricSlide>, level: Level) : PazZombie(type, level) {

    companion object {

    }

    init {
        xpReward = 120
    }

    override fun shouldDiscardFriction(): Boolean {
        return super.shouldDiscardFriction()
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
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

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        setItemSlot(EquipmentSlot.FEET, PazItems.ROLLER_SKATES.defaultInstance)
        setDropChance(EquipmentSlot.FEET, 0.0f)
        return data
    }
}