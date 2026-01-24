package joshxviii.plantz.entity.gnome

import joshxviii.plantz.PazDataSerializers.GNOME_SOUND_VARIANT
import joshxviii.plantz.PazDataSerializers.GNOME_VARIANT
import joshxviii.plantz.PazTags
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.tags.TagKey
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.control.JumpControl
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.entity.projectile.arrow.AbstractArrow
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3

class Gnome(type: EntityType<out Gnome>, level: Level) :Monster(type, level) {

    companion object {
        fun checkGnomeSpawnRules(
            type: EntityType<out Mob>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            return (EntitySpawnReason.ignoresLightRequirements(spawnReason))
                    && checkMobSpawnRules(type, level, spawnReason, pos, random)
                    && pos.y < level.seaLevel
        }

        val DATA_VARIANT_ID: EntityDataAccessor<GnomeVariant> = SynchedEntityData.defineId(Gnome::class.java, GNOME_VARIANT)
        val DATA_SOUND_VARIANT_ID: EntityDataAccessor<GnomeSoundVariant> = SynchedEntityData.defineId(Gnome::class.java, GNOME_SOUND_VARIANT)
    }

    private var jumpTicks = 0
    private var jumpDuration = 0
    private var wasOnGround = false
    private var jumpDelayTicks = 0
    private val bowGoal = ProjectileAttackGoal(
        usingEntity = this,
        projectileFactory = { getArrow() },
        velocity = 1.4,
        actionDelay = 40)
    private val meleeGoal = MeleeAttackGoal(this, 1.2, false)

