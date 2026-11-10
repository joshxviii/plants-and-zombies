package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazTags
import joshxviii.plantz.ai.goal.SummonMinionsGoal
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.entity.ZombieEquipment.COFFIN
import joshxviii.plantz.entity.ZombieEquipment.equipZombieEquipment
import net.minecraft.core.UUIDUtil
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.TagKey
import net.minecraft.util.Mth
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
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
        private val BEAM_TIME_ID: EntityDataAccessor<Int> =
            SynchedEntityData.defineId<Int>(BaronVonBats::class.java, EntityDataSerializers.INT)
        private val BEAM_COOLDOWN_ID: EntityDataAccessor<Int> =
            SynchedEntityData.defineId<Int>(BaronVonBats::class.java, EntityDataSerializers.INT)
        private val TRANSFORM_DELAY_ID: EntityDataAccessor<Int> =
            SynchedEntityData.defineId<Int>(BaronVonBats::class.java, EntityDataSerializers.INT)
        private const val TELEPORT_COOLDOWN = 190
        private const val ESCAPE_TRIGGER_DISTANCE = 10.0
        private const val MIN_TELEPORT_DISTANCE = 16.0
        private const val MAX_TELEPORT_DISTANCE = 24.0
        private const val TELEPORT_COOLDOWN_VARIANCE = 100
        private const val TRANSFORMATION_DURATION = 200
        private const val TRANSFORMATION_COOLDOWN_MIN = 600
        private const val TRANSFORMATION_COOLDOWN_MAX = 1000
        private const val BEAM_DURATION = 20
        private const val BEAM_COOLDOWN = 220
        private const val BEAM_RANGE = 14.0
        const val TELEPORT_DURATION = 40
    }

    init {
        xpReward = 100
    }

    val summonAnimation: AnimationState = AnimationState()
    val teleportAnimation: AnimationState = AnimationState()
    val transformAnimation: AnimationState = AnimationState()

    var summoningTime: Int
        get() = entityData.get(SUMMON_TIME_ID)
        set(value) = entityData.set(SUMMON_TIME_ID, value)
    var teleportTime: Int
        get() = entityData.get(TELEPORT_TIME_ID)
        set(value) = entityData.set(TELEPORT_TIME_ID, value)
    private var beamAttackTime: Int
        get() = entityData.get(BEAM_TIME_ID)
        set(value) = entityData.set(BEAM_TIME_ID, value)
    private var beamCooldown: Int
        get() = entityData.get(BEAM_COOLDOWN_ID)
        set(value) = entityData.set(BEAM_COOLDOWN_ID, value)
    private var transformDelay: Int
        get() = entityData.get(TRANSFORM_DELAY_ID)
        set(value) = entityData.set(TRANSFORM_DELAY_ID, value)

    var isTransformed: Boolean
        get() = this.entityData.get(BAT_TRANSFORMATION)
        set(value) {
            this.entityData.set(BAT_TRANSFORMATION, value)
            updateBehavior()
        }
    private val trackedBatIds = mutableSetOf<UUID>()
    private val trackedVampireIds = mutableSetOf<UUID>()
    private var teleportCooldown = TELEPORT_COOLDOWN
    private var transformationCooldown = TRANSFORMATION_COOLDOWN_MIN
    private var transformationTime = 0
    private var pendingTeleportDestination: Vec3? = null

    override fun isImmobile(): Boolean =
        isTransformed || transformDelay > 0 || teleportTime > 0 || super.isImmobile()
    override fun isPickable(): Boolean = !isTransformed && super.isPickable()
    override fun isPushable(): Boolean = !isTransformed && super.isPushable()
    override fun getMoveControl() =
        if (summoningTime > 0 || transformDelay > 0 || teleportTime > 0) noMoveControl else super.getMoveControl()

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(BAT_TRANSFORMATION, false)
        entityData.define(SUMMON_TIME_ID, 0)
        entityData.define(TELEPORT_TIME_ID, 0)
        entityData.define(BEAM_TIME_ID, 0)
        entityData.define(BEAM_COOLDOWN_ID, BEAM_COOLDOWN)
        entityData.define(TRANSFORM_DELAY_ID, 0)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("tracked_bats", UUIDUtil.CODEC_SET, trackedBatIds)
        output.store("tracked_vampire_browncoats", UUIDUtil.CODEC_SET, trackedVampireIds)
        output.putInt("teleport_cooldown", teleportCooldown)
        output.putInt("beam_cooldown", beamCooldown)
        output.putInt("transformation_cooldown", transformationCooldown)
        output.putInt("transformation_time", transformationTime)
        output.putInt("transform_delay", transformDelay)
        output.putBoolean("is_transformed", isTransformed)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        trackedBatIds.clear()
        trackedBatIds.addAll(input.read("tracked_bats", UUIDUtil.CODEC_SET).getOrDefault(emptySet()))
        trackedVampireIds.clear()
        trackedVampireIds.addAll(input.read("tracked_vampire_browncoats", UUIDUtil.CODEC_SET).getOrDefault(emptySet()))
        teleportCooldown = input.getIntOr("teleport_cooldown", TELEPORT_COOLDOWN)
        beamCooldown = input.getIntOr("beam_cooldown", BEAM_COOLDOWN)
        beamAttackTime = 0
        transformationCooldown = input.getIntOr("transformation_cooldown", TRANSFORMATION_COOLDOWN_MIN)
        transformationTime = input.getIntOr("transformation_time", 0)
        transformDelay = input.getIntOr("transform_delay", 0)
        entityData.set(BAT_TRANSFORMATION, input.getBooleanOr("is_transformed", false))
        updateBehavior()
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(3, NavigateToTargetGoal(this, speedModifier = 1.5, keepAwayDistance = 7.0, alwaysFaceTarget = true))
        goalSelector.addGoal(
            1,
            SummonMinionsGoal(
                this,
                PazEntities.BROWN_COAT,
                trackedVampireIds,
                isSummoning = { summoningTime > 0 },
                beginSummoning = { summoningTime = 1 },
                configureMinion = { if (it is BrownCoat) {
                    it.variant = BrownCoatVariant.VAMPIRE
                    if (it.getItemBySlot(EquipmentSlot.CHEST).isEmpty && random.nextFloat() < 0.2f) {
                        it.setItemSlot(EquipmentSlot.CHEST, COFFIN())
                        it.setDropChance(EquipmentSlot.CHEST, 0f)
                    }
                } },
                canSummon = {
                    teleportTime <= 0 && transformDelay <= 0 && beamAttackTime <= 0 && !isTransformed
                }
            )
        )
        goalSelector.addGoal(1, TeleportAwayFromTargetGoal())
        goalSelector.addGoal(2, StaffBeamAttackGoal())
    }

    override fun addBehaviourGoals() {
        addBehaviourGoalsNoMelee()
    }

    fun updateBehavior() {
        isInvisible = isTransformed
        isNoGravity = isTransformed
        isSilent = isTransformed
    }

    override fun handleAttributes(difficultyModifier: Float, spawnReason: EntitySpawnReason) {}

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        return !isTransformed && super.hurtServer(level, source, damage)
    }

    fun hurtAsBaronBat(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        return super.hurtServer(level, source, damage)
    }

    override fun getPreferredWeaponType(): TagKey<Item> = PazTags.ItemTags.BARON_PREFERRED_WEAPONS
    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun tick() {
        super.tick()
        updateBehavior()
        val serverLevel = level() as? ServerLevel
        if (serverLevel != null) {
            if (isTransformed) {
                transformationTime--
                val anyLivingBats = trackedBatIds.any { id ->
                    (serverLevel.getEntity(id) as? Zombat)?.isAlive == true
                }
                if (transformationTime <= 0 || !anyLivingBats || !isAlive) {
                    returnToBaron(serverLevel)
                }
            } else if (transformDelay > 0) {
                transformDelay--
                if (transformDelay == 0) transformIntoBats(serverLevel)
            } else {
                if (transformationCooldown > 0) transformationCooldown--
                if (beamCooldown > 0) beamCooldown--
                if (
                    transformationCooldown <= 0 &&
                    target?.isAlive == true &&
                    teleportTime <= 0 &&
                    summoningTime <= 0 &&
                    beamAttackTime <= 0 &&
                    !isTransformed
                ) {
                    beginBatTransformation()
                }
            }
        }
        if (transformDelay > 0) transformAnimation.startIfStopped(tickCount)
        else transformAnimation.stop()
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

    private fun beginBatTransformation() {
        navigation.stop()
        transformDelay = 15
        transformAnimation.start(tickCount)
    }

    private inner class StaffBeamAttackGoal : Goal() {
        init {
            flags = EnumSet.of(Flag.MOVE, Flag.LOOK)
        }

        override fun canUse(): Boolean {
            if (
                beamCooldown > 0 || beamAttackTime > 0 || isTransformed ||
                transformDelay > 0 || teleportTime > 0 || summoningTime > 0
            ) return false
            val target = target?.takeIf { it.isAlive } ?: return false
            return distanceToSqr(target) <= BEAM_RANGE * BEAM_RANGE
        }

        override fun canContinueToUse(): Boolean =
            beamAttackTime > 0 && !isTransformed &&
                target?.let { it.isAlive && distanceToSqr(it) <= BEAM_RANGE * BEAM_RANGE } == true

        override fun requiresUpdateEveryTick(): Boolean = true

        override fun start() {
            navigation.stop()
            beamAttackTime = BEAM_DURATION
            beamCooldown = BEAM_COOLDOWN
        }

        override fun tick() {
            val target = target?.takeIf { it.isAlive } ?: return
            lookControl.setLookAt(target, 4.0f, 30.0f)
            startUsingItem(usedItemHand)
            beamAttackTime--
        }

        override fun stop() {
            beamAttackTime = 0
            stopUsingItem()
        }
    }

    private fun transformIntoBats(serverLevel: ServerLevel) {
        val target = target?.takeIf { it.isAlive } ?: run {
            transformationCooldown = 20
            return
        }
        val count = random.nextInt(6, 8)
        val bats = mutableListOf<Zombat>()

        repeat(count) {
            val angle = random.nextDouble() * Math.PI * 2.0
            val spawnPos = Vec3(x + cos(angle) * 1.5, y + 0.5, z + sin(angle) * 1.5)
            val bat = PazEntities.ZOMBAT.create(serverLevel, EntitySpawnReason.MOB_SUMMONED) ?: return@repeat
            bat.snapTo(spawnPos, random.nextFloat() * 360.0f, 0.0f)
            bat.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPosition()), EntitySpawnReason.MOB_SUMMONED, null)
            bat.baronId = uuid
            bat.target = target
            bat.setPersistenceRequired()
            if (serverLevel.addFreshEntity(bat)) {
                if (bats.isEmpty()) {
                    bat.isBaronBat = true
                    bat.customName = customName
                    startRiding(bat)
                }
                bats.add(bat)
            }
        }

        if (bats.isEmpty()) {
            transformationCooldown = 20
            return
        }

        val batMaxHealth = maxHealth / bats.size
        val batHealth = health / bats.size
        trackedBatIds.clear()
        bats.forEach { bat ->
            bat.getAttribute(Attributes.MAX_HEALTH)?.baseValue = batMaxHealth.toDouble()
            bat.getAttribute(Attributes.ATTACK_DAMAGE)?.baseValue = 2.0
            bat.health = batHealth.coerceAtMost(bat.maxHealth)
            trackedBatIds.add(bat.uuid)
        }
        transformationTime = TRANSFORMATION_DURATION
        transformDelay = 0
        transformationCooldown = random.nextInt(TRANSFORMATION_COOLDOWN_MIN, TRANSFORMATION_COOLDOWN_MAX + 1)
        navigation.stop()
        isTransformed = true
        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x, y + 1.0, z, 18, 0.5, 0.5, 0.5, 0.04)
        playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.2f)
    }

    private fun returnToBaron(serverLevel: ServerLevel) {
        val bats = trackedBatIds.mapNotNull { serverLevel.getEntity(it) as? Zombat }
        noPhysics = false
        bats.forEach { it.discard() }
        trackedBatIds.clear()
        transformationTime = 0
        isTransformed = false
        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x, y + 1.0, z, 18, 0.5, 0.5, 0.5, 0.04)
        playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 0.8f)
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
        level.sendParticles(ParticleTypes.PORTAL, destination.x, destination.y + 1.0, destination.z, 24, 0.4, 0.8, 0.4, 0.1)
        level.playSound(null, origin.x, origin.y, origin.z, SoundEvents.ENDERMAN_TELEPORT, soundSource, 1.0f, 1.0f)
        level.playSound(null, destination.x, destination.y, destination.z, SoundEvents.ENDERMAN_TELEPORT, soundSource, 1.0f, 1.0f)
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
            if (
                teleportCooldown > 0 || teleportTime > 0 || summoningTime > 0 ||
                transformDelay > 0 || beamAttackTime > 0 || isTransformed || !isAggressive
            ) return false
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