package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.ai.goal.SummonMinionsGoal
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import net.minecraft.core.UUIDUtil
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import java.util.UUID
import java.util.EnumSet
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.jvm.optionals.getOrDefault

class BaronVonBats(type: EntityType<out BaronVonBats>, level: Level) : PazZombie(type, level) {

    companion object {
        val BAT_TRANSFORMATION: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId<Boolean>(BaronVonBats::class.java, EntityDataSerializers.BOOLEAN)
        val SUMMON_TIME_ID: EntityDataAccessor<Int> =
            SynchedEntityData.defineId<Int>(BaronVonBats::class.java, EntityDataSerializers.INT)
        val TELEPORT_TIME_ID: EntityDataAccessor<Int> =
            SynchedEntityData.defineId<Int>(BaronVonBats::class.java, EntityDataSerializers.INT)
        private const val TELEPORT_COOLDOWN = 190
        private const val ESCAPE_TRIGGER_DISTANCE = 10.0
        private const val MIN_TELEPORT_DISTANCE = 16.0
        private const val MAX_TELEPORT_DISTANCE = 24.0
        private const val TELEPORT_COOLDOWN_VARIANCE = 100
        const val TELEPORT_DURATION = 40
    }

    init {
        xpReward = 100
    }

    val summonAnimation: AnimationState = AnimationState()
    val teleportAnimation: AnimationState = AnimationState()

    var summoningTime: Int
        get() = entityData.get(SUMMON_TIME_ID)
        set(value) = entityData.set(SUMMON_TIME_ID, value)
    var teleportTime: Int
        get() = entityData.get(TELEPORT_TIME_ID)
        set(value) = entityData.set(TELEPORT_TIME_ID, value)

    var isTransformed: Boolean
        get() = this.entityData.get(BAT_TRANSFORMATION)
        set(value) {
            this.entityData.set(BAT_TRANSFORMATION, value)
            updateBehavior()
        }
    private val trackedBatIds = mutableSetOf<UUID>()
    private val trackedVampireIds = mutableSetOf<UUID>()
    private var teleportCooldown = TELEPORT_COOLDOWN
    private var pendingTeleportDestination: Vec3? = null

