package joshxviii.plantz.entity.zombie

import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class Zombat(type: EntityType<out Zombat>, level: Level) : PazZombie(type, level) {

    companion object {

    }

    init {
    }

    var barronId: UUID? = null

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.storeNullable("baron_id", UUIDUtil.CODEC , barronId)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        barronId = input.read("baron_id", UUIDUtil.CODEC).getOrNull()
    }

    override fun registerGoals() {
        super.registerGoals()
    }

    override fun handleAttributes(difficultyModifier: Float, spawnReason: EntitySpawnReason) {}

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        barronId?.let {
            level.getEntity(it)?.hurtServer(level, source, damage)
        }
        return super.hurtServer(level, source, damage)
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun tick() {
        super.tick()
    }

    override fun remove(reason: RemovalReason) {
        super.remove(reason)
    }

    override fun canEquipBalloons(): Boolean = false
    override fun canEquipDuckyInWater(): Boolean = false
    override fun canPickUpLoot(): Boolean = false
    override fun canBreakDoors(): Boolean = false
    override fun canHoldItem(itemStack: ItemStack): Boolean = false
    override fun getStepSound(): SoundEvent = SoundEvents.EMPTY
    override fun getVoicePitch(): Float = 1.9f
    override fun getSoundVolume(): Float = 0.7f

    override fun getDefaultGravity(): Double = 0.0
    override fun checkFallDamage(ya: Double, onGround: Boolean, onState: BlockState, pos: BlockPos) {}

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        isFlying = true
        return data
    }
}