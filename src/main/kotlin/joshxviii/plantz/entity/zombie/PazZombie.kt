package joshxviii.plantz.entity.zombie

import joshxviii.plantz.*
import joshxviii.plantz.PazDataSerializers.DATA_ZOMBIE_STATE
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.ai.control.PazFlyingMoveControl
import joshxviii.plantz.ai.control.PazMoveControl
import joshxviii.plantz.ai.goal.FlyingPathfindingGoal
import joshxviii.plantz.ai.pathfinding.PazFlyingPathNavigation
import joshxviii.plantz.ai.pathfinding.PazGroundPathNavigation
import joshxviii.plantz.entity.Balloon
import joshxviii.plantz.entity.ZombieRaider
import joshxviii.plantz.entity.setCountHealth
import joshxviii.plantz.item.BalloonItem
import joshxviii.plantz.raid.ZombieRaid.Companion.RAID_DISTANCE
import joshxviii.plantz.raid.getZombieRaids
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.FlyingMoveControl
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal
import net.minecraft.world.entity.ai.goal.SpearUseGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation
import net.minecraft.world.entity.animal.golem.IronGolem
import net.minecraft.world.entity.animal.turtle.Turtle
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin
import net.minecraft.world.entity.npc.villager.AbstractVillager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.collections.getValue
import kotlin.jvm.optionals.getOrElse
import kotlin.math.max

abstract class PazZombie(type: EntityType<out PazZombie>, level: Level) : Zombie(type, level) {

