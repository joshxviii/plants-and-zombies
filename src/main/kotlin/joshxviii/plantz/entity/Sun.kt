package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.FluidTags
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.math.min
import kotlin.math.sqrt

class Sun(type: EntityType<out Sun>, level: Level) : Entity(type, level) {
    private var age = 0
    private var health = 5
    private var count = 1
    private var followingEntity: LivingEntity? = null
    private val interpolation = InterpolationHandler(this)

    constructor(level: Level, pos: Vec3, roughly: Vec3, value: Int) : this(PazEntities.SUN, level) {
        setPos(pos)
        if (!level.isClientSide) {
            yRot = random.nextFloat() * 360.0f
            var randomMovement = Vec3(
                (random.nextDouble() * 0.2 - 0.1) * 2.0,
                random.nextDouble() * 0.2 * 2.0,
                (random.nextDouble() * 0.2 - 0.1) * 2.0
            )
            if (roughly.lengthSqr() > 0.0 && roughly.dot(randomMovement) < 0.0) {
                randomMovement = randomMovement.scale(-1.0)
            }

            val size = boundingBox.getSize()
            setPos(pos.add(roughly.normalize().scale(size * ORB_MERGE_DISTANCE)))
            deltaMovement = randomMovement
            if (!level.noCollision(boundingBox)) unstuckIfPossible(size)
        }
        this.value = value
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

    override fun getMovementEmission(): MovementEmission = MovementEmission.NONE

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) { entityData.define(DATA_VALUE, 0) }

    override fun getDefaultGravity(): Double = 0.04

    override fun tick() {
        interpolation.interpolate()
        if (firstTick && level().isClientSide) firstTick = false
        else {
            super.tick()
            val colliding = !level().noCollision(boundingBox)
            if (isEyeInFluid(FluidTags.WATER)) setUnderwaterMovement()
            else if (!colliding) applyGravity()

            if (level().getFluidState(blockPosition()).`is`(FluidTags.LAVA)) {
                setDeltaMovement(
                    ((random.nextFloat() - random.nextFloat()) * 0.2f).toDouble(),
                    0.2,
                    ((random.nextFloat() - random.nextFloat()) * 0.2f).toDouble()
                )
            }

            if (tickCount % ENTITY_SCAN_PERIOD == 1) scanForMerges()

            followNearbyEntity()
            if (followingEntity == null && !level().isClientSide && colliding) {
                val nextColliding = !level().noCollision(boundingBox.move(deltaMovement))
                if (nextColliding) {
                    moveTowardsClosestSpace(x, (boundingBox.minY + boundingBox.maxY) / 2.0, z)
                    needsSync = true
                }
            }

            val fallSpeed = deltaMovement.y
            move(MoverType.SELF, deltaMovement)
            applyEffectsFromBlocks()
            var friction = 0.98f
            if (onGround()) {
                friction = level().getBlockState(blockPosBelowThatAffectsMyMovement).block.getFriction() * 0.98f
            }

            deltaMovement = deltaMovement.scale(friction.toDouble())
            if (verticalCollisionBelow && fallSpeed < -gravity) {
                deltaMovement = Vec3(deltaMovement.x, -fallSpeed * 0.8, deltaMovement.z)
            }

            if (age++ >= LIFETIME) discard()
        }
    }

    private fun followNearbyEntity() {
        followingEntity = getNearestEntity(MAX_FOLLOW_DIST)
//        if (followingEntity == null
//            || followingEntity!!.isSpectator
//            || followingEntity!!.distanceToSqr(this) > 64.0
//            || followingEntity!!.health == followingEntity!!.maxHealth
//        ) {
//            followingEntity = getNearestEntity(8.0)
//        }

        if (followingEntity != null) {
            val delta = Vec3(
                followingEntity!!.x - x,
                (followingEntity!!.y + followingEntity!!.eyeHeight - y),
                followingEntity!!.z - z
            )
            val length = delta.lengthSqr()
            val power = 1.0 - sqrt(length) / MAX_FOLLOW_DIST
            deltaMovement = deltaMovement.add(delta.normalize().multiply(Vec3(1.0,1.5,1.0)).scale(power * power * 0.1))
            if ( boundingBox.inflate(0.1).intersects(followingEntity!!.boundingBox) ) {
                touchedEntity(followingEntity)

            }
        }
    }

    private fun touchedEntity(entity: LivingEntity?) {
        if (entity is Player) {
            val serverLevel = level() as? ServerLevel
            if (serverLevel!=null) {
                val itemStack = PazItems.SUN.defaultInstance
                itemStack.count = this.value
                if (
                    (!entity.isCreative && entity.inventory.add(itemStack))
                    || entity.isCreative
                ) {
                    playPickupSound()
                    discard()
                }
            }
        }
        else if (entity is Plant && !entity.isDeadOrDying) {
            this.value -= entity.sunHeal(this.value).toInt()
            entity.addParticlesAroundSelf(particle = ParticleTypes.HAPPY_VILLAGER)
            playPickupSound()
        }
        if (value <= 0) discard()
    }

    fun getNearestEntity(range: Double = 3.5) : LivingEntity? {
        var best = -1.0
        var result: LivingEntity? = null
        val candidates = level().getEntitiesOfClass(
            LivingEntity::class.java,
            boundingBox.inflate(range),
            Predicate {
                !it.isDeadOrDying && !it.isSpectator
                ( (it is Plant && it.health != it.maxHealth) || it is Player )
            }
        )
        for (candidate in candidates) {
            val dist: Double = candidate.distanceToSqr(x, y, z)
            if ((range < 0.0 || dist < range * range) && (best == -1.0 || dist < best)) {
                best = dist
                result = candidate
            }
        }
        return result
    }

