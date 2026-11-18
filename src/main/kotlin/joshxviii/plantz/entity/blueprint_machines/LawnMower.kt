package joshxviii.plantz.entity.blueprint_machines

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.item.BlueprintItem
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.phys.Vec3

class LawnMower(type: EntityType<out LawnMower>, level: Level) : LivingEntity(type, level) {
    companion object {
        private val DATA_GRABBER_ID: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(LawnMower::class.java, EntityDataSerializers.INT)

        const val MAX_DAMAGE = 40f
        const val COLLISION_DAMAGE = 4f
        const val MAX_GRAB_RANGE_SQR = 8.0 * 8.0
        const val MIN_GRAB_DISTANCE = 0.9
        const val MAX_GRAB_DISTANCE = 1.1

        @JvmField var clientGrabHook: ((LawnMower) -> Unit)? = null
    }

    val actionAnimation = AnimationState()

    private var handle: LawnMowerHandle? = null
    private var damageTaken = 0f
    private var grabDistance = 2.0

    var grabberId: Int
        get() = entityData.get(DATA_GRABBER_ID)
        private set(value) = entityData.set(DATA_GRABBER_ID, value)

    val isGrabbed: Boolean get() = grabberId != -1

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(DATA_GRABBER_ID, -1)
    }

    override fun onSyncedDataUpdated(accessor: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(accessor)
        if (accessor == DATA_GRABBER_ID && level().isClientSide) {
            if (isGrabbed) actionAnimation.startIfStopped(tickCount) else actionAnimation.stop()
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
        grabberId = -1
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

        if (player != null && isGrabbed) {
            turnToward(player)
            damageNearby(level as ServerLevel, player)
        }
        handle?.followMower(this)
    }

    private fun pullToward(player: Player) {
        val handleCenter = handle?.boundingBox?.center ?: return
        val target = player.eyePosition.add(player.lookAngle.scale(grabDistance).multiply(1.0, 0.75, 1.0).add(0.0, -0.7, 0.0))
        var velocity = target.subtract(handleCenter).scale(0.5)
        if (velocity.lengthSqr() > 0.8 * 0.8) velocity = velocity.normalize().scale(0.8)
        deltaMovement = Vec3(velocity.x, velocity.y.coerceIn(-0.5, 0.5), velocity.z)
        needsSync = true
    }

    private fun turnToward(player: Player) {
        val yaw = Mth.rotLerp(0.3f, yRot, player.yRot)
        yRot = yaw
        yBodyRot = yaw
        yHeadRot = yaw
    }

    private fun damageNearby(level: ServerLevel, player: Player) {
        val source = damageSources().playerAttack(player)
        val targets = level.getEntitiesOfClass(LivingEntity::class.java, boundingBox.inflate(0.1)) {
            it !== this && it !== player && it !is LawnMower && it.isAlive && !it.isAlliedTo(player)
        }
        for (target in targets) target.hurtServer(level, source, COLLISION_DAMAGE)
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
        if (creative || damageTaken > MAX_DAMAGE) destroy(level, drop = !creative)
        return true
    }

    private fun destroy(level: ServerLevel, drop: Boolean) {
        release()
        discard()
        if (drop && level.gameRules.get(GameRules.ENTITY_DROPS)) {
            spawnAtLocation(level, PazItems.BRAINZ_ALLOY.defaultInstance)
        }
    }

    override fun remove(reason: RemovalReason) {
        handle?.discard()
        handle = null
        super.remove(reason)
    }

    override fun getMainArm(): HumanoidArm = HumanoidArm.RIGHT
}
