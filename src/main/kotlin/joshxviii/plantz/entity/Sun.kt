package joshxviii.plantz.entity

import joshxviii.plantz.*
import joshxviii.plantz.PazBlocks.SUN_BATTERY_POI
import joshxviii.plantz.block.entity.SunBatteryBlockEntity
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import java.util.function.Consumer
import kotlin.math.min

class Sun(type: EntityType<out Sun>, level: Level) : Entity(type, level) {
    private var age = 0
    private var followingEntity: LivingEntity? = null
    private var batteryPos: Vec3? = null
    private val interpolation = InterpolationHandler(this, 2)

    companion object {
        val DATA_VALUE: EntityDataAccessor<Int> =
            SynchedEntityData.defineId<Int>(Sun::class.java, EntityDataSerializers.INT)
        val DATA_EXPIRING: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId<Boolean>(Sun::class.java, EntityDataSerializers.BOOLEAN)
        private const val EXPIRING_TICKS = 40
        private const val TARGET_SCAN_PERIOD = 20
        private const val MAX_FOLLOW_DIST = 8.0
        private const val MERGE_DISTANCE = 0.6
        private const val MAX_VALUE = 64
        private const val MAX_AWARD_VALUE = 3
        private const val DEFAULT_LIFETIME = 750

        @JvmStatic
        fun scaleForValue(value: Int): Float = 1.2f - (8.0f / (value.coerceAtLeast(1) + 8f))

        fun award(level: ServerLevel, pos: Vec3, amount: Int) {
            awardWithDirection(level, pos, Vec3.Y_AXIS, amount)
        }

        fun awardWithDirection(level: ServerLevel, pos: Vec3, roughDirection: Vec3, amount: Int) {
            var remaining = amount
            while (remaining > 0) {
                val chunk = remaining.coerceAtMost(MAX_AWARD_VALUE)
                remaining -= chunk
                level.addFreshEntity(Sun(level, pos, roughDirection, chunk))
            }
        }

        private fun tryMergeIntoExisting(level: ServerLevel, pos: Vec3, value: Int): Boolean {
            val target = level.getEntities(
                EntityTypeTest.forClass(Sun::class.java),
                AABB.ofSize(pos, 1.0, 1.0, 1.0)
            ) { it.canAbsorb(value) }.firstOrNull() ?: return false
            target.value += value
            target.age = 0
            return true
        }
    }

    var value: Int
        get() = entityData.get(DATA_VALUE)
        private set(value) {
            entityData.set(DATA_VALUE, value)
            if (value <= 0) discard()
        }

    val isExpiring: Boolean get() = entityData.get(DATA_EXPIRING)

    constructor(level: Level, pos: Vec3, roughly: Vec3, value: Int) : this(PazEntities.SUN, level) {
        this.value = value
        setPos(pos)
        if (!level.isClientSide) {
            var randomMovement = Vec3(
                (random.nextDouble() * 0.2 - 0.1) * 2.0,
                random.nextDouble() * 0.2 * 2.0,
                (random.nextDouble() * 0.2 - 0.1) * 2.0
            )
            if (roughly.lengthSqr() > 0.0 && roughly.dot(randomMovement) < 0.0) {
                randomMovement = randomMovement.scale(-1.0)
            }

            val size = boundingBox.size
            setPos(pos.add(roughly.normalize().scale(size * MERGE_DISTANCE)))
            deltaMovement = randomMovement
            if (!level.noCollision(boundingBox)) unstuckIfPossible(size)
        }
    }

    override fun getDimensions(pose: Pose): EntityDimensions {
        val size = (scaleForValue(value) * 0.8f).coerceAtLeast(0.25f)
        return EntityDimensions.fixed(size, size)
    }

    override fun onSyncedDataUpdated(accessor: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(accessor)
        if (accessor == DATA_VALUE) refreshDimensions()
    }

    fun unstuckIfPossible(maxDistance: Double) {
        val center = position().add(0.0, bbHeight / 2.0, 0.0)
        val allowedCenters = Shapes.create(AABB.ofSize(center, maxDistance, maxDistance, maxDistance))
        level()
            .findFreePosition(
                this,
                allowedCenters,
                center,
                bbWidth.toDouble(),
                bbHeight.toDouble(),
                bbWidth.toDouble()
            )
            .ifPresent(Consumer { pos: Vec3? -> setPos(pos!!.add(0.0, -bbHeight / 2.0, 0.0)) })
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        entityData.define(DATA_VALUE, 0)
        entityData.define(DATA_EXPIRING, false)
    }

    override fun getDefaultGravity(): Double = 0.015

