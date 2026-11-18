package joshxviii.plantz.entity.blueprint_machines

import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazTags
import joshxviii.plantz.clampYRot
import joshxviii.plantz.entity.plant.ExplosivePlant.Companion.scaledExplosion
import joshxviii.plantz.item.BlueprintItem
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import kotlin.math.max

class LawnMower(type: EntityType<out LawnMower>, level: Level) : LivingEntity(type, level) {
    companion object {
        private val DATA_GRABBER_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(LawnMower::class.java, EntityDataSerializers.INT)
        private val AUTO_MOW_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(LawnMower::class.java, EntityDataSerializers.INT)

        const val MAX_HEALTH = 40f
        const val COLLISION_DAMAGE = 4f
        const val MAX_GRAB_RANGE_SQR = 8.0 * 8.0
        const val MIN_GRAB_DISTANCE = 0.9
        const val MAX_GRAB_DISTANCE = 1.1
        const val AUTO_MOW_TIME = 160

        @JvmField var clientGrabHook: ((LawnMower) -> Unit)? = null
    }

    val actionAnimation = AnimationState()

    private var handle: LawnMowerHandle? = null
    private var damageTaken = 0f
    private var grabDistance = 2.0

    var grabberId: Int
        get() = entityData.get(DATA_GRABBER_ID)
        private set(value) = entityData.set(DATA_GRABBER_ID, value)

    var autoMowTime: Int
        get() = entityData.get(AUTO_MOW_TIME_ID)
        private set(value) = entityData.set(AUTO_MOW_TIME_ID, value)

    var mowDamage: Float = COLLISION_DAMAGE