    override fun isImmobile(): Boolean = isTransformed || teleportTime > 0 || super.isImmobile()
    override fun getMoveControl() =
        if (summoningTime > 0 || teleportTime > 0) noMoveControl else super.getMoveControl()

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(BAT_TRANSFORMATION, false)
        entityData.define(SUMMON_TIME_ID, 0)
        entityData.define(TELEPORT_TIME_ID, 0)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("tracked_bats", UUIDUtil.CODEC_SET, trackedBatIds)
        output.store("tracked_vampire_browncoats", UUIDUtil.CODEC_SET, trackedVampireIds)
        output.putInt("teleport_cooldown", teleportCooldown)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        trackedBatIds.clear()
        trackedBatIds.addAll(input.read("tracked_bats", UUIDUtil.CODEC_SET).getOrDefault(emptySet()))
        trackedVampireIds.clear()
        trackedVampireIds.addAll(input.read("tracked_vampire_browncoats", UUIDUtil.CODEC_SET).getOrDefault(emptySet()))
        teleportCooldown = input.getIntOr("teleport_cooldown", TELEPORT_COOLDOWN)
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(3, NavigateToTargetGoal(this, speedModifier = 1.5, keepAwayDistance = 8.0, alwaysFaceTarget = false))
        goalSelector.addGoal(
            1,
            SummonMinionsGoal(
                this,
                PazEntities.BROWN_COAT,
                trackedVampireIds,
                isSummoning = { summoningTime > 0 },
                beginSummoning = { summoningTime = 1 },
                configureMinion = { if (it is BrownCoat) it.variant = BrownCoatVariant.VAMPIRE },
                canSummon = { teleportTime <= 0 }
            )
        )
        goalSelector.addGoal(1, TeleportAwayFromTargetGoal())
    }

    override fun addBehaviourGoals() {
        addBehaviourGoalsNoMelee()
    }

    fun updateBehavior() {

    }

    override fun handleAttributes(difficultyModifier: Float, spawnReason: EntitySpawnReason) {}

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun tick() {
        super.tick()
        if (summoningTime > 0) {
            summonAnimation.startIfStopped(tickCount)
            if (summoningTime++ >= 42) {
                summonAnimation.stop()
                summoningTime = 0
            }
        } else {
            summonAnimation.stop()
        }
        if (teleportCooldown > 0) teleportCooldown--
        if (teleportTime > 0) {
            teleportAnimation.startIfStopped(tickCount)
            if (teleportTime == TELEPORT_DURATION/2) {
                teleportAnimation.start(tickCount)
                if (level() is ServerLevel) completeTeleport()
            }
            if (teleportTime++ >= TELEPORT_DURATION) {
                teleportAnimation.stop()
                pendingTeleportDestination = null
                teleportTime = 0
            }
        } else {
            teleportAnimation.stop()
        }
    }

    private fun completeTeleport() {
        val level = level() as ServerLevel
        val destination = pendingTeleportDestination ?: run {
            teleportTime = 0
            return
        }
        val origin = position()
        if (!teleportTo(level, destination.x, destination.y, destination.z, emptySet(), yRot, xRot, false)) {
            teleportTime = 0
            pendingTeleportDestination = null
            return
        }

        pendingTeleportDestination = null
        level.sendParticles(ParticleTypes.PORTAL, origin.x, origin.y + 1.0, origin.z, 24, 0.4, 0.8, 0.4, 0.1)
        level.sendParticles(
            ParticleTypes.PORTAL,
            destination.x,
            destination.y + 1.0,
            destination.z,
            24,
            0.4,
            0.8,
            0.4,
            0.1
        )
        level.playSound(null, origin.x, origin.y, origin.z, SoundEvents.ENDERMAN_TELEPORT, soundSource, 1.0f, 1.0f)
        level.playSound(
            null,
            destination.x,
            destination.y,
            destination.z,
            SoundEvents.ENDERMAN_TELEPORT,
            soundSource,
            1.0f,
            1.0f
        )
    }

    override fun remove(reason: RemovalReason) {
        super.remove(reason)
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        setItemSlot(EquipmentSlot.MAINHAND, PazItems.BARON_STAFF.defaultInstance)
        setDropChance(EquipmentSlot.MAINHAND, 0.0f)
        return data
    }

    private inner class TeleportAwayFromTargetGoal : Goal() {
        private var destination: Vec3? = null

        init {
            flags = EnumSet.of(Flag.MOVE)
        }

        override fun canUse(): Boolean {
            if (teleportCooldown > 0 || teleportTime > 0 || summoningTime > 0 || isTransformed || !isAggressive) return false
            val target = target?.takeIf { it.isAlive } ?: return false
            if (distanceToSqr(target) > ESCAPE_TRIGGER_DISTANCE * ESCAPE_TRIGGER_DISTANCE) return false
            destination = findDestination(target)
            if (destination == null) teleportCooldown = random.nextInt(20, 40)
            return destination != null
        }

        override fun canContinueToUse(): Boolean = teleportTime > 0

        override fun start() {
            navigation.stop()
            val destination = destination ?: return
            teleportCooldown = TELEPORT_COOLDOWN + random.nextInt(TELEPORT_COOLDOWN_VARIANCE)
            pendingTeleportDestination = destination
            teleportTime = 1
        }

        override fun stop() {
            destination = null
        }

        private fun findDestination(target: LivingEntity): Vec3? {
            val level = level() as? ServerLevel ?: return null
            val awayX = x - target.x
            val awayZ = z - target.z
            val baseAngle = if (awayX * awayX + awayZ * awayZ < 1.0E-6) {
                random.nextDouble() * Math.PI * 2.0
            } else {
                atan2(awayZ, awayX)
            }

            repeat(16) {
                val angle = baseAngle + (random.nextDouble() - 0.5) * (Math.PI / 2.0)
                val distance =
                    MIN_TELEPORT_DISTANCE + random.nextDouble() * (MAX_TELEPORT_DISTANCE - MIN_TELEPORT_DISTANCE)
                val destinationX = target.x + cos(angle) * distance
                val destinationZ = target.z + sin(angle) * distance
                val blockX = Mth.floor(destinationX)
                val blockZ = Mth.floor(destinationZ)
                val baseY = Mth.floor(y)

                for (offset in 3 downTo -4) {
                    val feet = BlockPos(blockX, baseY + offset, blockZ)
                    if (!level.isLoaded(feet)) continue
                    if (!level.getBlockState(feet.below()).isFaceSturdy(level, feet.below(), Direction.UP)) continue
                    if (!level.getBlockState(feet).getCollisionShape(level, feet).isEmpty) continue
                    if (!level.getBlockState(feet.above()).getCollisionShape(level, feet.above()).isEmpty) continue

                    val candidate = Vec3(destinationX, feet.y.toDouble(), destinationZ)
                    val shiftedBox = boundingBox.move(candidate.subtract(position()))
                    if (level.noCollision(this@BaronVonBats, shiftedBox)) return candidate
                }
            }
            return null
        }
    }
}