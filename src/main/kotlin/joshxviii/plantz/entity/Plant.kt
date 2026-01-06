package joshxviii.plantz.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazItems
import joshxviii.plantz.entity.projectile.PlantProjectile
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
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
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SpawnEggItem
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.math.sqrt

abstract class Plant(type: EntityType<out Plant>, level: Level) : TamableAnimal(type, level) {
    companion object {

        private const val NUTRIENT_SUPPLY_MAX = 120  // 15 seconds before suffocating when on invalid block
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
        }
    }

    open fun createProjectile(): PlantProjectile? { return null }

    fun performRangedAttack(target: LivingEntity, power: Float) {
        val speed = 1.5f
        val projectile = createProjectile()
        if (projectile==null) return
        val xd = target.x - this.x
        val yd = target.eyeY + 0.5
        val zd = target.z - this.z
        val yo = sqrt(xd * xd + zd * zd) * 0.2f
        if (this.level() is ServerLevel) {
            Projectile.spawnProjectile(projectile, this.level() as ServerLevel, ItemStack.EMPTY) {
                it.shoot(xd, yd + yo - it.y, zd, power * speed, 12.0f)
            }
        }

        // TODO make pea shooting sound
        this.playSound(SoundEvents.BUBBLE_POP, 3.0f, 0.4f / (this.getRandom().nextFloat() * 0.4f + 0.8f))
    }

    override fun registerGoals() {
        this.goalSelector.addGoal(2, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        this.goalSelector.addGoal(3, RandomLookAroundGoal(this))

        this.targetSelector.addGoal(1, HurtByTargetGoal(this).setAlertOthers())
        this.targetSelector.addGoal(1, NearestAttackableTargetGoal(this, Mob::class.java, 10, true, false) { target: LivingEntity, level: ServerLevel -> target is Enemy }
        )
    }

    private var nutrientSupply = NUTRIENT_SUPPLY_MAX
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

    override fun getBreedOffspring(
        level: ServerLevel,
        partner: AgeableMob
    ): AgeableMob? { return this }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
    }

    override fun isFood(itemStack: ItemStack): Boolean {
        return false
    }

    override fun setPos(x: Double, y: Double, z: Double) {
        super.setPos(Mth.floor(x) + 0.5, y, Mth.floor(z) + 0.5)
    }

    override fun tick() {
        super.tick()

        if (onValidGround() && canStayAt(this.blockPosition())) {
            this.nutrientSupply = NUTRIENT_SUPPLY_MAX
        } else {
            this.nutrientSupply--

            if (this.nutrientSupply <= 0) {
                if (this.tickCount % 20 == 0) {
                    val level = this.level()
                    if (level is ServerLevel) this.hurtServer(level, this.damageSources().dryOut(), 2.0f)
                }
            }

            if (this.nutrientSupply < 100 && this.random.nextInt(10) == 0) {
                addParticlesAroundSelf()
            }
        }

        val target = this.target
        if (target != null) this.getLookControl().setLookAt(target, 180.0F, 180.0F);
    }

    private fun destroy() {
        this.playSound(SoundEvents.BIG_DRIPLEAF_BREAK)
        this.discard()
    }

    private fun canStayAt(pos: BlockPos): Boolean {
        // Check if another Plant entity is already occupying this position
        val aabb = AABB(pos)
        val entitiesInBlock = this.level().getEntitiesOfClass(Plant::class.java, aabb) { it != this }
        return entitiesInBlock.isEmpty()
    }

    // if on invalid ground plant should start to suffocate
    fun onValidGround() : Boolean {
        val feetY = this.y - 0.001 // slight offset down to avoid floating point issues
        val blockBelowPos = BlockPos.containing(this.x, feetY, this.z)
        val blockBelow =  this.level().getBlockState(blockBelowPos)

        return blockBelow.`is`(PazBlocks.PLANTABLE)
    }

    override fun getDeltaMovement(): Vec3 = Vec3(0.0, super.deltaMovement.y, 0.0)

    override fun setDeltaMovement(deltaMovement: Vec3) {
        if (onGround()) return
        super.setDeltaMovement(deltaMovement)
    }

    override fun getPickResult(): ItemStack {
        return SeedPacketItem.stackFor(this.type)
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

    private fun addParticlesAroundSelf(particle: ParticleOptions = ParticleTypes.SPLASH, amount: Int = 8) {
        repeat(amount) {
            val xa = this.random.nextGaussian() * 0.02
            val ya = this.random.nextGaussian() * 0.02
            val za = this.random.nextGaussian() * 0.02
            this.level()
                .addParticle(particle, this.getRandomX(0.6), this.position().y + this.eyeHeight, this.getRandomZ(0.6), xa, ya, za)
        }
    }
}