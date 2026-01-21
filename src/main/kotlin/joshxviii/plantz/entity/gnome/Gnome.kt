package joshxviii.plantz.entity.gnome

import PazDataSerializers.GNOME_SOUND_VARIANT
import PazDataSerializers.GNOME_VARIANT
import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.control.JumpControl
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3

class Gnome(type: EntityType<out Gnome>, level: Level) : Monster(type, level) {

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
        this.goalSelector.addGoal(3, RandomLookAroundGoal(this))
        this.goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        this.goalSelector.addGoal(5, MeleeAttackGoal(this, 1.0, true))
        this.goalSelector.addGoal(6, WaterAvoidingRandomStrollGoal(this, 0.6))
        this.goalSelector.addGoal(2, SpearUseGoal(this, 1.0, 1.0, 10.0f, 2.0f))
        this.targetSelector.addGoal(1, HurtByTargetGoal(this).setAlertOthers())
        this.targetSelector.addGoal(2, NearestAttackableTargetGoal(this, LivingEntity::class.java, true) { target, level ->
            target !is Gnome
        })
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
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        setCanPickUpLoot(true)

        if (random.nextFloat() < 0.25) setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_PICKAXE.defaultInstance)
        variant = GnomeVariant.pickRandomVariant()
        soundVariant = GnomeSoundVariant.pickRandomVariant()

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
        jumpDelayTicks = if (moveControl.getSpeedModifier() < 2.2) 5 else 1
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
            if (currentPos.y > y + 0.5)  baseJumpPower = 0.5f
        }
        if (horizontalCollision || jumping && moveControl.getWantedY() > y + 0.5) {
            baseJumpPower = 0.5f
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
}