package joshxviii.plantz.entity.zombie

import it.unimi.dsi.fastutil.ints.IntList
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazSounds
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.isActiveInRaid
import joshxviii.plantz.entity.zombie.RoboZombie.Companion.MISSILE_COOLDOWN_TIME
import net.fabricmc.fabric.api.networking.v1.PlayerLookup.level
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.core.component.DataComponents
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.jsonrpc.methods.ServerSettingsService.difficulty
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.CrossbowItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ChargedProjectiles
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import java.util.Optional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class ZombieBat(type: EntityType<out ZombieBat>, level: Level) : PazZombie(type, level) {

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