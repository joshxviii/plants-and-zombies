package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazDataSerializers.DATA_ZOMBIE_STATE
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazTags
import joshxviii.plantz.ai.ZombieState
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
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
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal
import net.minecraft.world.entity.ai.goal.SpearUseGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.golem.IronGolem
import net.minecraft.world.entity.animal.turtle.Turtle
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin
import net.minecraft.world.entity.npc.villager.AbstractVillager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.biome.Biome
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
            if (level.difficulty == Difficulty.PEACEFUL) return false

            val biome = level.getBiome(pos)
            val isRaining = level.level.isRaining
            val inWater = level.getFluidState(pos).`is`(FluidTags.WATER)

            // light / day requirements
            val canSpawn = (inWater && isRaining) || EntitySpawnReason.ignoresLightRequirements(spawnReason) || biome.`is`(PazTags.Biomes.DAY_SPAWNS) || isDarkEnoughToSpawn(level, pos, random)
            if (!canSpawn) return false

            // water spawning
            if (inWater) {
                val rainBonus = if (isRaining) 2.5f else 1f
                val spawnChance = if (biome.`is`(PazTags.Biomes.WATER_SPAWNS)) 0.075f else 0.01f
                return EntitySpawnReason.isSpawner(spawnReason) ||
                        (random.nextFloat() < (spawnChance * rainBonus) && pos.y > level.seaLevel - 3)
            }

            // land spawning
            return checkMobSpawnRules(type, level, spawnReason, pos, random)
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
    }

    fun behaviourGoalsNoMelee() {
        this.goalSelector.addGoal(2, SpearUseGoal<Zombie>(this, 1.0, 1.0, 10.0f, 2.0f))
        this.goalSelector.addGoal(6, MoveThroughVillageGoal(this, 1.0, true, 4) { this.canBreakDoors() })
        this.goalSelector.addGoal(7, WaterAvoidingRandomStrollGoal(this, 1.0))
        this.targetSelector.addGoal(1, HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin::class.java))
        this.targetSelector.addGoal(2, NearestAttackableTargetGoal(this, Player::class.java, true))
        this.targetSelector.addGoal(3, NearestAttackableTargetGoal(this, AbstractVillager::class.java, false))
        this.targetSelector.addGoal(3, NearestAttackableTargetGoal(this, IronGolem::class.java, true))
        this.targetSelector.addGoal(5, NearestAttackableTargetGoal(this, Turtle::class.java, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR))
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

    override fun maxUpStep(): Float = if (isInWater) 0.5f else super.maxUpStep()
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
            else setDropChance(EquipmentSlot.LEGS, 0.125f)
        }

        return data
    }
}