    fun getArrow(): AbstractArrow {
        val bowItem = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW))
        val projectile = this.getProjectile(bowItem)
        return ProjectileUtil.getMobArrow(this, projectile, 1.0f, bowItem)
    }

    var variant: GnomeVariant
        get() = this.entityData.get(DATA_VARIANT_ID)
        set(value) = this.entityData.set(DATA_VARIANT_ID, value)

    var soundVariant: GnomeSoundVariant
        get() = this.entityData.get(DATA_SOUND_VARIANT_ID)
        set(value) = this.entityData.set(DATA_SOUND_VARIANT_ID, value)

    val soundSet = soundVariant.getSoundSet()

    init {
        jumpControl = GnomeJumpControl(this)
        moveControl = GnomeMoveControl(this)
        setSpeedModifier(0.0)
    }

    override fun registerGoals() {
        this.goalSelector.addGoal(1, SpearUseGoal(this, 1.0, 1.0, 10.0f, 2.0f))
        this.goalSelector.addGoal(2, RandomLookAroundGoal(this))
        this.goalSelector.addGoal(2, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        this.goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 0.6))
        this.goalSelector.addGoal(4, FindAndRideAnimalGoal(this))
        //this.goalSelector.addGoal(7, FollowMobGoal(this, 1.0, 3.0f, 7.0f))
        this.targetSelector.addGoal(1, HurtByTargetGoal(this, Gnome::class.java).setAlertOthers())
        this.targetSelector.addGoal(2, NearestAttackableTargetGoal(this, LivingEntity::class.java, true) { target, level ->
            target !is Gnome
                    && target is Plant
                    || target is Zombie
                    || target is Player
        })
    }

    fun reassessWeaponGoal() {
        if (this.level() != null && !this.level().isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal)
            this.goalSelector.removeGoal(this.bowGoal)
            val usedWeapon = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW))
            if (usedWeapon.`is`(Items.BOW))
                this.goalSelector.addGoal(1, this.bowGoal)
            else
                this.goalSelector.addGoal(1, this.meleeGoal)
        }
    }

    override fun onEquipItem(slot: EquipmentSlot, oldStack: ItemStack, stack: ItemStack) {
        super.onEquipItem(slot, oldStack, stack)
        if (!this.level().isClientSide) this.reassessWeaponGoal()
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(
            DATA_VARIANT_ID,
            GnomeVariant.getDefault()
        )
        entityData.define(
            DATA_SOUND_VARIANT_ID,
            GnomeSoundVariant.getDefault()
        )
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("color_variant", GnomeVariant.CODEC, variant)
        output.store("sound_variant", GnomeSoundVariant.CODEC, soundVariant)
    }
    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        variant = input.read<GnomeVariant>("color_variant", GnomeVariant.CODEC).get()
        soundVariant = input.read<GnomeSoundVariant>("sound_variant", GnomeSoundVariant.CODEC).get()
        this.reassessWeaponGoal()
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        setCanPickUpLoot(true)
        populateDefaultEquipmentSlots(random, difficulty)

        variant = GnomeVariant.pickRandomVariant()
        soundVariant = GnomeSoundVariant.pickRandomVariant()

        this.reassessWeaponGoal()
        return groupData
    }

    override fun getAmbientSound(): SoundEvent = soundSet.ambientSound.value()

    override fun getHurtSound(source: DamageSource): SoundEvent = soundSet.hurtSound.value()

    override fun getDeathSound(): SoundEvent = soundSet.deathSound.value()

    override fun playStepSound(pos: BlockPos, blockState: BlockState) {
        playSound(soundSet.stepSound.value(), 0.15f, 1.0f)
    }

    protected fun getJumpSound(): SoundEvent {
        return soundSet.deathSound.value()
    }

    override fun getPreferredWeaponType(): TagKey<Item> = PazTags.ItemTags.GNOME_PREFERRED_WEAPONS

    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {
        var armorType = random.nextInt(2)
        if (random.nextFloat() < 0.1) armorType++

        for (slot in listOf(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            val itemStack = this.getItemBySlot(slot)
            if (itemStack.isEmpty) {
                val equip = getEquipmentForSlot(slot, armorType)
                if (equip != null) this.maybeWearArmor(slot, ItemStack(equip), random)
            }
        }

        if (random.nextFloat() < (if (this.level().difficulty == Difficulty.HARD) 0.5f else 0.33f)) {
            val rand = random.nextInt(5)
            when (rand) {
                0 -> this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(Items.COPPER_AXE))
                1 -> this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(Items.COPPER_SPEAR))
                2 -> this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(Items.BOW))
                else -> this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(Items.COPPER_PICKAXE))
            }
        }
    }

    private fun maybeWearArmor(slot: EquipmentSlot, itemStack: ItemStack, random: RandomSource) {
        if (random.nextFloat() < 0.25f) this.setItemSlot(slot, itemStack)
    }


    public override fun customServerAiStep(level: ServerLevel) {
        if (jumpDelayTicks > 0) jumpDelayTicks--

        if (onGround()) {
            if (!wasOnGround) {
                this.setJumping(false)
                this.checkLandingDelay()
            }

            val jumpControl = jumpControl as GnomeJumpControl
            if (!jumpControl.wantJump()) {
                if (moveControl.hasWanted() && jumpDelayTicks == 0) {
                    val path = navigation.getPath()
                    var pos = Vec3(
                        moveControl.getWantedX(),
                        moveControl.getWantedY(),
                        moveControl.getWantedZ()
                    )
                    if (path != null && !path.isDone) pos = path.getNextEntityPos(this)

                    facePoint(pos.x, pos.z)
                    startJumping()
                }
            } else if (!jumpControl.canJump) enableJumpControl()
        }
        this.wasOnGround = this.onGround()
    }

    private fun setLandingDelay() {
        jumpDelayTicks = if (moveControl.getSpeedModifier() < 2.2) 5 else 0
    }

    private fun checkLandingDelay() {
        this.setLandingDelay()
        this.disableJumpControl()
    }

    override fun aiStep() {
        super.aiStep()
        if (jumpTicks != jumpDuration) jumpTicks++
        else if (jumpDuration != 0) {
            jumpTicks = 0
            jumpDuration = 0
            jumping = false
        }
    }

    private fun facePoint(faceX: Double, faceZ: Double) {
        this.yRot = (Mth.atan2(faceZ - z, faceX - x) * 180.0f / Math.PI.toFloat()).toFloat() - 90.0f
    }

    private fun enableJumpControl() {
        (jumpControl as GnomeJumpControl).canJump = true
    }

    private fun disableJumpControl() {
        (jumpControl as GnomeJumpControl).canJump = false
    }

    override fun getJumpPower(): Float {
        var baseJumpPower = 0.4f
        if (moveControl.getSpeedModifier() <= 0.6) baseJumpPower = 0.2f
        val path = navigation.getPath()
        if (path != null && !path.isDone) {
            val currentPos = path.getNextEntityPos(this)
            if (currentPos.y > y + 1) baseJumpPower = 0.8f
        }
        if (horizontalCollision || jumping && moveControl.getWantedY() > y + 1) {
            baseJumpPower = 0.8f
        }
        return super.getJumpPower(baseJumpPower / 0.42f)
    }

    override fun jumpFromGround() {
        super.jumpFromGround()
        val speedModifier = moveControl.getSpeedModifier()
        if (speedModifier > 0.0) {
            val current = deltaMovement.horizontalDistanceSqr()
            if (current < 0.01) moveRelative(0.1f, Vec3(0.0, 1.5, 1.0))
        }
        if (!level().isClientSide) {
            level().broadcastEntityEvent(this, 1.toByte())
        }
    }

    fun getJumpCompletion(a: Float): Float = if (jumpDuration == 0) 0.0f else (jumpTicks + a) / jumpDuration

    fun setSpeedModifier(speed: Double) {
        getNavigation().setSpeedModifier(speed)
        moveControl.setWantedPosition(moveControl.getWantedX(), moveControl.getWantedY(), moveControl.getWantedZ(), speed)
    }

    override fun setJumping(jump: Boolean) {
        super.setJumping(jump)
        if (jump) this.playSound(getJumpSound(), soundVolume, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 0.8f)
    }

    fun startJumping() {
        isJumping = true
        jumpDuration = 15
        jumpTicks = 0
    }

    class GnomeJumpControl(private val gnome: Gnome) : JumpControl(gnome) {
        var canJump = false
        fun wantJump(): Boolean = jump
        override fun tick() {
            if (jump) {
                gnome.startJumping()
                jump = false
            }
        }
    }

    private class GnomeMoveControl(private val gnome: Gnome) : MoveControl(gnome) {
        private var nextJumpSpeed = 0.0

        override fun tick() {
            if (gnome.onGround() && !gnome.jumping && !(gnome.jumpControl as GnomeJumpControl).wantJump()) gnome.setSpeedModifier(0.0)
            else if (hasWanted() || operation == Operation.JUMPING) gnome.setSpeedModifier(nextJumpSpeed)
            super.tick()
        }

        override fun setWantedPosition(x: Double, y: Double, z: Double, speedModifier: Double) {
            var speedModifier = speedModifier
            if (gnome.isInWater) speedModifier = 1.5
            super.setWantedPosition(x, y, z, speedModifier)
            if (speedModifier > 0.0)  nextJumpSpeed = speedModifier
        }
    }

    private class FindAndRideAnimalGoal(
        val gnome: Gnome,
        val searchDistance: Float = 8f
    ) : Goal() {
        val speedModifier = 0.8
        var nearestRideable: LivingEntity? = null
        var pathTick: Int = 0
        val targeting: TargetingConditions = TargetingConditions.forCombat()
            .range(searchDistance.toDouble())
            .selector { entity, level ->  entity.passengers.isEmpty() && !(entity is AgeableMob && entity.age<0) }

        override fun canUse(): Boolean {
            nearestRideable = getServerLevel(gnome)
                .getNearestEntity(
                    PazTags.EntityTypes.GNOME_RIDEABLE, targeting,
                    gnome, gnome.x, gnome.y, gnome.z,
                    gnome.boundingBox.inflate(searchDistance.toDouble(), 3.5, searchDistance.toDouble())
                )?: return false
            return !gnome.isPassenger
        }

        override fun canContinueToUse(): Boolean {
            return !gnome.isPassenger
                    && nearestRideable != null
                    && nearestRideable?.isAlive == true
                    && nearestRideable?.passengers?.isEmpty()==true
        }

        override fun stop() { nearestRideable = null }

        private fun checkAndRide(target: LivingEntity) {
            val canRide = gnome.isWithinMeleeAttackRange(target) && gnome.sensing.hasLineOfSight(target)
            if (canRide) gnome.startRiding(target)
        }

        override fun tick() {
            if (nearestRideable!=null) {
                if (--pathTick <= 0) {
                    pathTick = adjustedTickDelay(6)
                    gnome.getNavigation().moveTo(nearestRideable!!, speedModifier)
                    checkAndRide(nearestRideable!!)
                }
            }
        }
    }
}