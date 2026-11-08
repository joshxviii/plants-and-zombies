package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazTags
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.FluidTags
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.vehicle.boat.AbstractBoat
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import java.util.*
import kotlin.jvm.optionals.getOrNull

class Zombat(type: EntityType<out Zombat>, level: Level) : PazZombie(type, level) {

    companion object {
        fun checkZombatSpawnRules(
            type: EntityType<out Mob>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            if (level.difficulty == Difficulty.PEACEFUL) return false
            return checkMobSpawnRules(type, level, spawnReason, pos, random)
        }

        val BARON_BAT_ID = SynchedEntityData.defineId(Zombat::class.java, EntityDataSerializers.BOOLEAN)
    }

    init {
    }

    var isBaronBat: Boolean
        get() = entityData.get(BARON_BAT_ID)
        set(value) = entityData.set(BARON_BAT_ID, value)

    var baronId: UUID? = null

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(BARON_BAT_ID, false)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.storeNullable("baron_id", UUIDUtil.CODEC , baronId)
        output.putBoolean("baron_bat", isBaronBat)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        baronId = input.read("baron_id", UUIDUtil.CODEC).getOrNull()
        isBaronBat = input.getBooleanOr("baron_bat", entityData.get(BARON_BAT_ID))
    }

    override fun getTypeName(): Component {
        if (isBaronBat) return PazEntities.BARON_VON_BATS.description
        return super.getTypeName()
    }

    override fun registerGoals() {
        super.registerGoals()
    }

    override fun updateControlFlags() {
        val noController = this.controllingPassenger !is Mob || this.controllingPassenger?.`is`(PazTags.EntityTypes.ZOMBIE_RAIDERS) == true
        val notInBoat = this.vehicle !is AbstractBoat
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, noController)
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, noController && notInBoat)
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, noController)
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, noController)
    }

    override fun handleAttributes(difficultyModifier: Float, spawnReason: EntitySpawnReason) {}

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        val baron = if (isBaronBat) baronId?.let { level.getEntity(it) as? BaronVonBats } else null
        if (baron != null) {
            val baronWasHurt = baron.hurtAsBaronBat(level, source, damage)
            val batWasHurt = super.hurtServer(level, source, 0f)
            return baronWasHurt || batWasHurt
        }
        else return super.hurtServer(level, source, damage)
    }

    override fun heal(amount: Float) {
        super.heal(amount)
        baronId?.let { id ->
            ((level() as? ServerLevel)?.getEntity(id) as? BaronVonBats)?.heal(amount)
        }
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        if (result) heal(maxHealth * 0.1f)
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