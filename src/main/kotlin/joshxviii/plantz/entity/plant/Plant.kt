package joshxviii.plantz.entity.plant

import joshxviii.plantz.*
import joshxviii.plantz.PazDataSerializers.DATA_COOLDOWN
import joshxviii.plantz.PazDataSerializers.DATA_PLANT_STATE
import joshxviii.plantz.PazDataSerializers.DATA_RECEIVED_SUN
import joshxviii.plantz.PazDataSerializers.DATA_SEED_GROW_COOLDOWN
import joshxviii.plantz.PazDataSerializers.DATA_SLEEPING
import joshxviii.plantz.PazDataSerializers.DATA_SWELL_DIR
import joshxviii.plantz.PazTags.BlockTags.PLANTABLE
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.entity.Sun
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.ItemTags
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.BodyRotationControl
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.jvm.optionals.getOrElse

/**
 * Base class for all the other plant entities.
 * Provides basic behavior for the plants.
 */
abstract class Plant(type: EntityType<out Plant>, level: Level) : TamableAnimal(type, level) {
    companion object {

        fun checkPlantSpawnRules(
            type: EntityType<out Plant>,
            level: LevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val below = pos.below()
            return EntitySpawnReason.isSpawner(spawnReason) || level.getBlockState(below).isValidSpawn(level, below, type)
        }

        private const val NUTRIENT_SUPPLY_MAX = 140  // ticks before suffocating when on invalid ground

        val PLANT_STATE: EntityDataAccessor<PlantState> = SynchedEntityData.defineId<PlantState>(Plant::class.java, DATA_PLANT_STATE)
        val SWELL_DIR: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Plant::class.java, DATA_SWELL_DIR)
        val COOLDOWN: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Plant::class.java, DATA_COOLDOWN)
        val RECEIVED_SUN: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Plant::class.java, DATA_RECEIVED_SUN)
        val SEED_GROW_COOLDOWN: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Plant::class.java, DATA_SEED_GROW_COOLDOWN)
        val SLEEPING: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(Plant::class.java, DATA_SLEEPING)

        data class PlantAttributes(
            val maxHealth: Double = 10.0,
            val attackDamage: Double = 2.0,
            val attackKnockback: Double = 0.07,
            val movementSpeed: Double = 0.0,
            val followRange: Double = 14.0,
            val armor: Double = 0.0,
            val sunCost: Int = 0
        ) {
            fun apply(builder: AttributeSupplier.Builder): AttributeSupplier.Builder {
                return builder
                    .add(Attributes.MAX_HEALTH, maxHealth)
                    .add(Attributes.FOLLOW_RANGE, followRange)
                    .add(Attributes.ATTACK_DAMAGE, attackDamage)
                    .add(Attributes.ATTACK_KNOCKBACK, attackKnockback)
                    .add(Attributes.MOVEMENT_SPEED, movementSpeed)
                    .add(Attributes.ARMOR, armor)
                    .add(PazAttributes.SUN_COST, sunCost.toDouble())
            }
        }
    }

    open fun getMaxSwell() : Int = 30
    var oldSwell = 0; var swell = 0
    fun getSwelling(a: Float): Float = Mth.lerp(a, oldSwell.toFloat(), swell.toFloat()) / (getMaxSwell() - 2).toFloat()

    var swellDir: Int
        get() = this.entityData.get(SWELL_DIR)
        set(value) {
            this.entityData.set(SWELL_DIR, value)
        }

    private var nutrientSupply = NUTRIENT_SUPPLY_MAX

    val isGrowingSeeds: Boolean
        get() = testGrowConditions() != PlantGrowNeeds.SOIL

    var isAsleep: Boolean
        get() = this.entityData.get(SLEEPING)
        set(value) {
            this.entityData.set(SLEEPING, value)
        }

    val damagedPercent: Float
        get() { return 1.0f - (this.health / this.maxHealth); }

    var state: PlantState
        get() = this.entityData.get(PLANT_STATE)
        set(value) {
            stateUpdated(value)
            this.entityData.set(PLANT_STATE, value)
        }

    var cooldown: Int
        get() = this.entityData.get(COOLDOWN)
        set(value) = this.entityData.set(COOLDOWN, value.coerceAtLeast(-1))

    var receivedSun: Int
        get() = this.entityData.get(RECEIVED_SUN)
        set(value) = this.entityData.set(RECEIVED_SUN, value.coerceAtLeast(0))

    var seedGrowCooldown: Int
        get() = this.entityData.get(SEED_GROW_COOLDOWN)
        set(value) = this.entityData.set(SEED_GROW_COOLDOWN, value.coerceAtLeast(0))

    override fun die(source: DamageSource) {
        super.die(source)
        if (source.entity is Player) {
            val sunCost = PazEntities.getSunCostFromType(this.type)
            val level = level() as? ServerLevel
            if (level!=null) Sun.award(level, position(), (sunCost/2) - random.nextInt(0,1))
        }
    }

    private var idleAnimationStartTick: Int = 0
    val initAnimationState = AnimationState()
    val idleAnimationState = AnimationState()
    val actionAnimationState = AnimationState()
    val coolDownAnimationState = AnimationState()
    val sleepAnimationState = AnimationState()
    val specialAnimation = AnimationState()

    init {
        cooldown = -1
        this.lookControl = object : LookControl(this) {
            override fun clampHeadRotationToBody() {}
            override fun tick() { if (!isAsleep) super.tick() }
        }
        idleAnimationStartTick = this.random.nextInt(200, 240)
    }

    // disables body control
    override fun createBodyControl(): BodyRotationControl = object : BodyRotationControl(this) { override fun clientTick() {} }

    // only apply up/down movement
    override fun getDeltaMovement(): Vec3 = Vec3(0.0, super.deltaMovement.y, 0.0)
    override fun setDeltaMovement(deltaMovement: Vec3) {
        if (!onGround()) return super.setDeltaMovement(deltaMovement)
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(PLANT_STATE, PlantState.IDLE)
        entityData.define(SWELL_DIR, 0)
        entityData.define(COOLDOWN, 0)
        entityData.define(RECEIVED_SUN, 0)
        entityData.define(SEED_GROW_COOLDOWN, timeRequiredForSeeds())
        entityData.define(SLEEPING, false)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putInt("SeedGrowTime", seedGrowCooldown)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        seedGrowCooldown = input.getInt("SeedGrowTime").getOrElse { timeRequiredForSeeds() }
    }

    fun calculateSwell() {
        oldSwell = swell
        swell = (swell + swellDir).coerceIn(0, getMaxSwell())
    }

    override fun getAmbientSound(): SoundEvent? = super.getAmbientSound()// TODO make custom sounds
    override fun getHurtSound(source: DamageSource): SoundEvent? {
        if (source.entity is Zombie) return PazSounds.ZOMBIE_EATS
        return SoundEvents.ROOTED_DIRT_HIT// TODO make custom sounds
    }
    override fun getDeathSound(): SoundEvent? = SoundEvents.ROOTED_DIRT_BREAK// TODO make custom sounds
    open fun getActionSound(): SoundEvent? = null// TODO make custom sounds

    override fun registerGoals() {
        this.goalSelector.addGoal(3, RandomLookAroundGoal(this))
        this.goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        attackGoals()
    }
    open fun attackGoals() {
        this.targetSelector.addGoal(1, HurtByTargetGoal(this, Plant::class.java).setAlertOthers())
        this.targetSelector.addGoal(1, OwnerHurtByTargetGoal(this))
        this.targetSelector.addGoal(2, OwnerHurtTargetGoal(this))
    }
    override fun canAttack(target: LivingEntity): Boolean = super.canAttack(target) && !target.hasSameOwner(this)

    open fun stateUpdated(state: PlantState) {}

    override fun getBreedOffspring(
        level: ServerLevel,
        partner: AgeableMob
    ): AgeableMob? { return this }

    override fun canRide(vehicle: Entity): Boolean = false
    override fun isFood(itemStack: ItemStack): Boolean = false
    override fun getLeashOffset(): Vec3 = Vec3.ZERO
    override fun getPickResult(): ItemStack = SeedPacketItem.stackFor(this.type)
    override fun wantsToAttack(target: LivingEntity, owner: LivingEntity): Boolean {
        return (target !is Plant || target.owner != owner)
    }

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        return if (source.`is`(PazDamageTypes.PLANT_AOE)) false
        else super.hurtServer(level, source, damage)
    }

    override fun hurtClient(source: DamageSource): Boolean {
        return if (source.`is`(PazDamageTypes.PLANT_AOE)) false
        else super.hurtClient(source)
    }

    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        if (source.`is`(PazDamageTypes.PLANT_AOE)) return
        val plantPotProtection: Boolean = getBlockBelow().`is`(PazTags.BlockTags.PLANT_POT_PROTECTION) && source.entity is Enemy
        super.actuallyHurt(
            level,
            if (source.entity is Zombie && source.`is`(DamageTypes.MOB_ATTACK)) DamageSource(
                level.registryAccess().get(PazDamageTypes.ZOMBIE_EAT).get(),
                source.directEntity,
                source.entity
            ) else source,
            if (plantPotProtection) damage/2 else damage
        )
    }

    override fun setPos(x: Double, y: Double, z: Double) {
        if (this.isPassenger) super.setPos(x, y, z)
        else super.setPos(Mth.floor(x) + 0.5, y, Mth.floor(z) + 0.5)
    }

    override fun tick() {
        super.tick()
        val level = this.level()

        if (level is ServerLevel) {
            if (!onValidGround() || isOverlappingWithOther(blockPosition())) {
                if (--nutrientSupply <= 0) {
                    if (tickCount % 20 == 0) hurtServer(level, damageSources().dryOut(), 2.0f)
                }
                //panic particles when low on nutrients
                if (nutrientSupply < 100 && random.nextInt(10) == 0) addParticlesAroundSelf(level)
            } else nutrientSupply = NUTRIENT_SUPPLY_MAX
        }

        calculateSwell()

        --cooldown
        if (!this.isNoAi) { updateAnimationState() }

        val target = this.target
        if (target != null) getLookControl().setLookAt(target, 180.0F, 180.0F);

        if (isAsleep && tickCount % 12 == 0 && random.nextFloat()>0.6 && tickCount > 18 && isAlive) {
            val direction = calculateViewVector(xRot, yHeadRot).scale(boundingBox.xsize)
            level().addParticle(
                PazServerParticles.SLEEP,
                direction.x + getRandomX(0.2),
                direction.y.toFloat() + y + eyeHeight.toDouble() - 0.1,
                direction.z + getRandomZ(0.2),
                0.0, 0.0, 0.0,
            )
        }

        val needs = testGrowConditions()
        if (needs == PlantGrowNeeds.SUN) {
            if (tickCount%25==0) level().addParticle(PazServerParticles.NEEDS_SUN, x, y+eyeHeight+0.5, z, 0.0, 0.0, 0.0)
        }
    }

    /**
     * State machine for plant animations
     */
    private fun updateAnimationState() {
        when (state) {
            PlantState.IDLE -> {
                idleAnimationState.startIfStopped(tickCount - idleAnimationStartTick)
                initAnimationState.stop()
                actionAnimationState.stop()
                coolDownAnimationState.stop()
                specialAnimation.stop()
                sleepAnimationState.stop()
                if (isAsleep) state = PlantState.SLEEP
                if (cooldown > 0) {
                    state = PlantState.ACTION
                }
            }
            PlantState.ACTION -> {
                actionAnimationState.startIfStopped(tickCount)
                coolDownAnimationState.stop()
                state = PlantState.COOLDOWN
            }
            PlantState.COOLDOWN -> {
                if (cooldown <= 0) {
                    state = PlantState.IDLE
                }
            }
            PlantState.RECHARGE -> state = PlantState.IDLE
            PlantState.SLEEP -> {
                sleepAnimationState.startIfStopped(tickCount)
                idleAnimationState.stop()
                initAnimationState.stop()
                actionAnimationState.stop()
                coolDownAnimationState.stop()
                specialAnimation.stop()
                if (!isAsleep) state = PlantState.IDLE
            }
            PlantState.GROW -> {
                initAnimationState.startIfStopped(tickCount)
                if (tickCount >= 19) {
                    idleAnimationStartTick = 0
                    state = if (cooldown >= 0) PlantState.COOLDOWN else PlantState.IDLE
                }
            }
        }
    }

    /**
     * @param sunAmount the amount of sun used to heal.
     * @return the amount of unused sun.
     */
    fun sunHeal(sunAmount: Int) : Float {
        val sunHeal = sunAmount*2.0f
        val leftOver =( (health + sunHeal) - maxHealth)
        heal(sunHeal)
        return leftOver.coerceAtLeast(0.0f)
    }

    fun sunRequiredForSeeds(): Int = Mth.floor(PazEntities.getSunCostFromType(this.type)*1.5f).coerceAtLeast(1)

    fun timeRequiredForSeeds() : Int {
        val sunCost = PazEntities.getSunCostFromType(this.type)
        return 1000 + (sunCost*450) + random.nextInt(50) * if (level().hasChunkAt(blockPosition()) && getBlockBelow().`is`(PazBlocks.ZEN_PLANT_POT)) 1 else 2
    }

    open fun canSurviveOn(block: BlockState) : Boolean {
        return block.`is`(PLANTABLE)
    }

    fun testGrowConditions(): PlantGrowNeeds {
        val farmBlock = getBlockBelow()
        if (!farmBlock.`is`(PazTags.BlockTags.FARMABLE)) return PlantGrowNeeds.SOIL
        if (farmBlock.hasProperty(BlockStateProperties.MOISTURE)) {
            if (farmBlock.getValue(BlockStateProperties.MOISTURE) < 7) return PlantGrowNeeds.WATER
        }
        if (seedGrowCooldown > 0) {
            if (!isAsleep) isAsleep = true
            --seedGrowCooldown
            return PlantGrowNeeds.TIME
        }
        if (isAsleep) isAsleep = false
        return PlantGrowNeeds.SUN
    }

    // if on invalid ground plant should start to suffocate
    private fun onValidGround() : Boolean {
        return canSurviveOn(getBlockBelow()) || vehicle?.`is`(PazEntities.PLANT_POT_MINECART) == true
    }

    fun sunIsVisible() : Boolean {
        return level().isBrightOutside && level().getBrightness(LightLayer.SKY, BlockPos.containing(x, eyeY, z)) >= 7
    }

    fun getBlockBelow(): BlockState {
        val feetY = y - 0.001
        val blockBelowPos = BlockPos.containing(x, feetY, z)
        val blockBelow = level().getBlockState(blockBelowPos)
        return blockBelow
    }

    // whether another plant is overlapping with this one
    private fun isOverlappingWithOther(pos: BlockPos): Boolean {
        val otherPlantsAtPos = level().getEntitiesOfClass(Plant::class.java, AABB(pos)) { it != this }
        otherPlantsAtPos.forEach {
            if (!it.isAlive || it.isDeadOrDying) return false
            if(boundingBox.intersects(it.boundingBox)) return true
        }
        return false
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        state = PlantState.GROW

        if (spawnReason == EntitySpawnReason.NATURAL
            && (!onValidGround() || isOverlappingWithOther(blockPosition()))) this.discard()

        return groupData
    }

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)
        val level = level()

        if (level is ServerLevel) {
            // sun iteration
            if (itemStack.`is`(PazItems.SUN) ) {// heal
                if (isTame && health < maxHealth) {
                    itemStack.consume(1, player)
                    sunHeal(1)
                    addParticlesAroundSelf(level, ParticleTypes.HAPPY_VILLAGER)
                    return InteractionResult.SUCCESS_SERVER
                }
                else if (!isTame) {// try to tame
                    itemStack.consume(1, player)
                    if (random.nextFloat() < (1f - (PazEntities.getSunCostFromType(this.type) / 14f))*0.5f) {
                        tame(player)
                        this.target = null
                        level.broadcastEntityEvent(this, 7.toByte())
                    } else level.broadcastEntityEvent(this, 6.toByte())
                    return InteractionResult.SUCCESS_SERVER
                }
                else if (testGrowConditions() == PlantGrowNeeds.SUN) {// grow seeds
                    if (!isTame || player != owner) {
                        player.sendOverlayMessage(Component.translatable("message.plantz.not_yours", this.name).withStyle(ChatFormatting.RED))
                        return InteractionResult.FAIL
                    }
                    itemStack.consume(1, player)
                    playSound(SoundEvents.BUBBLE_POP, 1.0f,// TODO make custom sound
                        receivedSun.toFloat()/sunRequiredForSeeds() + 0.9f
                    )
                    if (receivedSun++ >= sunRequiredForSeeds()) {
                        receivedSun = 0
                        seedGrowCooldown = timeRequiredForSeeds()
                        val stack = SeedPacketItem.stackFor(this.type)
                        val itemEntity = ItemEntity(level, x, y + 0.5, z, stack)
                        level.addFreshEntity(itemEntity)
                        playSound(SoundEvents.ROOTED_DIRT_BREAK)
                        if (player is ServerPlayer) PazCriteria.GROW_SEEDS.trigger(player, 1)
                    }
                    return InteractionResult.SUCCESS_SERVER
                }
            }

            // shovel interaction
            if (itemStack.`is`(ItemTags.SHOVELS)) {
                if (!isTame || player != owner) {
                    player.sendOverlayMessage(
                        Component.translatable("message.plantz.not_yours", this.name).withStyle(ChatFormatting.RED)
                    )
                    return InteractionResult.FAIL
                }
                // apply tool damage base on how damaged the plant was
                itemStack.hurtAndBreak(4, player, hand.asEquipmentSlot())
                if (!player.isCreative || customName!=null) run {// Spawn a seed packet item containing this plant's data
                    val stack = SeedPacketItem.stackFor(this.type)
                    if (customName!=null) stack.set(DataComponents.CUSTOM_NAME, customName)
                    val itemEntity = ItemEntity(level, x, y + 0.5, z, stack)
                    level.addFreshEntity(itemEntity)
                }
                playSound(SoundEvents.ROOTED_DIRT_BREAK)
                level.sendParticles(BlockParticleOption(
                    ParticleTypes.BLOCK, getBlockBelow()),
                    x, y+0.05, z, 16, 0.25,0.0,0.25, 0.4)
                this.discard()

                return InteractionResult.SUCCESS_SERVER
            }
        }
        return super.mobInteract(player, hand)
    }

    fun addParticlesAroundSelf(
        level: Level = level(),
        particle: ParticleOptions = ParticleTypes.SPLASH,
        amount: Int = 8,
        horizontalSpreadScale: Double = 0.3,
        verticalSpreadScale: Double = 0.5,
        height: Float = 0.0f,
        speed: Double = 0.0,
    ) {
        if (level is ServerLevel) {
            level.sendParticles(
                particle,
                x, y + height, z,
                amount,
                horizontalSpreadScale, verticalSpreadScale, horizontalSpreadScale,
                speed
            )
        }
        else repeat(amount) {
            // Random offsets for velocity
            val xa = random.nextGaussian() * 0.02
            val ya = random.nextGaussian() * 0.02
            val za = random.nextGaussian() * 0.02

            // Position inside the bounding box
            val px = getRandomX(horizontalSpreadScale)
            val py = y + height + random.nextDouble() * bbHeight * verticalSpreadScale
            val pz = getRandomZ(horizontalSpreadScale)

            level.addParticle(
                particle,
                px, py, pz,
                xa, ya, za,
            )
        }
    }
}

enum class PlantGrowNeeds {
    SOIL,
    SUN,
    WATER,
    TIME;
}