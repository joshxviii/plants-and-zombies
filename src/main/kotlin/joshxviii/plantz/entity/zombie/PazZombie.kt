package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazDataSerializers.DATA_ZOMBIE_STATE
import joshxviii.plantz.PazItems
import joshxviii.plantz.ai.ZombieState
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.FluidTags
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.material.Fluids

abstract class PazZombie(type: EntityType<out PazZombie>, level: Level) : Zombie(type, level) {

    val emergeAnimation : AnimationState = AnimationState()

    companion object {
        fun checkPazZombieSpawnRules(
            type: EntityType<out Mob>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val canSpawn = level.difficulty != Difficulty.PEACEFUL
                    && (EntitySpawnReason.ignoresLightRequirements(spawnReason) || isDarkEnoughToSpawn(level, pos, random))

            val inWater = level.getFluidState(pos).`is`(FluidTags.WATER)

            return if (inWater)
                    EntitySpawnReason.isSpawner(spawnReason) || (random.nextFloat() < 0.1f && pos.y > level.seaLevel-3)
                else
                    canSpawn && (checkMobSpawnRules(type, level, spawnReason, pos, random))
        }

        val ZOMBIE_STATE: EntityDataAccessor<ZombieState> = SynchedEntityData.defineId<ZombieState>(PazZombie::class.java, DATA_ZOMBIE_STATE)
    }

    override fun onEquipItem(slot: EquipmentSlot, oldStack: ItemStack, stack: ItemStack) {
        if (stack.`is`(PazItems.DUCKY_TUBE) && slot == EquipmentSlot.LEGS) this.getNavigation().setCanFloat(true);
        else if (oldStack.`is`(PazItems.DUCKY_TUBE) && slot == EquipmentSlot.LEGS) this.getNavigation().setCanFloat(false);
        super.onEquipItem(slot, oldStack, stack)
    }

    var state: ZombieState
        get() = this.entityData.get(ZOMBIE_STATE)
        set(value) { this.entityData.set(ZOMBIE_STATE, value) }

    private val noMoveControl = object : MoveControl(this) {
        override fun getSpeedModifier(): Double = 0.0
    }

    override fun getMoveControl(): MoveControl {
        if (state == ZombieState.EMERGING) return noMoveControl
        return super.getMoveControl()
    }

    override fun registerGoals() {
        super.registerGoals()
        //this.goalSelector.addGoal(1, FloatGoal(this))
    }

    override fun getFluidJumpThreshold(): Double {
        return super.getFluidJumpThreshold()
    }

    var waterTime = -1
    override fun tick() {
        super.tick()
        val level = level()

        if (isEyeInFluid(FluidTags.WATER)) {
            waterTime++
            if (waterTime>=250 && canEquipDuckyInWater() && getItemBySlot(EquipmentSlot.LEGS).isEmpty) {
                setItemSlot(EquipmentSlot.LEGS, PazItems.DUCKY_TUBE.defaultInstance)
                setDropChance(EquipmentSlot.LEGS, 0.0f)
            }
        } else waterTime = -1

        when (state) {
            ZombieState.EMERGING -> {
                isImmobile
                emergeAnimation.startIfStopped(tickCount)
                if (tickCount < 15) {
                    if(tickCount==1) playSound(SoundEvents.ROOTED_DIRT_HIT, 1.0f, 0.2f)
                    if (level is ServerLevel) level.sendParticles(
                        BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockPosition().below())),
                        x, y + 0.05, z, 8, 0.25, 0.0, 0.25, 0.4
                    )
                }
                if (tickCount > emergingTime()) {
                    emergeAnimation.stop()
                    state = ZombieState.IDLE
                }
            }
            else -> {}
        }
    }

    override fun isImmobile(): Boolean {
        return if (state==ZombieState.EMERGING) true else super.isImmobile()
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(ZOMBIE_STATE, ZombieState.IDLE)
    }

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        return if (source.`is`(PazDamageTypes.ZOMBIE_SMASH)) false else super.hurtServer(level, source, damage)
    }
    override fun hurtClient(source: DamageSource): Boolean {
        return if (source.`is`(PazDamageTypes.ZOMBIE_SMASH)) false else super.hurtClient(source)
    }
    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        if (source.`is`(PazDamageTypes.ZOMBIE_SMASH)) return
        super.actuallyHurt(level, source, damage)
    }

    override fun wantsToPickUp(level: ServerLevel, itemStack: ItemStack): Boolean {
        if (itemStack.`is`(PazBlocks.PLANTZ_FLAG.asItem())) return false
        return super.wantsToPickUp(level, itemStack)
    }

    open fun emergingTime(): Int = 40
    open fun canEquipDuckyInWater() = true

    override fun isSunSensitive(): Boolean = false
    override fun convertsInWater(): Boolean = false
    override fun canSpawnInLiquids(): Boolean = canEquipDuckyInWater()
    override fun checkSpawnObstruction(level: LevelReader): Boolean {
        return if (canSpawnInLiquids()) level.isUnobstructed(this)
        else super.checkSpawnObstruction(level)
    }
    override fun isBaby(): Boolean = false
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {}
    fun isBabyZombie() = super.isBaby()
    fun randomEquip(random: RandomSource, difficulty: DifficultyInstance) {
        super.populateDefaultEquipmentSlots(random, difficulty)
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)

        if (canEquipDuckyInWater() && level.getBlockState(blockPosition()).fluidState.type == Fluids.WATER) {
            setItemSlot(EquipmentSlot.LEGS, PazItems.DUCKY_TUBE.defaultInstance)
            if (spawnReason != EntitySpawnReason.NATURAL) setDropChance(EquipmentSlot.LEGS, 0.0f)
        }

        return data

    }
}