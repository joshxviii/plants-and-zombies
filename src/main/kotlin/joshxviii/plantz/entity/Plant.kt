package joshxviii.plantz.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazEntities.DATA_PLANT_STATE
import joshxviii.plantz.PazItems
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.ItemTags
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
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
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.math.sqrt

/**
 * Base class for all the other plant entities.
 * Provides basic behavior for the plants.
 */
abstract class Plant(type: EntityType<out Plant>, level: Level) : TamableAnimal(type, level) {
    companion object {

        private const val NUTRIENT_SUPPLY_MAX = 140  // ticks before suffocating when on invalid ground

        val PLANT_STATE: EntityDataAccessor<PlantState> = SynchedEntityData.defineId<PlantState>(Plant::class.java, DATA_PLANT_STATE)

        data class PlantAttributes(
            val maxHealth: Double = 20.0,
            val attackDamage: Double = 4.0,
            val movementSpeed: Double = 0.0,
            val armor: Double = 0.0
        ) {
            fun apply(builder: AttributeSupplier.Builder): AttributeSupplier.Builder {
                return builder
                    .add(Attributes.MAX_HEALTH, maxHealth)
                    .add(Attributes.ATTACK_DAMAGE, attackDamage)
                    .add(Attributes.MOVEMENT_SPEED, movementSpeed)
                    .add(Attributes.ARMOR, armor)
            }
        }
    }

    init {
        setState(PlantState.IDLE)
        //TODO replace with ambient entity sound
        this.playSound(SoundEvents.BIG_DRIPLEAF_PLACE)
        this.lookControl = object : LookControl(this) { override fun clampHeadRotationToBody() {} }
    }
    // disables body control
    override fun createBodyControl(): BodyRotationControl = object : BodyRotationControl(this) { override fun clientTick() {} }
    open fun snapSpawnRotation(): Boolean = false

    // only apply up/down movement
    override fun getDeltaMovement(): Vec3 = Vec3(0.0, super.deltaMovement.y, 0.0)
    override fun setDeltaMovement(deltaMovement: Vec3) {
        if (onGround()) return super.setDeltaMovement(deltaMovement)
    }

    fun getState(): PlantState = this.entityData.get(PLANT_STATE)
    fun setState(state: PlantState) = this.entityData.set(PLANT_STATE, state)
    val idleAnimationState = AnimationState()
    val actionAnimationState = AnimationState()
    val coolDownAnimationState = AnimationState()

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(PLANT_STATE, PlantState.IDLE)
    }

    override fun registerGoals() {
        this.goalSelector.addGoal(2, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        this.goalSelector.addGoal(3, RandomLookAroundGoal(this))
        attackGoals()
    }
    open fun attackGoals() {
        this.targetSelector.addGoal(1, OwnerHurtByTargetGoal(this))
        this.targetSelector.addGoal(2, OwnerHurtTargetGoal(this))
        this.targetSelector.addGoal(3, HurtByTargetGoal(this).setAlertOthers())
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target: LivingEntity, level: ServerLevel -> target is Enemy })
    }

    open fun createProjectile(): Projectile? { return null }
    fun performRangedAttack(target: LivingEntity, power: Float) {
        val speed = 1.5f
        val projectile = createProjectile()
        if (projectile==null) return
        val xd = target.x - this.x
        val yd = target.eyeY
        val zd = target.z - this.z
        val yo = sqrt(xd * xd + zd * zd) * 0.2f
        if (this.level() is ServerLevel) {
            Projectile.spawnProjectile(projectile, this.level() as ServerLevel, ItemStack.EMPTY) {
                it.shoot(xd, yd + yo - it.y, zd, power * speed, 10.0f)
            }
        }
        this.playSound(SoundEvents.BUBBLE_POP, 3.0f, 0.4f / (this.getRandom().nextFloat() * 0.4f + 0.8f))
    }

    private var nutrientSupply = NUTRIENT_SUPPLY_MAX
    val damagedPercent: Float
        get() { return 1.0f - (this.health / this.maxHealth); }

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

        if (this.level().isClientSide && !this.isNoAi) updateAnimationState()

        if (!onValidGround() || isOverlappingWithOther(this.blockPosition())) {
            if (--this.nutrientSupply <= 0) {
                if (this.tickCount % 20 == 0) {
                    val level = this.level()
                    //TODO make "lackOfNutrients" damage type
                    if (level is ServerLevel) this.hurtServer(level, this.damageSources().dryOut(), 2.0f)
                }
            }
            //panic particles when low on nutrients
            if (this.nutrientSupply < 100 && this.random.nextInt(10) == 0) addParticlesAroundSelf()
        }
        else this.nutrientSupply = NUTRIENT_SUPPLY_MAX

        val target = this.target
        if (target != null) this.getLookControl().setLookAt(target, 180.0F, 180.0F);
    }

    private fun updateAnimationState() {
        when ( getState() ) {
            PlantState.IDLE -> {
                this.idleAnimationState.startIfStopped(this.tickCount)
                this.actionAnimationState.stop()
                this.coolDownAnimationState.stop()
            }
            PlantState.ACTION -> {
                this.idleAnimationState.stop()
                this.actionAnimationState.startIfStopped(this.tickCount)
                this.coolDownAnimationState.stop()
            }
            PlantState.COOLDOWN -> {
                this.idleAnimationState.stop()
                this.actionAnimationState.stop()
                this.coolDownAnimationState.startIfStopped(this.tickCount)
            }
        }
    }

    // if on invalid ground plant should start to suffocate
    fun onValidGround() : Boolean {
        val feetY = this.y - 0.001 // slight offset down to avoid floating point issues
        val blockBelowPos = BlockPos.containing(this.x, feetY, this.z)
        val blockBelow = this.level().getBlockState(blockBelowPos)

        return blockBelow.`is`(PazBlocks.PLANTABLE) || this.vehicle?.`is`(PazEntities.PLANT_POT_MINECART) == true
    }

    // whether another plant is overlapping with this one
    private fun isOverlappingWithOther(pos: BlockPos): Boolean {
        val otherPlantsAtPos = this.level().getEntitiesOfClass(Plant::class.java, AABB(pos)) { it != this }
        otherPlantsAtPos.forEach { this.boundingBox.intersects(it.boundingBox) }
        return false
    }


    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)

        //TODO debug stuff
        if (itemStack.`is`(Items.STICK) ) this.setState(PlantState.ACTION)
        if (itemStack.`is`(Items.BLAZE_ROD) ) this.setState(PlantState.IDLE)
        if (itemStack.`is`(Items.BREEZE_ROD) ) this.setState(PlantState.COOLDOWN)

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

    private fun addParticlesAroundSelf(particle: ParticleOptions = ParticleTypes.SPLASH, amount: Int = 4) {
        repeat(amount) {
            val xa = this.random.nextGaussian() * 0.02
            val ya = this.random.nextGaussian() * 0.02
            val za = this.random.nextGaussian() * 0.02
            this.level()
                .addParticle(particle, this.getRandomX(0.6), this.position().y + this.eyeHeight, this.getRandomZ(0.6), xa, ya, za)
        }
    }
}