    val isGrabbed: Boolean get() = grabberId != -1
    val isAutoMowing: Boolean get() = autoMowTime > 0

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(DATA_GRABBER_ID, -1)
        entityData.define(AUTO_MOW_TIME_ID, 0)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putInt("auto_mow_time", autoMowTime)
        output.putFloat("mow_damage", mowDamage)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        autoMowTime = input.getIntOr("auto_mow_time", 0)
        mowDamage = input.getFloatOr("mow_damage", COLLISION_DAMAGE)
    }

    override fun onSyncedDataUpdated(accessor: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(accessor)
        if (accessor == DATA_GRABBER_ID || accessor == AUTO_MOW_TIME_ID && level().isClientSide) {
            if (isGrabbed || isAutoMowing) actionAnimation.startIfStopped(tickCount) else actionAnimation.stop()
        }
    }

    override fun getPickResult(): ItemStack = BlueprintItem.stackFor(this.type)

    fun startGrab(player: Player): InteractionResult {
        if (level().isClientSide) return InteractionResult.PASS
        if (isGrabbed) return if (grabberId == player.id) InteractionResult.PASS else InteractionResult.PASS
        val handleCenter = handle?.boundingBox?.center ?: position()
        grabDistance = player.eyePosition.distanceTo(handleCenter).coerceIn(MIN_GRAB_DISTANCE, MAX_GRAB_DISTANCE)
        grabberId = player.id
        return InteractionResult.PASS
    }

    fun release() {
        setRotation(clampYRot(yRot))
        autoMowTime = 0
        grabberId = -1
    }

    override fun maxUpStep(): Float {
        return max(super.maxUpStep(), 1.0f)
    }

    private fun grabber(): Player? = level().getEntity(grabberId) as? Player

    override fun tick() {
        val level = level()
        if (level.isClientSide) {
            super.tick()
            if (isGrabbed) clientGrabHook?.invoke(this)
            return
        }

        ensureHandle()
        if (damageTaken > 0f) damageTaken -= 1f

        val player = grabber()
        if (isGrabbed && (player == null || !player.isAlive || player.isSpectator
                    || player.level() !== level || player.distanceToSqr(this) > MAX_GRAB_RANGE_SQR)) {
            release()
        }
        if (player != null && isGrabbed) pullToward(player)

        super.tick()
        autoMowWhenPowered()

        if (isGrabbed || isAutoMowing) {
            val level = level as ServerLevel
            if (tickCount % 20 == 0) level.sendParticles(ParticleTypes.SMOKE, x, y+0.6, z, 1, 0.0, 0.0, 0.0, 0.0)
            player?.let { setRotation(Mth.rotLerp(0.3f, yRot, player.yRot)) }
            damageNearby(level, player)
            mowBlocks(level, player)
            if (isAutoMowing) {
                if (onGround()) deltaMovement = deltaMovement.add(lookAngle.scale(0.075))
                autoMowTime--
            }
        }
        handle?.followMower(this)
    }

    private fun pullToward(player: Player) {
        val handleCenter = handle?.boundingBox?.center ?: return
        val target = player.eyePosition.add(player.lookAngle.scale(grabDistance).add(0.0, -0.68, 0.0))
        var velocity = target.subtract(handleCenter).scale(0.5)
        if (velocity.lengthSqr() > 0.8 * 0.8) velocity = velocity.normalize().scale(0.8)
        deltaMovement = Vec3(velocity.x, velocity.y.coerceIn(-0.5, 0.5), velocity.z)
        needsSync = true
    }

    private fun setRotation(yaw: Float) {
        yRot = yaw
        yBodyRot = yaw
        yHeadRot = yaw
    }

    private fun autoMowWhenPowered() {
        val level = level() as? ServerLevel?: return
        val here = BlockPos.containing(this.position())
        if (level.getBestOwnOrNeighbourSignal(here) != 0) autoMowTime = AUTO_MOW_TIME
    }

    private fun mowBlocks(level: ServerLevel, player: Player?) {
        val blockstate = level.getBlockState(blockPosition())
        if (blockstate.`is`(PazTags.BlockTags.LAWN_MOWABLE)) level.destroyBlock(blockPosition(), true, player)
    }

    private fun damageNearby(level: ServerLevel, player: Player?) {
        val source = damageSources().source(PazDamageTypes.MOWED, player)
        val targets = level.getEntitiesOfClass(LivingEntity::class.java, boundingBox.inflate(0.1)) {
            it !== this && it !== player && it !is LawnMower && it.isAlive && !it.isAlliedTo(player)
        }
        for (target in targets) target.hurtServer(level, source, mowDamage)
    }

    private fun ensureHandle() {
        val current = handle
        if (current != null && !current.isRemoved) return
        val created = LawnMowerHandle(PazEntities.LAWN_MOWER_HANDLE, level())
        created.setParent(this)
        created.followMower(this)
        level().addFreshEntity(created)
        handle = created
    }

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        if (isRemoved) return true
        if (isInvulnerableToBase(source)) return false

        hurtTime = 10
        hurtDuration = 10
        level.broadcastEntityEvent(this, 2.toByte())
        markHurt()
        damageTaken += damage * 10f
        gameEvent(GameEvent.ENTITY_DAMAGE, source.entity)

        val creative = (source.entity as? Player)?.abilities?.instabuild == true
        if (creative || damageTaken > MAX_HEALTH) {
            destroy(level, drop = !creative)
            if (source.`is`(DamageTypes.DROWN)) scaledExplosion(scale = 0.5f, playSound = true)
        }
        return true
    }

    private fun destroy(level: ServerLevel, drop: Boolean) {
        release()
        discard()
        if (drop && level.gameRules.get(GameRules.ENTITY_DROPS)) {
            val item = PazItems.BRAINZ_ALLOY.defaultInstance.copyWithCount(PazConfig.getAlloyCost(this.type))
            spawnAtLocation(level, item)
        }
    }

    override fun isSensitiveToWater(): Boolean = true

    override fun remove(reason: RemovalReason) {
        handle?.discard()
        handle = null
        super.remove(reason)
    }

    override fun getMainArm(): HumanoidArm = HumanoidArm.RIGHT
}
