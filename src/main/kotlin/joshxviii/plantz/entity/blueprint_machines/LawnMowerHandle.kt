package joshxviii.plantz.entity.blueprint_machines

import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

class LawnMowerHandle(type: EntityType<out LawnMowerHandle>, level: Level) : Entity(type, level) {
    companion object {
        private val DATA_PARENT_ID: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(LawnMowerHandle::class.java, EntityDataSerializers.INT)

        const val BACK_OFFSET = 0.85
        const val HEIGHT_OFFSET = 0.5
    }

    init {
        noPhysics = true
    }

    private var parentRef: LawnMower? = null

    val parent: LawnMower?
        get() {
            parentRef?.let { if (!it.isRemoved) return it }
            val found = level().getEntity(entityData.get(DATA_PARENT_ID)) as? LawnMower
            parentRef = found
            return found
        }

    fun setParent(mower: LawnMower) {
        parentRef = mower
        entityData.set(DATA_PARENT_ID, mower.id)
    }

    fun followMower(mower: LawnMower) {
        val yawRad = Math.toRadians(mower.yRot.toDouble())
        val forwardX = -sin(yawRad)
        val forwardZ = cos(yawRad)
        setPos(
            mower.x - forwardX * BACK_OFFSET,
            mower.y + HEIGHT_OFFSET,
            mower.z - forwardZ * BACK_OFFSET
        )
    }

    override fun tick() {
        super.tick()
        if (!level().isClientSide) {
            val mower = parentRef
            if (mower == null || mower.isRemoved) discard()
        }
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        entityData.define(DATA_PARENT_ID, -1)
    }

    override fun interact(player: Player, hand: InteractionHand, location: Vec3): InteractionResult {
        return parent?.startGrab(player) ?: InteractionResult.PASS
    }

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        if (isInvulnerableToBase(source)) return false
        return parentRef?.hurtServer(level, source, damage) ?: false
    }

    override fun getPickResult(): ItemStack? = parent?.pickResult

    override fun `is`(other: Entity): Boolean = this === other || parent === other

    override fun isPickable(): Boolean = true
    override fun isPushable(): Boolean = false
    override fun canBeCollidedWith(other: Entity?): Boolean = false
    override fun isNoGravity(): Boolean = true
    override fun shouldBeSaved(): Boolean = false

    override fun readAdditionalSaveData(input: ValueInput) {}
    override fun addAdditionalSaveData(output: ValueOutput) {}
}
