package joshxviii.plantz.entity

import PazDataSerializers.DATA_COOLDOWN
import PazDataSerializers.DATA_PLANT_STATE
import joshxviii.plantz.PazAttributes
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.ItemTags
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.BodyRotationControl
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*

/**
 * Base class for all the other plant entities.
 * Provides basic behavior for the plants.
 */
abstract class Plant(type: EntityType<out Plant>, level: Level) : TamableAnimal(type, level) {
    companion object {

        private const val NUTRIENT_SUPPLY_MAX = 140  // ticks before suffocating when on invalid ground

        val PLANT_STATE: EntityDataAccessor<PlantState> = SynchedEntityData.defineId<PlantState>(Plant::class.java, DATA_PLANT_STATE)
        val COOLDOWN: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Plant::class.java, DATA_COOLDOWN)

        data class PlantAttributes(
            val maxHealth: Double = 20.0,
            val attackDamage: Double = 0.5,
            val attackKnockback: Double = 0.07,
            val movementSpeed: Double = 0.0,
            val followRange: Double = 16.0,
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

    private var nutrientSupply = NUTRIENT_SUPPLY_MAX

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

    private var idleAnimationStartTick: Int = 0
    val initAnimationState = AnimationState()
    val idleAnimationState = AnimationState()
    val actionAnimationState = AnimationState()
    val coolDownAnimationState = AnimationState()

    init {
        cooldown = -1
        this.lookControl = object : LookControl(this) { override fun clampHeadRotationToBody() {} }
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
        entityData.define(COOLDOWN, 0)
    }

    override fun getAmbientSound(): SoundEvent? {
        return super.getAmbientSound()// TODO make custom sounds
    }

    override fun getHurtSound(source: DamageSource): SoundEvent? {
        return SoundEvents.GENERIC_HURT// TODO make custom sounds
    }

    override fun getDeathSound(): SoundEvent? {
        return SoundEvents.GENERIC_DEATH// TODO make custom sounds
    }

    open fun getActionSound(): SoundEvent? {
        return null// TODO make custom sounds
    }

    override fun registerGoals() {
        this.goalSelector.addGoal(3, RandomLookAroundGoal(this))
        this.goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        attackGoals()
    }
    open fun attackGoals() {
        this.targetSelector.addGoal(1, OwnerHurtByTargetGoal(this))
        this.targetSelector.addGoal(2, OwnerHurtTargetGoal(this))
        this.targetSelector.addGoal(3, HurtByTargetGoal(this).setAlertOthers())
    }

    open fun stateUpdated(state: PlantState) {}

    override fun getBreedOffspring(
        level: ServerLevel,
        partner: AgeableMob
    ): AgeableMob? { return this }

    override fun canRide(vehicle: Entity): Boolean = false
    override fun isFood(itemStack: ItemStack): Boolean = false
    override fun getLeashOffset(): Vec3 = Vec3.ZERO
    override fun getPickResult(): ItemStack = SeedPacketItem.stackFor(this.type)

    override fun setPos(x: Double, y: Double, z: Double) {
        if (this.isPassenger) super.setPos(x, y, z)
        else super.setPos(Mth.floor(x) + 0.5, y, Mth.floor(z) + 0.5)
    }

    override fun tick() {
        super.tick()

        if (!onValidGround() || isOverlappingWithOther(this.blockPosition())) {
            if (--this.nutrientSupply <= 0) {
                if (this.tickCount % 20 == 0) {
                    val level = this.level()
                    if (level is ServerLevel) this.hurtServer(level, this.damageSources().dryOut(), 2.0f)
                }
            }
            //panic particles when low on nutrients
            if (this.nutrientSupply < 100 && this.random.nextInt(10) == 0) addParticlesAroundSelf()
        }
        else this.nutrientSupply = NUTRIENT_SUPPLY_MAX

        --cooldown
        if (this.level().isClientSide && !this.isNoAi) { updateAnimationState() }

        val target = this.target
        if (target != null) this.getLookControl().setLookAt(target, 180.0F, 180.0F);
    }

    /**
     * State machine for plant animations
     */
    private fun updateAnimationState() {
        when (state) {
            PlantState.IDLE -> {
                this.idleAnimationState.startIfStopped(this.tickCount - idleAnimationStartTick)
                this.initAnimationState.stop()
                this.actionAnimationState.stop()
                this.coolDownAnimationState.stop()
                if (cooldown > 0) {
                    state = PlantState.ACTION
                }
            }
            PlantState.ACTION -> {
                this.actionAnimationState.startIfStopped(this.tickCount)
                this.coolDownAnimationState.stop()
                state = PlantState.COOLDOWN
            }
            PlantState.COOLDOWN -> {
                this.coolDownAnimationState.startIfStopped(this.tickCount)
                if (cooldown <= 0) {
                    state = PlantState.IDLE
                }
            }
            PlantState.GROW -> {
                this.initAnimationState.startIfStopped(0)
                if (this.tickCount >= 19) {
                    idleAnimationStartTick = 0
                    state = PlantState.IDLE
                }
            }
        }
    }

    open fun canSurviveOn(blockPos: BlockPos) : Boolean {
        val blockBelow = this.level().getBlockState(blockPos)
        return blockBelow.`is`(PazBlocks.TAG_PLANTABLE)
    }

    // if on invalid ground plant should start to suffocate
    private fun onValidGround() : Boolean {
        val feetY = this.y - 0.001
        val blockBelowPos = BlockPos.containing(this.x, feetY, this.z)

        return canSurviveOn(blockBelowPos) || this.vehicle?.`is`(PazEntities.PLANT_POT_MINECART) == true
    }

    // whether another plant is overlapping with this one
    private fun isOverlappingWithOther(pos: BlockPos): Boolean {
        val otherPlantsAtPos = this.level().getEntitiesOfClass(Plant::class.java, AABB(pos)) { it != this }
        otherPlantsAtPos.forEach { this.boundingBox.intersects(it.boundingBox) }
        return false
    }


    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)

