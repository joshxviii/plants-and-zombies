package joshxviii.plantz.entity

import joshxviii.plantz.PazItems
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.util.Mth
import net.minecraft.util.StringRepresentable
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.BodyRotationControl
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.animal.golem.AbstractGolem
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import java.util.*

open class Plant(type: EntityType<out Plant>, level: Level) : AbstractGolem(type, level) {
    companion object {
        @JvmStatic val DATA_POTTED_ID: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(Plant::class.java, EntityDataSerializers.BOOLEAN)

        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
        }
    }

    var isPotted: Boolean
        get() { return this.entityData.get(DATA_POTTED_ID) }
        set(value) { this.entityData.set(DATA_POTTED_ID, value); refreshDimensions() }
    val damage: Float
        get() { return 1.0f - (this.health / this.maxHealth); }

    init {
        this.playSound(SoundEvents.BIG_DRIPLEAF_PLACE)

        this.lookControl = object : LookControl(this) {
            override fun clampHeadRotationToBody() {}
        }
    }

    // disable body control
    override fun createBodyControl(): BodyRotationControl {
        return object : BodyRotationControl(this) { override fun clientTick() {} }
    }
    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(DATA_POTTED_ID, false)
    }

    // save and load custom synced data
    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putBoolean("plantz:IsPotted", this.isPotted)
    }
    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        this.isPotted = input.getBooleanOr("plantz:IsPotted", false)
    }

    override fun registerGoals() {
        this.targetSelector.addGoal(1, HurtByTargetGoal(this).setAlertOthers())
        
        this.goalSelector.addGoal(2, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        this.goalSelector.addGoal(3, RandomLookAroundGoal(this))
    }

    override fun setPos(x: Double, y: Double, z: Double) {
        // snap to the center of blocks, and on top of flower pots
        super.setPos(
            Mth.floor(x) + 0.5,
            Mth.floor(y + 0.5).toDouble(),
            Mth.floor(z) + 0.5
        )
    }

    override fun getDefaultDimensions(pose: Pose): EntityDimensions {
        val base = super.getDefaultDimensions(pose)
        val height = if (isPotted) base.height + 0.375 else base.height
        return EntityDimensions.scalable(base.width, height.toFloat())
    }

    override fun tick() {
        super.tick()
        // check if the attached block still exists
        if (!this.level().isClientSide && !this.isPassenger) {
            if (!this.canStayAt(this.blockPosition()) && !isPotted) {
                // kill if on invalid ground or not potted
                destroy()
            }
        }
        val target = this.target
        if (target != null) this.getLookControl().setLookAt(target, 180.0F, 180.0F);
    }

    private fun destroy() {
        if (isPotted) {
            val pos = this.blockPosition()
            this.level().setBlock(pos, Blocks.FLOWER_POT.defaultBlockState(), 3)
            this.level().gameEvent(this, GameEvent.BLOCK_CHANGE, pos)
        }
        this.playSound(SoundEvents.BIG_DRIPLEAF_BREAK)
        this.discard()
    }

    private fun canStayAt(pos: BlockPos): Boolean {
        val blockBelow = this.level().getBlockState(pos.below())
        return blockBelow.`is`(BlockTags.DIRT)
            || blockBelow.`is`(Blocks.FARMLAND)
            || blockBelow.`is`(Blocks.LILY_PAD)
            || blockBelow.`is`(Blocks.DECORATED_POT)
    }

    override fun getDeltaMovement(): Vec3 = Vec3.ZERO

    override fun setDeltaMovement(deltaMovement: Vec3) {/* Prevent movement */}

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)

        // sun iteration
        if (itemStack.`is`(PazItems.SUN) ) {
            if (this.health < this.maxHealth) {
                itemStack.shrink(1)
                this.playSound(SoundEvents.BUBBLE_POP, 2.0f, 0.5f)
                this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER)
                this.heal(1f)

                return InteractionResult.SUCCESS
            }
        }

        // shovel interaction
        if (itemStack.`is`(ItemTags.SHOVELS)) {

            if (this.health < this.maxHealth) {
                player.displayClientMessage(Component
                    .translatable("message.plantz.too_damaged", this.name)
                    .withStyle(ChatFormatting.RED),
                true)
                return InteractionResult.SUCCESS
            }

            itemStack.hurtAndBreak(8, player, hand.asEquipmentSlot())
            run {// Spawn a seed packet item containing this plant's data
                val stack = SeedPacketItem.stackFor(this.type)
                val itemEntity = ItemEntity(this.level(), this.x, this.y + 0.5, this.z, stack)
                this.level().addFreshEntity(itemEntity)
            }
            destroy()

            return InteractionResult.SUCCESS
        }
        
        return super.mobInteract(player, hand)
    }

    private fun addParticlesAroundSelf(particle: ParticleOptions = ParticleTypes.DRIPPING_WATER, amount: Int = 10) {
        repeat(amount) {
            val xa = this.random.nextGaussian() * 0.02
            val ya = this.random.nextGaussian() * 0.02
            val za = this.random.nextGaussian() * 0.02
            this.level()
                .addParticle(particle, this.getRandomX(1.0), this.randomY + 0.5, this.getRandomZ(1.0), xa, ya, za)
        }
    }
}