    override fun tick() {
        super.tick()
        if (level().isClientSide) {
            interpolation.interpolate()
            return
        }

        val colliding = !level().noCollision(boundingBox)
        if (!colliding) applyGravity()
        if (isInWater) {
            deltaMovement = Vec3(deltaMovement.x * 0.9, (deltaMovement.y + 0.02).coerceAtMost(0.1), deltaMovement.z * 0.9)
        }

        if (tickCount % TARGET_SCAN_PERIOD == 0) {
            batteryPos = getNearestBattery(MAX_FOLLOW_DIST)
            followingEntity = getNearestEntity(MAX_FOLLOW_DIST)
            scanForMerges()
        }
        moveTowardsClosestTarget()
        if (isRemoved) return

        if (followingEntity == null && colliding) {
            val nextColliding = !level().noCollision(boundingBox.move(deltaMovement))
            if (nextColliding) moveTowardsClosestSpace(x, (boundingBox.minY + boundingBox.maxY) / 2.0, z)
        }

        val fallSpeed = deltaMovement.y
        move(MoverType.SELF, deltaMovement)
        var friction = 0.97f
        if (onGround()) {
            friction = level().getBlockState(blockPosBelowThatAffectsMyMovement).block.getFriction() * 0.97f
        }

        deltaMovement = deltaMovement.scale(friction.toDouble())
        // floaty bounce
        if (verticalCollisionBelow && fallSpeed < -gravity) {
            deltaMovement = Vec3(deltaMovement.x, -fallSpeed * 0.8, deltaMovement.z)
        }

        val expiring = getLifeTime() - age <= EXPIRING_TICKS
        if (expiring != isExpiring) entityData.set(DATA_EXPIRING, expiring)
        if (age++ >= getLifeTime()) discard()
    }

    fun getLifeTime(): Int = DEFAULT_LIFETIME

    private fun moveTowardsClosestTarget() {
        val battery = batteryPos
        val entity = followingEntity?.takeIf { it.isAlive }
        val targetPos: Vec3 = when {
            battery != null && (entity == null || battery.distanceToSqr(position()) < entity.distanceToSqr(position())) -> battery
            entity != null -> entity.position()
            else -> return
        }

        val delta = targetPos.subtract(position())
        val distance = delta.length()
        if (distance > MAX_FOLLOW_DIST) return
        val power = 1.0 - distance / MAX_FOLLOW_DIST

        deltaMovement = deltaMovement.add(
            delta.normalize()
                .multiply(1.0, 1.5, 1.0)
                .scale(power * power * 0.1)
        )

        if (targetPos === battery) {
            if (distanceToSqr(battery) < 0.5) touchedBattery(battery)
        } else if (entity != null && boundingBox.inflate(0.1).intersects(entity.boundingBox)) {
            touchedEntity(entity)
        }
    }

    private fun touchedBattery(pos: Vec3) {
        val blockPos = BlockPos.containing(pos)
        val battery = level().getBlockEntity(blockPos) as? SunBatteryBlockEntity ?: return
        if (battery.isFull()) return
        battery.addSun(1)
        level().blockEvent(blockPos, PazBlocks.SUN_BATTERY_BLOCK, 1, 2)
        value--
    }

    private fun touchedEntity(entity: LivingEntity) {
        if (level() !is ServerLevel) return
        var collected = 0
        while (value > 0) {
            val success = when (entity) {
                is Player -> tryToGiveSun(entity)
                is Plant -> entity.sunHeal()
                else -> false
            }
            if (!success) break
            collected++
            value--
        }
        if (collected > 0) playPickupSound()
    }

    fun tryToGiveSun(entity: Player): Boolean {
        if (entity.tryAddSunToStorage()) return true
        return (!entity.hasInfiniteMaterials() && entity.inventory.add(PazItems.SUN.defaultInstance)) || entity.isCreative
    }

    fun getNearestEntity(range: Double = 3.5): LivingEntity? {
        val rangeSq = range * range
        return level().getEntitiesOfClass(
            LivingEntity::class.java,
            boundingBox.inflate(range)
        ) {
            (it is Player && !it.isSpectator && it.hasSpaceForSun(PazItems.SUN.defaultInstance)) ||
                (it is Plant && it.health != it.maxHealth)
        }.filter { it.distanceToSqr(x, y, z) < rangeSq }
            .minByOrNull { it.distanceToSqr(x, y, z) }
    }

    fun getNearestBattery(range: Double = 4.5): Vec3? {
        val poiManager = (level() as? ServerLevel)?.poiManager ?: return null
        val pos: BlockPos = poiManager.findClosest(
            { it.value() == SUN_BATTERY_POI },
            blockPosition(),
            range.toInt(),
            Occupancy.ANY
        ).orElse(null) ?: return null
        if ((level().getBlockEntity(pos) as? SunBatteryBlockEntity)?.isFull() == true) return null
        return Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
    }

    private fun playPickupSound() {
        playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F)
    }

    override fun getBlockPosBelowThatAffectsMyMovement(): BlockPos = getOnPos(0.999999f)

    private fun canAbsorb(amount: Int): Boolean = !isRemoved && value + amount <= MAX_VALUE

    private fun scanForMerges() {
        if (age < 5) return
        for (other in level().getEntities(
            EntityTypeTest.forClass(Sun::class.java),
            boundingBox.inflate(MERGE_DISTANCE)
        ) { it !== this && id > it.id }) {
            if (!canAbsorb(other.value)) continue
            value += other.value
            age = min(age, other.age)
            other.discard()
        }
    }

    override fun doWaterSplashEffect() {}

    override fun getMovementEmission(): MovementEmission = MovementEmission.NONE

    override fun isPickable(): Boolean = false

    override fun isAttackable(): Boolean = false

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean = false

    override fun getSoundSource(): SoundSource = SoundSource.AMBIENT

    override fun getInterpolation(): InterpolationHandler = interpolation

    override fun addAdditionalSaveData(output: ValueOutput) {
        output.putShort("Age", age.toShort())
        output.putInt("Value", value)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        age = input.getShortOr("Age", 0.toShort()).toInt()
        value = input.getInt("Value").orElseGet { input.getShortOr("Value", 0.toShort()).toInt() }
    }
}