    private fun playPickupSound() {
        playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F)
    }

    override fun getBlockPosBelowThatAffectsMyMovement(): BlockPos = getOnPos(0.999999f)

    private fun scanForMerges() {
        if (level() is ServerLevel) {
            for (orb in level().getEntities(
                EntityTypeTest.forClass(Sun::class.java),
                boundingBox.inflate(ORB_MERGE_DISTANCE),
                Predicate { orb: Sun -> canMerge(orb) })) {
                merge(orb)
            }
        }
    }

    private fun canMerge(orb: Sun): Boolean = orb !== this && canMerge(orb, id, value)

    private fun merge(orb: Sun) {
        count += orb.count
        age = min(age, orb.age)
        orb.discard()
    }

    private fun setUnderwaterMovement() {
        val movement = deltaMovement
        setDeltaMovement(movement.x * 0.99f, min(movement.y + 5.0E-4f, 0.06), movement.z * 0.99f)
    }

    override fun doWaterSplashEffect() {}

    override fun hurtClient(source: DamageSource): Boolean = !isInvulnerableToBase(source)

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        if (isInvulnerableToBase(source)) return false
        else {
            markHurt()
            health = (health - damage).toInt()
            if (health <= 0) discard()
            return true
        }
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        output.putShort("Health", health.toShort())
        output.putShort("Age", age.toShort())
        output.putShort("Value", value.toShort())
        output.putInt("Count", count)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        health = input.getShortOr("Health", DEFAULT_HEALTH)
        age = input.getShortOr("Age", DEFAULT_AGE)
        value = input.getShortOr("Value", DEFAULT_VALUE)
        count = input.read("Count", ExtraCodecs.POSITIVE_INT).orElse(DEFAULT_COUNT) as Int
    }

    var value: Int
        get() = entityData.get(DATA_VALUE)
        private set(value) {
            entityData.set(DATA_VALUE, value)
        }

    val icon: Int
        get() {
            val value = value
            if (value >= 2477) {
                return 10
            } else if (value >= 1237) {
                return 9
            } else if (value >= 617) {
                return 8
            } else if (value >= 307) {
                return 7
            } else if (value >= 149) {
                return 6
            } else if (value >= 73) {
                return 5
            } else if (value >= 37) {
                return 4
            } else if (value >= 17) {
                return 3
            } else if (value >= 7) {
                return 2
            } else {
                return if (value >= 3) 1 else 0
            }
        }

    override fun isAttackable(): Boolean = false

    override fun getSoundSource(): SoundSource = SoundSource.AMBIENT

    override fun getInterpolation(): InterpolationHandler = interpolation

    companion object {
        val DATA_VALUE: EntityDataAccessor<Int> =
            SynchedEntityData.defineId<Int>(Sun::class.java, EntityDataSerializers.INT)
        private const val LIFETIME = 800
        private const val ENTITY_SCAN_PERIOD = 20
        private const val MAX_FOLLOW_DIST = 8.0
        private const val ORB_GROUPS_PER_AREA = 40
        private const val ORB_MERGE_DISTANCE = 0.5
        private const val DEFAULT_HEALTH: Short = 5
        private const val DEFAULT_AGE: Short = 0
        private const val DEFAULT_VALUE: Short = 0
        private const val DEFAULT_COUNT = 1
        fun award(level: ServerLevel, pos: Vec3, amount: Int) {
            awardWithDirection(level, pos, Vec3.Y_AXIS, amount)
        }

        fun awardWithDirection(level: ServerLevel, pos: Vec3, roughDirection: Vec3, amount: Int) {
            var amount = amount
            while (amount > 0) {
                val newCount = getExperienceValue(amount)
                amount -= newCount
                if (!tryMergeToExisting(level, pos, newCount)) level.addFreshEntity(Sun(level, pos, roughDirection, newCount))
            }
        }

        private fun tryMergeToExisting(level: ServerLevel, pos: Vec3, value: Int): Boolean {
            val box = AABB.ofSize(pos, 1.0, 1.0, 1.0)
            val id = level.getRandom().nextInt(ORB_GROUPS_PER_AREA)
            val orbs: MutableList<Sun> = level.getEntities(
                EntityTypeTest.forClass(Sun::class.java),
                box,
                Predicate { orbx: Sun -> canMerge(orbx, id, value) })
            if (!orbs.isEmpty()) {
                val orb = orbs[0]
                orb.count++
                orb.age = 0
                return true
            } else return false
        }

        private fun canMerge(orb: Sun, id: Int, value: Int): Boolean {
            return !orb.isRemoved && (orb.id - id) % ORB_GROUPS_PER_AREA == 0 && orb.value == value
        }

        fun getExperienceValue(maxValue: Int): Int {
            if (maxValue >= 2477) {
                return 2477
            } else if (maxValue >= 1237) {
                return 1237
            } else if (maxValue >= 617) {
                return 617
            } else if (maxValue >= 307) {
                return 307
            } else if (maxValue >= 149) {
                return 149
            } else if (maxValue >= 73) {
                return 73
            } else if (maxValue >= 37) {
                return 37
            } else if (maxValue >= 17) {
                return 17
            } else if (maxValue >= 7) {
                return 7
            } else {
                return if (maxValue >= 3) 3 else 1
            }
        }
    }
}