package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.applyImpulse
import joshxviii.plantz.block.GravestoneBlock.Companion.FACING
import joshxviii.plantz.createFallingBlock
import joshxviii.plantz.toAngle
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.ItemTags
import net.minecraft.util.Mth
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3

class GraveDigger(type: EntityType<out GraveDigger>, level: Level) : PazZombie(type, level) {

    companion object {
        val DIG_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(GraveDigger::class.java, EntityDataSerializers.INT)
        val DUG_GRAVES_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(GraveDigger::class.java, EntityDataSerializers.INT)
        val MAX_GRAVES_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(GraveDigger::class.java, EntityDataSerializers.INT)
        const val DIG_TIME = 80
    }

    val digAnimation : AnimationState = AnimationState()
    var digTime: Int
        get() = this.entityData.get(DIG_TIME_ID)
        set(value) = this.entityData.set(DIG_TIME_ID, value)

    var dugGraves: Int
        get() = this.entityData.get(DUG_GRAVES_ID)
        set(value) = this.entityData.set(DUG_GRAVES_ID, value)

    var maxGraves: Int
        get() = this.entityData.get(MAX_GRAVES_ID)
        set(value) = this.entityData.set(MAX_GRAVES_ID, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(DIG_TIME_ID, 0)
        entityData.define(DUG_GRAVES_ID, 0)
        entityData.define(MAX_GRAVES_ID, 0)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        maxGraves = input.getIntOr("max_graves", this.entityData.get(MAX_GRAVES_ID))
        dugGraves = input.getIntOr("dug_graves", 0)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putInt("max_graves", maxGraves)
        output.putInt("dug_graves", dugGraves)
    }

    override fun getMoveControl(): MoveControl = if (digTime>0) noMoveControl else super.getMoveControl()

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(2, DigGraveGoal(this))
    }

    override fun addBehaviourGoals() {
        super.addBehaviourGoals()
        //addBehaviourGoalsNoMelee()
    }

    var retreatDelay = -1
    override fun tick() {
        super.tick()

        if (dugGraves >= maxGraves) {
            if(retreatDelay++ > 60) state = ZombieState.RETREATING
            return
        }

        if (digTime>0) {
            val buildPos = calculateUpVector(90f, this.yRot).scale(1.0).add(position())
            if (digTime<DIG_TIME*.9 && digTime>=26) (level() as? ServerLevel)?.sendParticles(
                BlockParticleOption(ParticleTypes.BLOCK, level().getBlockState(BlockPos.containing(buildPos).below())), buildPos.x, buildPos.y+0.25, buildPos.z,
                1, 0.2, 0.2, 0.2, 0.01
            )
            if(digTime % 28 == 0 || digTime % 42 == 0) playSound(SoundEvents.ROOTED_DIRT_BREAK, 1.0f, 0.9f)
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

    override fun isLeftHanded(): Boolean = false

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)

        setCanBreakDoors(true)
        setItemSlot(EquipmentSlot.MAINHAND, Items.STONE_SHOVEL.defaultInstance)
        setDropChance(EquipmentSlot.MAINHAND, 0.0f)

        maxGraves = random.nextIntBetweenInclusive(2, 5)

        return data
    }

    private class DigGraveGoal(
        val gravedigger: GraveDigger,
    ) : Goal() {
        companion object {
            const val DIG_DELAY_TIME = 260
        }
        var digTime = gravedigger.random.nextInt(40,100)

        override fun canUse(): Boolean {
            val level = gravedigger.level() as ServerLevel
            level.getNearestPlayer(gravedigger, 14.0) ?: return false
            if (!level.gameRules.get(GameRules.MOB_GRIEFING)) return false
            if (!gravedigger.mainHandItem.`is`(ItemTags.SHOVELS)) return false
            if (gravedigger.dugGraves >= gravedigger.maxGraves) return false
            if (gravedigger.digTime>0) return true
            return gravedigger.target?.let { gravedigger.distanceToSqr(it) < 196 } == true && !gravedigger.isDeadOrDying && (gravedigger.target?.isAlive == true)
        }

        override fun tick() {
            super.tick()
            if (--digTime == 0) gravedigger.digTime=1
            if (digTime<-32) {
                val target = gravedigger.target?.position() ?: gravedigger.lookAngle
                val angleToTarget = (target.subtract(gravedigger.position()).normalize().toAngle().toDouble()) * Mth.DEG_TO_RAD

                val xd = Mth.cos(angleToTarget)
                val zd = Mth.sin(angleToTarget)
                val x = gravedigger.x + xd
                val z = gravedigger.z + zd

                val gravePos = BlockPos.containing(x, gravedigger.y, z)
                tryDigGrave(gravePos, angleToTarget)
            }
        }

        override fun stop() {
            super.stop()
        }

        private fun tryDigGrave(gravePos: BlockPos, angleToTarget: Double) {
            gravedigger.dugGraves++
            digTime = DIG_DELAY_TIME + gravedigger.random.nextInt(160)
            val level = gravedigger.level() as ServerLevel
            val graveStone = createFallingBlock(level, gravePos.center, PazBlocks.GRAVESTONE.defaultBlockState().setValue(FACING, Direction.fromYRot(angleToTarget * Mth.RAD_TO_DEG - 90)))?: return
            graveStone.setHurtsEntities(2.5f, 10)
            graveStone.dropItem = true
            gravedigger.playSound(SoundEvents.MUDDY_MANGROVE_ROOTS_BREAK, 1.4f, 0.9f)
            graveStone.playSound(SoundEvents.TUFF_BRICKS_BREAK, 1.4f, 0.8f)

            val motionDirection = Vec3(Mth.cos(angleToTarget).toDouble(), 2.25, Mth.sin(angleToTarget).toDouble())
            graveStone.applyImpulse(motionDirection, 0.25f, 0.1f)
        }
    }
}