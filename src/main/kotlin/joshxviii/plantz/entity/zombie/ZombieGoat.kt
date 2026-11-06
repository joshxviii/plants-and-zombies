package joshxviii.plantz.entity.zombie
import joshxviii.plantz.PazTags
import joshxviii.plantz.raid.setIgnoreFlag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.vehicle.boat.AbstractBoat
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import java.util.*

class ZombieGoat(type: EntityType<out ZombieGoat>, level: Level) : PazZombie(type, level) {

    companion object {
    }

    init {
        xpReward = 0
    }

    override fun getAmbientSound(): SoundEvent {
        return SoundEvents.GOAT_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.GOAT_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return SoundEvents.GOAT_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.GOAT_STEP
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

    override fun updateControlFlags() {
        val noController = this.controllingPassenger !is Mob || this.controllingPassenger?.`is`(PazTags.EntityTypes.ZOMBIE_RAIDERS) == true
        val notInBoat = this.vehicle !is AbstractBoat
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, noController)
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, noController && notInBoat)
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, noController)
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, noController)
    }

    override fun handleAttributes(difficultyModifier: Float, spawnReason: EntitySpawnReason) {}

    override fun canEquipBalloons(): Boolean = false
    override fun canEquipDuckyInWater(): Boolean = false
    override fun canPickUpLoot(): Boolean = false
    override fun canBreakDoors(): Boolean = false
    override fun canHoldItem(itemStack: ItemStack): Boolean = false

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        this.setIgnoreFlag(true)
        return data
    }
}