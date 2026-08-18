package joshxviii.plantz.entity.zombie

import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.phys.Vec3
import java.util.*

class PirateCaptainGhost(type: EntityType<out PirateCaptainGhost>, level: Level) : PazZombie(type, level) {

    companion object {
        val IS_CHARGING_ID: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(PirateCaptainGhost::class.java, EntityDataSerializers.BOOLEAN)
    }

    init {
        xpReward = 30
        moveControl = GhostMoveControl(this)
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, GhostChargeAttackGoal(this))
        goalSelector.addGoal(2, GhostRandomMoveGoal(this))
    }

    override fun getAmbientSound(): SoundEvent {
        return SoundEvents.EMPTY
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return SoundEvents.EMPTY
    }
    override fun getDeathSound(): SoundEvent {
        return SoundEvents.EMPTY
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.EMPTY
    }

    var isCharging: Boolean
        get() = this.entityData.get(IS_CHARGING_ID)
        set(value) = this.entityData.set(IS_CHARGING_ID, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(IS_CHARGING_ID, false)
    }

    override fun handleAttributes(difficultyModifier: Float, spawnReason: EntitySpawnReason) {}

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }
    override fun isAffectedByBlocks(): Boolean = !isRemoved
    override fun canPickUpLoot(): Boolean = false
    override fun isLeftHanded(): Boolean = false
    override fun hasLineOfSight(target: Entity): Boolean = true

    override fun tick() {
        noPhysics = true
        super.tick()
        noPhysics = false
        isNoGravity = true
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))

        setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_SWORD.defaultInstance)
        setDropChance(EquipmentSlot.MAINHAND, 0.0f)

        return data
    }

    private class GhostMoveControl(val ghost: PirateCaptainGhost) : MoveControl(ghost) {
        override fun tick() {
            if (operation != Operation.MOVE_TO) return
            val delta = Vec3(
                wantedX - ghost.x,
                wantedY - ghost.y,
                wantedZ - ghost.z
            )
            val deltaLength = delta.length()
            if (deltaLength < ghost.boundingBox.getSize()) {
                operation = Operation.WAIT
                ghost.deltaMovement = ghost.deltaMovement.scale(0.5)
            } else {
                ghost.deltaMovement = ghost.deltaMovement.add(delta.scale(speedModifier * 0.05 / deltaLength))
                if (ghost.target == null) {
                    val movement = ghost.deltaMovement
                    ghost.setYRot(-(Mth.atan2(movement.x, movement.z).toFloat()) * 57.295776f)
                    ghost.yBodyRot = ghost.yRot
                } else {
                    val tx = ghost.target!!.x - ghost.x
                    val tz = ghost.target!!.z - ghost.z
                    ghost.setYRot(-(Mth.atan2(tx, tz).toFloat()) * 57.295776f)
                    ghost.yBodyRot = ghost.yRot
                }
            }
        }
    }

    private class GhostRandomMoveGoal(val ghost: PirateCaptainGhost) : Goal() {

        init {
            setFlags(EnumSet.of(Flag.MOVE))
        }

        override fun canUse(): Boolean {
            return !ghost.getMoveControl().hasWanted() && ghost.random.nextInt(reducedTickDelay(7)) == 0
        }

        override fun canContinueToUse(): Boolean {
            return false
        }

        override fun tick() {
            for (attempts in 0..2) {
                val testPos: BlockPos = ghost.blockPosition().offset(
                    ghost.random.nextInt(15) - 7,
                    ghost.random.nextInt(11) - 4,
                    ghost.random.nextInt(15) - 7
                )
                if (!ghost.level().isEmptyBlock(testPos)) continue
                ghost.moveControl.setWantedPosition(testPos.x.toDouble() + 0.5, testPos.y.toDouble() + 0.5, testPos.z.toDouble() + 0.5, 0.25)
                if (ghost.target != null) break
                ghost.getLookControl().setLookAt(testPos.x.toDouble() + 0.5, testPos.y.toDouble() + 0.5, testPos.z.toDouble() + 0.5, 180.0f, 20.0f)
                break
            }
        }
    }

    private class GhostChargeAttackGoal(val ghost: PirateCaptainGhost) : Goal() {

        init {
            setFlags(EnumSet.of(Flag.MOVE))
        }

        override fun canUse(): Boolean {
            val target = ghost.target
            return if (target != null && target.isAlive && !ghost.getMoveControl().hasWanted() && ghost.random.nextInt(reducedTickDelay(7)) == 0)
                ghost.distanceToSqr(target) > 4.0
            else
                false
        }

        override fun canContinueToUse(): Boolean {
            return ghost.getMoveControl()
                .hasWanted() && ghost.isCharging && ghost.target?.isAlive == true
        }

        override fun start() {
            val attackTarget = ghost.target
            if (attackTarget != null) {
                val eyePosition = attackTarget.eyePosition
                ghost.moveControl.setWantedPosition(eyePosition.x, eyePosition.y, eyePosition.z, 1.0)
            }
            ghost.isCharging = true
            ghost.playSound(SoundEvents.VEX_CHARGE, 1.0f, 0.5f)
        }

        override fun stop() {
            ghost.isCharging = false
        }

        override fun requiresUpdateEveryTick(): Boolean {
            return true
        }

        override fun tick() {
            val attackTarget = ghost.target ?: return
            if (ghost.boundingBox.intersects(attackTarget.boundingBox)) {
                ghost.doHurtTarget(getServerLevel(ghost.level()), attackTarget)
                ghost.isCharging = false
            } else {
                val distance = ghost.distanceToSqr(attackTarget)
                if (distance < 9.0) {
                    val eyePosition = attackTarget.eyePosition
                    ghost.moveControl.setWantedPosition(eyePosition.x, eyePosition.y, eyePosition.z, 1.0)
                }
            }
        }
    }
}