        // sun iteration
        if (itemStack.`is`(PazItems.SUN) ) {// heal
            if (this.isTame && this.health < this.maxHealth) {
                itemStack.consume(1, player)
                this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER)
                this.heal( this.maxHealth / 10 )
                return InteractionResult.SUCCESS
            }
            else if (!this.isTame) {// try to tame
                itemStack.consume(1, player)
                if (this.random.nextInt(4) == 0) {
                    this.tame(player)
                    this.target = null
                    this.level().broadcastEntityEvent(this, 7.toByte())
                } else {
                    this.level().broadcastEntityEvent(this, 6.toByte())
                }
                return InteractionResult.SUCCESS
            }
        }

        // shovel interaction
        if (itemStack.`is`(ItemTags.SHOVELS)) {

            if (!this.isTame || player != this.owner) {
                player.displayClientMessage(
                    Component.translatable("message.plantz.not_yours", this.name).withStyle(ChatFormatting.RED),
                    true
                )
                return InteractionResult.FAIL
            }

            // apply tool damage base on how damaged the plant was
            val shovelCost = ((1.0 - (this.health / this.maxHealth)) * 128).toInt()
            itemStack.hurtAndBreak(shovelCost, player, hand.asEquipmentSlot())
            if (!player.isCreative) run {// Spawn a seed packet item containing this plant's data
                val stack = SeedPacketItem.stackFor(this.type)
                val itemEntity = ItemEntity(this.level(), this.x, this.y + 0.5, this.z, stack)
                this.level().addFreshEntity(itemEntity)
            }
            this.playSound(SoundEvents.BIG_DRIPLEAF_BREAK)
            this.discard()

            return InteractionResult.SUCCESS
        }
        
        return super.mobInteract(player, hand)
    }

    fun addParticlesAroundSelf(particle: ParticleOptions = ParticleTypes.SPLASH, amount: Int = 4) {
        repeat(amount) {
            val xa = this.random.nextGaussian() * 0.02
            val ya = this.random.nextGaussian() * 0.02
            val za = this.random.nextGaussian() * 0.02
            this.level()
                .addParticle(particle, this.getRandomX(0.6), this.position().y + this.eyeHeight, this.getRandomZ(0.6), xa, ya, za)
        }
    }
}