    val emergeAnimation : AnimationState = AnimationState()
    val retreatAnimation : AnimationState = AnimationState()
    val floatAnimation : AnimationState = AnimationState()
    var moveDir = Vec3.ZERO
    var moveDirO = Vec3.ZERO

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
                val rainBonus = if (isRaining) 2.75f else 1.25f
                val spawnChance = if (biome.`is`(PazTags.Biomes.WATER_SPAWNS)) 0.085f else 0.015f
                return EntitySpawnReason.isSpawner(spawnReason) ||
                        (random.nextFloat() < (spawnChance * rainBonus) && pos.y > level.seaLevel - 3)
            }

            // land spawning
            return checkMobSpawnRules(type, level, spawnReason, pos, random)
        }

        fun spawnZombieGroup(leader: Zombie, minAmount: Int = 3) {
            val level = leader.level() as? ServerLevel?: return
            var amount = minAmount
            val multi = level.getCurrentDifficultyAt(leader.blockPosition()).specialMultiplier
            if(multi > 0.0) repeat(3) {
                if(leader.random.nextFloat() < 0.3 * multi) amount++
            }

            for (i in 0..amount) {
                val spawnPos = findSpawnPosition(level, leader.blockPosition()) ?: return
                val type = rollZombieType(level.random) ?: return

                val zombie = type.create(
                    level,
                    null,
                    spawnPos,
                    EntitySpawnReason.REINFORCEMENT,
                    true,
                    false
                ) ?: return
                zombie.setPersistenceRequired()

                level.addFreshEntity(zombie)
            }
        }

        fun findSpawnPosition(level: ServerLevel, pos: BlockPos): BlockPos? {
            val random = level.random
            for (i in 0..7) {
                val x = pos.x + Mth.randomBetweenInclusive(random, -3, 3)
                val z = pos.z + Mth.randomBetweenInclusive(random, -3, 3)
                val pos = BlockPos(x, pos.y, z)

                var spawnY = pos.y
                while (spawnY > level.minY && level.isEmptyBlock(pos.atY(spawnY - 1))) spawnY--
                while (spawnY < level.maxY && !level.isEmptyBlock(pos.atY(spawnY))) spawnY++

                val finalPos = BlockPos(x, spawnY, z)

                if (level.isEmptyBlock(finalPos) && level.isEmptyBlock(finalPos.above())) return finalPos
            }
            return null
        }

        fun rollZombieType(random: RandomSource): EntityType<out Zombie>? {
            var roll = random.nextInt(SPAWN_TABLE_WEIGHTS.values.sum())
            for ((type, weight) in SPAWN_TABLE_WEIGHTS) {
                if (roll < weight) return type
                roll -= weight
            }
            return null
        }

        fun isBornLeader(zombie: Zombie): Boolean {
            return Objects.requireNonNull<AttributeInstance>(zombie.getAttribute(Attributes.MAX_HEALTH)).hasModifier(Identifier.withDefaultNamespace(LEADER_MODIFIER_ID))
        }

        // for group spawning and gravestone spawning
        val SPAWN_TABLE_WEIGHTS = mapOf(
            PazEntities.BROWN_COAT          to 20,
            PazEntities.NEWSPAPER_ZOMBIE    to 7,
            PazEntities.DIGGER_ZOMBIE       to 1,
            PazEntities.DISCO_ZOMBIE        to 1,
            PazEntities.ALL_STAR            to 1,
        )

        const val ZOMBIE_SPEED = 0.23
        const val MAX_EQUIPPABLE_BALLOONS = 4
        const val LEADER_MODIFIER_ID: String = "leader_zombie_bonus"

        data class PazZombieAttributes(
            val maxHealth: Double = 20.0,
            val attackDamage: Double = 1.0,
            val attackKnockback: Double = 0.4,
            val attackRange: Double = 1.5,
            val movementSpeed: Double = ZOMBIE_SPEED,
            val flyingSpeed: Double = ZOMBIE_SPEED,
            val jumpStrength: Double = 0.42,
            val followRange: Double = 50.0,
            val armor: Double = 0.0,
            val armorToughness: Double = 0.0,
            val spawnReinforcementsChance: Double = 0.0,
            val explosionKnockbackResistance: Double = 0.0,
            val knockbackResistance: Double = 0.2,
            val stepHeight: Double = 0.6,
            val scale: Double = 1.0,
            val waterMovementEfficiency: Double = 0.1
        ) {
            fun apply(builder: AttributeSupplier.Builder): AttributeSupplier.Builder {
                return builder
                    .add(Attributes.MAX_HEALTH, maxHealth)
                    .add(Attributes.FOLLOW_RANGE, followRange)
                    .add(Attributes.ATTACK_DAMAGE, attackDamage)
                    .add(Attributes.ATTACK_KNOCKBACK, attackKnockback)
                    .add(Attributes.MOVEMENT_SPEED, movementSpeed)
                    .add(Attributes.JUMP_STRENGTH, jumpStrength)
                    .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, spawnReinforcementsChance)
                    .add(Attributes.ARMOR, armor)
                    .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, explosionKnockbackResistance)
                    .add(Attributes.KNOCKBACK_RESISTANCE, knockbackResistance)
                    .add(Attributes.STEP_HEIGHT, stepHeight)
                    .add(Attributes.ENTITY_INTERACTION_RANGE, attackRange)
                    .add(Attributes.SCALE, scale)
                    .add(Attributes.FLYING_SPEED, flyingSpeed)
                    .add(Attributes.WATER_MOVEMENT_EFFICIENCY, waterMovementEfficiency)
            }
        }

        val ZOMBIE_STATE: EntityDataAccessor<ZombieState> = SynchedEntityData.defineId<ZombieState>(PazZombie::class.java, DATA_ZOMBIE_STATE)
        val FLYING: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(PazZombie::class.java, EntityDataSerializers.BOOLEAN)
    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.BROWNCOAT_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.BROWNCOAT_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.BROWNCOAT_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    override fun onEquipItem(slot: EquipmentSlot, oldStack: ItemStack, stack: ItemStack) {
        if (stack.`is`(PazItems.DUCKY_TUBE) && slot == EquipmentSlot.LEGS) {
            this.getNavigation().setCanFloat(true)
            this.getNavigation().recomputePath()
        };
        else if (oldStack.`is`(PazItems.DUCKY_TUBE) && slot == EquipmentSlot.LEGS) {
            this.getNavigation().setCanFloat(false)
            this.getNavigation().recomputePath()
        };

        super.onEquipItem(slot, oldStack, stack)
    }

    override fun pickUpItem(level: ServerLevel, entity: ItemEntity) {
        super.pickUpItem(level, entity)
    }

    var state: ZombieState
        get() = this.entityData.get(ZOMBIE_STATE)
        set(value) = this.entityData.set(ZOMBIE_STATE, value)

    var isFlying: Boolean
        get() = this.entityData.get(FLYING)
        set(value) {
            this.entityData.set(FLYING, value)
            updateNavigation()
        }

    protected val groundNavigation: PathNavigation = navigation
    protected val climbingNavigation: WallClimberNavigation by lazy {
        WallClimberNavigation(this, level())
    }
    protected val flyingNavigation: FlyingPathNavigation by lazy {
        val nav = PazFlyingPathNavigation(this, level())
        nav.setCanOpenDoors(false)
        nav.setCanFloat(true)
        nav
    }

    protected val groundMoveControl: MoveControl by lazy { PazMoveControl(this) }
    protected val flyingMoveControl: FlyingMoveControl by lazy { PazFlyingMoveControl(this, 20, true) }
    protected val noMoveControl = object : MoveControl(this) { override fun getSpeedModifier(): Double = 0.0 }
    protected val noLookControl = object : LookControl(this) {}

    fun updateNavigation() {
        if (isFlying) {
            if (this.navigation !== flyingNavigation) {
                this.navigation.stop()
                this.navigation = flyingNavigation
            }
            this.moveControl = flyingMoveControl
            this.isNoGravity = true
        } else {
            if (this.navigation !== if (canClimbWalls()) climbingNavigation else groundNavigation) {
                this.navigation.stop()
                this.navigation = if (canClimbWalls()) climbingNavigation else groundNavigation
            }
            this.moveControl = if (isImmobile) noMoveControl else groundMoveControl
            if (this !is ZombieRobot && this !is PirateCaptainGhost) {
                this.isNoGravity = false
            }
        }
    }

    override fun onSyncedDataUpdated(accessor: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(accessor)
        if (accessor == FLYING) updateNavigation()
    }

    override fun createNavigation(level: Level): PathNavigation {
        return PazGroundPathNavigation(this, level)
    }

    override fun getMoveControl(): MoveControl {
        if (isImmobile) return noMoveControl
        if (isFlying) return flyingMoveControl
        return super.getMoveControl()
    }

    override fun getLookControl(): LookControl {
        if (isImmobile) return noLookControl
        return super.getLookControl()
    }

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(7, FlyingPathfindingGoal(this, hoverGroundHeight = 1.5))
    }

    fun addBehaviourGoalsNoMelee() {
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
    var retreatTime = -1
    var emergeTime = -1
    override fun tick() {
        super.tick()
        moveDirO = moveDir
        val delta = deltaMovement

        val serverLevel = level() as? ServerLevel

        if (canEquipBalloons()) {
            isFlying = getHeldBalloons().isNotEmpty()
        }

        if (isEyeInFluid(FluidTags.WATER)) {
            waterTime++
            if (waterTime>=250 && canEquipDuckyInWater()) {
                if(!getItemBySlot(EquipmentSlot.LEGS).isEmpty) {
                    val currentLegs = getItemBySlot(EquipmentSlot.LEGS)
                    val dropChance = dropChances.byEquipment(EquipmentSlot.LEGS)
                    if (!currentLegs.isEmpty && max(this.random.nextFloat() - 0.1f, 0.0f).toDouble() < dropChance) {
                        if (serverLevel!=null) spawnAtLocation(serverLevel, currentLegs)
                    }
                }
                setItemSlot(EquipmentSlot.LEGS, PazItems.DUCKY_TUBE.defaultInstance)
                setDropChance(EquipmentSlot.LEGS, 0.0f)
            }
        } else waterTime = -1

        if (state != ZombieState.RETREATING) retreatTime = -1
        when (state) {
            ZombieState.IDLE -> {
                emergeAnimation.stop()
                retreatAnimation.stop()
                floatAnimation.stop()
                if (isFlying) state = ZombieState.FLYING
            }
            ZombieState.FLYING -> {
                floatAnimation.startIfStopped(tickCount)
                if (!isFlying) state = ZombieState.IDLE
            }
            ZombieState.EMERGING -> {
                emergeAnimation.startIfStopped(tickCount)
                retreatAnimation.stop()
                floatAnimation.stop()
                if (emergeTime++ > emergingTime()) {
                    state = ZombieState.IDLE
                    emergeTime = -1
                }

                if (emergeTime < 15) {
                    if(emergeTime%8==0) playSound(SoundEvents.ROOTED_DIRT_HIT, 1.0f, 0.2f)
                    serverLevel?.sendParticles(
                        BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPosition().below())),
                        x, y + 0.05, z, 8, 0.25, 0.0, 0.25, 0.4
                    )
                }
            }
            ZombieState.RETREATING -> {
                retreatAnimation.startIfStopped(tickCount)
                emergeAnimation.stop()
                floatAnimation.stop()
                if (retreatTime++ > 28) {
                    serverLevel?.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 10, 0.5, 0.5, 0.5, 0.02)
                    discard()
                }

                if(retreatTime%8==0) playSound(SoundEvents.ROOTED_DIRT_HIT, 1.0f, 0.2f)
                serverLevel?.sendParticles(
                    BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPosition().below())),
                    x, y + 0.05, z, 4, 0.25, 0.0, 0.25, 0.4
                )
            }
        }
        moveDir = moveDir.add(delta)
    }

    fun spawnBalloons(count: Int = 2, color: DyeColor = DyeColor.RED) {
        spawnBalloons(count, listOf(color))
    }

    fun spawnBalloons(count: Int, color: List<DyeColor>) {
        val level = level() as? ServerLevel ?: return
        for (i in 0 until count) {
            val balloon = PazEntities.BALLOON.create(level, EntitySpawnReason.TRIGGERED) ?: return
            balloon.dyeColor = color.random()
            val randomX = (random.nextDouble() - 0.5) * 2 + x
            val randomZ = (random.nextDouble() - 0.5) * 2 + z
            balloon.snapTo(randomX, eyeY + 1.0, randomZ)
            level.addFreshEntity(balloon)
            balloon.setLeashedTo(this, true)
        }
    }

    override fun isImmobile(): Boolean = state==ZombieState.EMERGING || state==ZombieState.RETREATING || super.isImmobile()
    override fun isWithinMeleeAttackRange(target: LivingEntity): Boolean = !isImmobile && super.isWithinMeleeAttackRange(target)

    override fun travel(travelVector: Vec3) {
        if (isFlying && this.isEffectiveAi) {
            val flyingSpeed = attributes.getValue(Attributes.FLYING_SPEED).toFloat() * this.speed * 0.25f
            if (this.isInWater) {
                this.moveRelative(flyingSpeed * 0.8f, travelVector)
                this.move(MoverType.SELF, this.deltaMovement)
                this.deltaMovement = this.deltaMovement.scale(0.8)
            } else if (this.isInLava) {
                this.moveRelative(flyingSpeed * 0.5f, travelVector)
                this.move(MoverType.SELF, this.deltaMovement)
                this.deltaMovement = this.deltaMovement.scale(0.5)
            } else {
                val input = if (travelVector.lengthSqr() > 1.0E-5) travelVector.normalize() else travelVector
                this.moveRelative(flyingSpeed, input)
                this.move(MoverType.SELF, this.deltaMovement)
                this.deltaMovement = this.deltaMovement.scale(0.91)
            }
            this.calculateEntityAnimation(true)
            return
        }
        super.travel(travelVector)
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(ZOMBIE_STATE, ZombieState.IDLE)
        entityData.define(FLYING, false)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putInt("plantz:State", state.ordinal)
        output.putBoolean("plantz:IsFlying", entityData.get(FLYING))
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        isFlying = input.getBooleanOr("plantz:IsFlying", false)
        state = ZombieState.entries[input.getInt("plantz:State").getOrElse { 0 }]
    }

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        if (!ignoreZombieDamage()) return super.hurtServer(level, source, damage)
        return if ((source.`is`(PazTags.DamageTypes.IGNORED_BY_ZOMBIES) || source.isZombieFireworkExplosion()) && !(source.directEntity.isHypnotized())) false else super.hurtServer(level, source, damage)
    }
    override fun hurtClient(source: DamageSource): Boolean {
        if (!ignoreZombieDamage()) return super.hurtClient(source)
        return if ((source.`is`(PazTags.DamageTypes.IGNORED_BY_ZOMBIES) || source.isZombieFireworkExplosion()) && !source.directEntity.isHypnotized()) false else super.hurtClient(source)
    }
    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        if (!ignoreZombieDamage()) return super.actuallyHurt(level, source, damage)
        if ((source.`is`(PazTags.DamageTypes.IGNORED_BY_ZOMBIES) || source.isZombieFireworkExplosion()) && !source.directEntity.isHypnotized()) {
            return
        }
        super.actuallyHurt(level, source, damage)
    }

    protected open fun ignoreZombieDamage(): Boolean = true

    override fun wantsToPickUp(level: ServerLevel, itemStack: ItemStack): Boolean {
        if (itemStack.`is`(PazBlocks.PLANTZ_FLAG.asItem())) return false
        return super.wantsToPickUp(level, itemStack)
    }

    open fun emergingTime(): Int = 40
    open fun canEquipDuckyInWater() = true
    open fun canEquipBalloons() = true
    open fun canClimbWalls(): Boolean = false

    override fun maxUpStep(): Float = if (isInWater) 0.5f else super.maxUpStep()
    override fun isSunSensitive(): Boolean = false
    override fun convertsInWater(): Boolean = false
    override fun canSpawnInLiquids(): Boolean = canEquipDuckyInWater()
    override fun checkSpawnObstruction(level: LevelReader): Boolean {
        return if (canSpawnInLiquids()) level.isUnobstructed(this)
        else super.checkSpawnObstruction(level)
    }
    override fun isBaby(): Boolean = false
    override fun onClimbable(): Boolean { return if (canClimbWalls() && !level().isClientSide) this.horizontalCollision else super.onClimbable() }
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {}
    fun isBabyZombie() = super.isBaby()
    fun randomEquip(random: RandomSource, difficulty: DifficultyInstance) {
        super.populateDefaultEquipmentSlots(random, difficulty)
    }
    fun isRaider() = (this as? ZombieRaider)?.`plantz$getIsFromRaid`()?: false

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val newData = PazZombieGroupData(
            isRaider = (groupData as? PazZombieGroupData)?.isRaider?: false,
            isCountedTowardRaidHealth = (groupData as? PazZombieGroupData)?.isCountedTowardRaidHealth?: false,
            isBaby = isBaby && (groupData as? ZombieGroupData)?.isBaby?: false
        )
        val level = level() as ServerLevel
        if (newData.isRaider) {
            val raid = level.getZombieRaids().getNearbyRaid(blockPosition(), RAID_DISTANCE)
            this.setCountHealth(newData.isCountedTowardRaidHealth)
            raid?.joinRaid(level, this)
        }

        val data = super.finalizeSpawn(level, difficulty, spawnReason, newData)

        if (spawnReason == EntitySpawnReason.REINFORCEMENT || spawnReason == EntitySpawnReason.EVENT) state = ZombieState.EMERGING

        if (canEquipDuckyInWater() && level.getBlockState(blockPosition()).fluidState.type == Fluids.WATER) {
            setItemSlot(EquipmentSlot.LEGS, PazItems.DUCKY_TUBE.defaultInstance)
            if (spawnReason != EntitySpawnReason.NATURAL) setDropChance(EquipmentSlot.LEGS, 0.0f)
            else setDropChance(EquipmentSlot.LEGS, 0.15f)
        }
        
        return data
    }
}

class PazZombieGroupData(
    val isRaider: Boolean = false,
    val isCountedTowardRaidHealth: Boolean = true,
    isBaby: Boolean = false
) : Zombie.ZombieGroupData(
    isBaby,
    false
)