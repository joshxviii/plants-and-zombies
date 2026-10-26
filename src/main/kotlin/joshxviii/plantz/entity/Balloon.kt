package joshxviii.plantz.entity

import LeashableEntity
import joshxviii.plantz.PazDataSerializers.DATA_DYE_COLOR
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.PazSounds
import joshxviii.plantz.entity.zombie.BrownCoatVariant
import joshxviii.plantz.entity.zombie.ImpVariant
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.item.BalloonItem
import net.fabricmc.fabric.api.networking.v1.PlayerLookup.level
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.Leashable.LeashData
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import kotlin.math.atan2
import kotlin.math.sqrt

class Balloon(
    type: EntityType<out Entity>,
    level: Level
) : LeashableEntity(type, level) {
    companion object {
        val DYE_COLOR: EntityDataAccessor<DyeColor> = SynchedEntityData.defineId(Balloon::class.java, DATA_DYE_COLOR)
        val TIME_UNLEASHED: EntityDataAccessor<Int> = SynchedEntityData.defineId(Balloon::class.java, EntityDataSerializers.INT)

        private const val MAX_PULL_PITCH = 25.0f
        private const val PITCH_SPEED_MULTIPLIER = 180.0f
        private const val PITCH_LERP_SPEED = 0.25f
        private const val YAW_LERP_SPEED = 0.5f
        private const val MIN_ROTATION_SPEED = 0.001f
        private const val HOLDER_GRAVITY_LIFT_MULTIPLIER = 0.25
        private const val HOLDER_PULL_STIFFNESS = 0.004
        private const val MAX_HOLDER_PULL_FORCE = 0.08
        private const val MAX_HOLDER_UPWARD_VELOCITY = 0.08
        private const val POPPING_HEIGHT = 96
    }

    var clientTiltZ: Float = 0f
    var clientTiltX: Float = 0f
    private val interpolation = InterpolationHandler(this)

    private var balloonLeashData: LeashData? = null
    var dyeColor: DyeColor
        get() = this.entityData.get(DYE_COLOR)
        set(value) = this.entityData.set(DYE_COLOR, value)

    var timeUnleashed: Int
        get() = this.entityData.get(TIME_UNLEASHED)
        set(value) = this.entityData.set(TIME_UNLEASHED, value)

    fun pop() {
        val level = level() as? ServerLevel ?: return
        playSound(PazSounds.BALLOON_POP)
        level.sendParticles(
            PazServerParticles.POP,
            x, y + boundingBox.ysize * 0.5, z,
            1,
            0.0, 0.0, 0.0, 0.0
        )
        discard()
    }

    private fun checkPoppingHeight() {
        if (tickCount > 200) {
            val floorHeight = y - level().getHeight(Heightmap.Types.WORLD_SURFACE, blockPosition()).toDouble()
            if (floorHeight > POPPING_HEIGHT * if (isLeashed) 1f else 0.5f) pop()
        }
        if (timeUnleashed > 2400) pop()
    }

    override fun getInterpolation(): InterpolationHandler = interpolation

    override fun save(output: ValueOutput): Boolean {
        output.putInt("unleash_time", timeUnleashed)
        return super.save(output)
    }

    override fun load(input: ValueInput) {
        super.load(input)
        timeUnleashed = input.getIntOr("unleash_time", 0)
    }

    override fun getLeashHolder(): Entity? {
        val holder = super.getLeashHolder()
        return holder
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        entityData.define(DYE_COLOR, DyeColor.WHITE)
        entityData.define(TIME_UNLEASHED, 0)
    }

    override fun baseTick() {
        super.baseTick()
        yRotO = yRot
        xRotO = xRot
    }

    override fun tick() {
        super.tick()
        checkPoppingHeight()
        if (leashHolder == null) timeUnleashed++
        else timeUnleashed = 0
        while (yRot - yRotO < -180.0f) yRotO -= 360.0f
        while (yRot - yRotO >= 180.0f) yRotO += 360.0f

        val horizontalSpeed = sqrt(deltaMovement.x * deltaMovement.x + deltaMovement.z * deltaMovement.z).toFloat()
        val targetPitch = (horizontalSpeed * PITCH_SPEED_MULTIPLIER).coerceAtMost(MAX_PULL_PITCH)

        xRot += (targetPitch - xRot) * PITCH_LERP_SPEED

        if (horizontalSpeed > MIN_ROTATION_SPEED) {
            val targetYaw = (atan2(deltaMovement.z, deltaMovement.x) * Mth.RAD_TO_DEG).toFloat() - 90.0f
            yRot += Mth.wrapDegrees(targetYaw - yRot) * YAW_LERP_SPEED * horizontalSpeed
        }

        if (!level().isClientSide) {
            pushCollidingEntities()
        }

        if (isRemoved) return
        if (isInterpolating) {
            getInterpolation().interpolate()
        } else if (canSimulateMovement()) {
            move(MoverType.SELF, deltaMovement)
            applyGravity()
            deltaMovement = deltaMovement.scale(0.98)
        } else {
            deltaMovement = deltaMovement.scale(0.98)
        }

        val holder = leashHolder as? LivingEntity?: return
        if ((holder as? Player)?.abilities?.flying == true) return
        if (y < holder.y) return
        val isFlyingMob = (holder is Mob && holder.navigation is FlyingPathNavigation)
        val verticalStretch = y - holder.y - 2.0
        if (verticalStretch <= 0.0) return

        val holderFloorHeight = (holder.y - level().getHeight(Heightmap.Types.WORLD_SURFACE, holder.blockPosition()).toDouble()).coerceAtLeast(0.0)
        val maxLiftHeight = if (isFlyingMob) 1.0 else 32.0
        val heightLimitForce = (holderFloorHeight / maxLiftHeight).coerceIn(0.0, 1.0)

        val gravityLift = holder.getAttributeValue(Attributes.GRAVITY) * HOLDER_GRAVITY_LIFT_MULTIPLIER
        val springLift = verticalStretch * HOLDER_PULL_STIFFNESS
        val totalLift = ((gravityLift + springLift) * (1.0 - heightLimitForce))
            .coerceAtMost(MAX_HOLDER_PULL_FORCE)

        val currentYVelocity = holder.deltaMovement.y
        val availableLift = (MAX_HOLDER_UPWARD_VELOCITY - currentYVelocity).coerceAtLeast(0.0)
        val appliedLift = totalLift.coerceAtMost(availableLift) * if (isFlyingMob) 0.15 else 1.0

        if (holder.isCrouching) {
            holder.deltaMovement = Vec3(holder.deltaMovement.x, holder.deltaMovement.y * 0.9, holder.deltaMovement.z)
            holder.needsSync = true
            holder.checkFallDistanceAccumulation()
            holder.checkFallDistanceAccumulation()
        }
        else if (appliedLift > 0.0) {
            holder.addDeltaMovement(Vec3(0.0, appliedLift, 0.0))
            holder.needsSync = true
            holder.checkFallDistanceAccumulation()
            holder.checkFallDistanceAccumulation()
        } else if (holderFloorHeight >= maxLiftHeight && holder.deltaMovement.y > 0.02) {
            holder.deltaMovement = Vec3(holder.deltaMovement.x, holder.deltaMovement.y * 0.75, holder.deltaMovement.z)
            holder.needsSync = true
        }
    }

    override fun dropLeash() {
        removeLeash()
    }

    private fun pushCollidingEntities() {
        val entities = level().getPushableEntities(this, boundingBox.inflate(0.25))
        for (entity in entities) {
            push(entity)
        }
    }

    override fun push(entity: Entity) {
        if (level().isClientSide) return
        if (entity.noPhysics || noPhysics) return
        if (hasPassenger(entity)) return

        var xa = entity.x - x
        var za = entity.z - z
        var distanceSquared = xa * xa + za * za

        if (distanceSquared < 1.0E-4) return

        distanceSquared = sqrt(distanceSquared)
        xa /= distanceSquared
        za /= distanceSquared

        var strength = 1.0 / distanceSquared
        if (strength > 1.0) strength = 1.0

        xa *= strength * 0.05
        za *= strength * 0.05

        if (entity is Balloon) entity.push(xa*0.075, 0.0, za*0.075)
        else {
            push(-xa*2, 0.0, -za*2)
            entity.push(xa, 0.0, za)
        }
    }

    override fun getPickResult(): ItemStack = BalloonItem.stackFor(this.dyeColor)
    override fun isPushable(): Boolean = true
    override fun isPickable(): Boolean = true
    override fun getDefaultGravity(): Double = -0.005
    override fun leashElasticDistance(): Double = 3.0

    override fun hurtServer(
        level: ServerLevel,
        source: DamageSource,
        damage: Float
    ): Boolean {
        return isRemoved || if (this.isInvulnerableToBase(source)) false
        else {
            pop()
            true
        }
    }

    override fun lerpPositionAndRotationStep(stepsToTarget: Int, targetX: Double, targetY: Double, targetZ: Double, targetYRot: Double, targetXRot: Double) {
        val wrappedTargetYRot = yRot + Mth.wrapDegrees(targetYRot.toFloat() - yRot)
        super.lerpPositionAndRotationStep(stepsToTarget, targetX, targetY, targetZ, wrappedTargetYRot.toDouble(), targetXRot)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        this.writeLeashData(output, balloonLeashData)
        this.entityData.set(DYE_COLOR, dyeColor)
        output.store("plantz:Color", DyeColor.CODEC, dyeColor)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        this.readLeashData(input)
        this.dyeColor = this.entityData.get(DYE_COLOR)
        input.read("plantz:Color", DyeColor.CODEC).ifPresent { dyeColor -> this.dyeColor = dyeColor }
    }

    override fun getLeashData(): LeashData? {
        return balloonLeashData
    }

    override fun setLeashData(leashData: LeashData?) {
        balloonLeashData = leashData
    }

    override fun getLeashOffset(): Vec3 {
        return Vec3.ZERO
    }
}