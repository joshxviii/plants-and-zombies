package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.GravestoneBlock.Companion.FACING
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluids
import kotlin.math.max
import kotlin.math.min


class GraveDigger(type: EntityType<out GraveDigger>, level: Level) : PazZombie(type, level) {

    companion object {
        val DIG_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(GraveDigger::class.java, EntityDataSerializers.INT)
        const val DIG_TIME = 60
    }

    val digAnimation : AnimationState = AnimationState()
    var digTime: Int
        get() = this.entityData.get(DIG_TIME_ID)
        set(value) = this.entityData.set(DIG_TIME_ID, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(DIG_TIME_ID, 0)
    }

    override fun getMoveControl(): MoveControl = if (digTime>0) noMoveControl else super.getMoveControl()
    override fun getLookControl(): LookControl = if (digTime>0) noLookControl else super.getLookControl()
    override fun isWithinMeleeAttackRange(target: LivingEntity): Boolean = if (digTime>0) false else super.isWithinMeleeAttackRange(target)

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(2, DigGraveGoal(this))
    }

    override fun addBehaviourGoals() {
        super.addBehaviourGoals()
        //addBehaviourGoalsNoMelee()
    }

    override fun tick() {
        super.tick()
        if (digTime>0) {
            val buildPos = calculateUpVector(90f, this.yRot).scale(1.0).add(position())
            if (digTime<DIG_TIME*.5) (level() as? ServerLevel)?.sendParticles(
                ParticleTypes.CAMPFIRE_COSY_SMOKE, buildPos.x, buildPos.y, buildPos.z,
                1, 0.2, 0.2, 0.2, 0.01
            )
            digAnimation.startIfStopped(tickCount)
            if (digTime++>DIG_TIME) {
                digAnimation.stop()
                digTime=0
            }
        } else if(digAnimation.isStarted) digAnimation.stop()
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))

        setCanBreakDoors(true)
        setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_SHOVEL.defaultInstance)
        setDropChance(EquipmentSlot.MAINHAND, 0.0f)

        return data
    }

    private class DigGraveGoal(
        val gravedigger: GraveDigger,
    ) : Goal() {
        companion object {
            const val DEFAULT_AMOUNT = 1
            const val DIG_DISTANCE = 1
            const val DIG_DELAY_TIME = 75
        }
        var buildTime = gravedigger.random.nextInt(20,60)

        override fun canUse(): Boolean {
            if (gravedigger.digTime>0) return true
            val nearbyGraves: Int = 0
            return gravedigger.target != null && !gravedigger.isDeadOrDying && (gravedigger.target?.isAlive == true) && nearbyGraves < 2
        }

        override fun tick() {
            super.tick()
            if (--buildTime == 0) gravedigger.digTime=1
            if (buildTime<-28) tryBuildBot()
        }

        override fun stop() {
            super.stop()
        }

        private fun getGraveAmount(): Int {
            var amount = DEFAULT_AMOUNT
            val level = gravedigger.level() as ServerLevelAccessor
            val multi = level.getCurrentDifficultyAt(gravedigger.blockPosition()).specialMultiplier
            if(multi > 0.0) repeat(2) {
                if(gravedigger.random.nextFloat() < 0.2 * multi) amount++
            }
            return amount
        }

        private fun tryBuildBot() {
            buildTime = DIG_DELAY_TIME + gravedigger.random.nextInt(40)
            val target = gravedigger.target?: return
            val angleToTarget = (gravedigger.yRot + 90) * Mth.DEG_TO_RAD
            val minY = min(target.y, gravedigger.y) - 2.0
            val maxY = max(target.y, gravedigger.y) + 2.0
            val amount = getGraveAmount()
            val a = (2*Mth.PI / amount)
            for(i in 1..amount) {
                val b = a*i-a*(amount+1)*.5
                val x = gravedigger.x + Mth.cos(b+angleToTarget)*DIG_DISTANCE
                val z = gravedigger.z + Mth.sin(b+angleToTarget)*DIG_DISTANCE
                tryDigGrave(x, z, minY, maxY, angleToTarget)
            }
        }

        private fun tryDigGrave(x: Double, z: Double, minY: Double, maxY: Double, angle: Float) {
            val level = gravedigger.level() as ServerLevel
            var pos = BlockPos.containing(x, maxY, z)
            var success = false
            var topOffset = 0.0
            do {// search for an empty space from minY to maxY
                val belowState = level.getBlockState(pos.below())
                val blockState = level.getBlockState(pos)
                val fluidState = blockState.fluidState
                if (belowState.isFaceSturdy(level, pos.below(), Direction.UP)) {
                    if (!level.isEmptyBlock(pos)) {
                        val blockState: BlockState = blockState
                        val shape = blockState.getCollisionShape(level, pos)
                        if (!shape.isEmpty) topOffset = shape.max(Direction.Axis.Y)
                    }
                    success = true; break
                }
                if (fluidState.`is`(Fluids.WATER)) {
                    topOffset = fluidState.getHeight(level, pos).toDouble()
                    success = true; break
                }
                pos = pos.below()
            } while (pos.y >= Mth.floor(minY) - 1)

            if (success) {
                val gravePos = BlockPos(pos.x,(pos.y+topOffset).toInt(),pos.z)
                if (level.getBlockState(gravePos).canBeReplaced()) {
                    level.setBlock(gravePos, PazBlocks.GRAVESTONE.defaultBlockState().setValue(FACING, Direction.fromYRot(angle.toDouble() * Mth.RAD_TO_DEG)), 3)
                }
            }
        }
    }
}