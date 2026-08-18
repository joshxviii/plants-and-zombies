package joshxviii.plantz.entity

import LeashableEntity
import joshxviii.plantz.PazDataSerializers.DATA_DYE_COLOR
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.item.BalloonItem
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.Leashable.LeashData
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
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

        private const val MAX_PULL_PITCH = 25.0f
        private const val PITCH_SPEED_MULTIPLIER = 180.0f
        private const val PITCH_LERP_SPEED = 0.25f
        private const val YAW_LERP_SPEED = 0.5f
        private const val MIN_ROTATION_SPEED = 0.001f
        private const val HOLDER_GRAVITY_LIFT_MULTIPLIER = 0.4
        private const val HOLDER_PULL_STIFFNESS = 0.0075
        private const val MAX_HOLDER_PULL_FORCE = 0.16
        private const val MAX_HOLDER_UPWARD_VELOCITY = 0.5

        fun spawnAndEquip(level: Level, pos: Vec3, dyeColor: DyeColor, entity: LivingEntity) {
            val balloon = Balloon(PazEntities.BALLOON, level)
            balloon.setPos(pos.x, pos.y, pos.z)
            balloon.dyeColor = dyeColor

        }
    }
    private val interpolation = InterpolationHandler(this)

    private var balloonLeashData: LeashData? = null
    var dyeColor: DyeColor
        get() = this.entityData.get(DYE_COLOR)
        set(value) = this.entityData.set(DYE_COLOR, value)

    override fun getInterpolation(): InterpolationHandler = interpolation

    override fun load(input: ValueInput) {
        super.load(input)
        val holder = leashHolder
        if (holder is PazZombie && !holder.balloons.contains(this)) {
            holder.balloons.add(this)
        }
    }

    override fun getLeashHolder(): Entity? {
        val holder = super.getLeashHolder()
        if (holder is PazZombie && !holder.balloons.contains(this)) {
            holder.balloons.add(this)
        }
        return holder
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        entityData.define(DYE_COLOR, DyeColor.WHITE)
    }

    override fun baseTick() {
        super.baseTick()
        yRotO = yRot
        xRotO = xRot
    }

    override fun tick() {
        super.tick()
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

        val holder = leashHolder as? LivingEntity ?: return
        if ((holder as? Player)?.abilities?.flying == true) return
        if (y < holder.y) return
        val verticalStretch = y - holder.y - leashElasticDistance()
        if (verticalStretch <= 0.0) return

        val floorHeight = y - level().getHeight(Heightmap.Types.WORLD_SURFACE, blockPosition()).toDouble()
        val heightLimitForce = (floorHeight / 64).coerceIn(0.0, 1.0)

        val crouchMultiplier = if (holder.isCrouching) 0.5 else 1.0
        val gravityLift = holder.getAttributeValue(Attributes.GRAVITY) * HOLDER_GRAVITY_LIFT_MULTIPLIER
        val springLift = verticalStretch * HOLDER_PULL_STIFFNESS
        val totalLift = ((gravityLift + springLift) * (crouchMultiplier - heightLimitForce))
            .coerceAtMost(MAX_HOLDER_PULL_FORCE)

        val currentYVelocity = holder.deltaMovement.y
        val availableLift = (MAX_HOLDER_UPWARD_VELOCITY - currentYVelocity).coerceAtLeast(0.0)
        val appliedLift = totalLift.coerceAtMost(availableLift)

        if (appliedLift <= 0.0) return

        holder.addDeltaMovement(Vec3(0.0, appliedLift, 0.0))
        holder.needsSync = true
        holder.checkFallDistanceAccumulation()
        holder.checkFallDistanceAccumulation()
    }

    override fun dropLeash() {
        super.dropLeash()
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
    override fun leashTooFarBehaviour() {
        super.leashTooFarBehaviour()
    }
    override fun closeRangeLeashBehaviour(leashHolder: Entity) {
        super.closeRangeLeashBehaviour(leashHolder)
    }

    override fun onElasticLeashPull() {
        super.onElasticLeashPull()
    }

    override fun setLeashedTo(holder: Entity, synch: Boolean) {
        super.setLeashedTo(holder, synch)
    }

    override fun onLeashRemoved() {
        val holder = this.leashHolder
        super.onLeashRemoved()
    }

    override fun hurtServer(
        level: ServerLevel,
        source: DamageSource,
        damage: Float
    ): Boolean {
        return if (isRemoved) true
        else if (this.isInvulnerableToBase(source)) false
        else {
            playSound(SoundEvents.LAVA_POP) // TODO custom sounds
            level.sendParticles(
                PazServerParticles.POP,
                x, y + boundingBox.ysize * 0.5, z,
                1,
                0.0, 0.0, 0.0, 0.0
            )
            discard()
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