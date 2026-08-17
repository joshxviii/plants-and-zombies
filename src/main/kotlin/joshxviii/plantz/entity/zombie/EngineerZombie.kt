package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.ParticleTypes
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
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.Vec3
import kotlin.math.max
import kotlin.math.min


class EngineerZombie(type: EntityType<out EngineerZombie>, level: Level) : PazZombie(type, level) {

    companion object {
        val BUILD_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(EngineerZombie::class.java, EntityDataSerializers.INT)
        const val BUILD_TIME = 60
    }

    val buildAnimation : AnimationState = AnimationState()
    var buildingTime: Int
        get() = this.entityData.get(BUILD_TIME_ID)
        set(value) = this.entityData.set(BUILD_TIME_ID, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(BUILD_TIME_ID, 0)
    }

    override fun getMoveControl(): MoveControl = if (buildingTime>0) noMoveControl else super.getMoveControl()
    override fun getLookControl(): LookControl = if (buildingTime>0) noLookControl else super.getLookControl()
    override fun isWithinMeleeAttackRange(target: LivingEntity): Boolean = if (buildingTime>0) false else super.isWithinMeleeAttackRange(target)

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(1, AvoidEntityGoal(this, LivingEntity::class.java, 32f, 1.0, 1.2) { target -> target is Player || target is Plant })
        goalSelector.addGoal(2, BuildBotGoal(this))
    }

    override fun addBehaviourGoals() {
        addBehaviourGoalsNoMelee()
    }

    override fun tick() {
        super.tick()
        if (buildingTime>0) {
            val buildPos = calculateUpVector(90f, this.yRot).scale(1.0).add(position())
            if (buildingTime<BUILD_TIME*.5) (level() as? ServerLevel)?.sendParticles(
                ParticleTypes.CAMPFIRE_COSY_SMOKE, buildPos.x, buildPos.y, buildPos.z,
                1, 0.2, 0.2, 0.2, 0.01
            )
            buildAnimation.startIfStopped(tickCount)
            if (buildingTime++>BUILD_TIME) {
                buildAnimation.stop()
                buildingTime=0
            }
        }
    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.DIGGER_ZOMBIE_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.DIGGER_ZOMBIE_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.DIGGER_ZOMBIE_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
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
        setItemSlot(EquipmentSlot.OFFHAND, PazItems.BLUEPRINT.defaultInstance)
        setDropChance(EquipmentSlot.OFFHAND, 0.0f)

        return data
    }

    private class BuildBotGoal(
        val engineerZombie: EngineerZombie,
    ) : Goal() {
        companion object {
            const val DEFAULT_AMOUNT = 1
            const val BUILD_DISTANCE = 1
            const val BUILD_DELAY_TIME = 75
            val botTargeting: TargetingConditions = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight().ignoreInvisibilityTesting()
        }
        var buildTime = engineerZombie.random.nextInt(20,60)

        override fun canUse(): Boolean {
            if (engineerZombie.buildingTime>0) return true
            val nearbyBots: Int = getServerLevel(engineerZombie.level()).getNearbyEntities(ZombieRobot::class.java, botTargeting, engineerZombie, engineerZombie.boundingBox.inflate(16.0)).size
            return engineerZombie.target != null && !engineerZombie.isDeadOrDying && (engineerZombie.target?.isAlive == true) && nearbyBots < 2
        }

        override fun tick() {
            super.tick()
            if (--buildTime == 0) engineerZombie.buildingTime=1
            if (buildTime<-28) tryBuildBot()
        }

        private fun tryBuildBot() {
            buildTime = BUILD_DELAY_TIME + engineerZombie.random.nextInt(40)
            val target = engineerZombie.target?: return
            val angleToTarget = (engineerZombie.yRot + 90) * Mth.DEG_TO_RAD
            val minY = min(target.y, engineerZombie.y) - 2.0
            val maxY = max(target.y, engineerZombie.y) + 2.0
            val amount = DEFAULT_AMOUNT
            val a = (2*Mth.PI / amount)
            for(i in 1..amount) {
                val b = a*i-a*(amount+1)*.5
                val x = engineerZombie.x + Mth.cos(b+angleToTarget)*BUILD_DISTANCE
                val z = engineerZombie.z + Mth.sin(b+angleToTarget)*BUILD_DISTANCE
                tryCreateBot(x, z, minY, maxY, angleToTarget)
            }
        }

        private fun tryCreateBot(x: Double, z: Double, minY: Double, maxY: Double, angle: Float) {
            val level = engineerZombie.level() as ServerLevel
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
                val bot : ZombieRobot = PazEntities.ZOMBIE_TURRET.create(level, EntitySpawnReason.MOB_SUMMONED)?: return
                bot.snapTo(BlockPos(Vec3i(x.toInt(),(pos.y+topOffset).toInt(),z.toInt())), angle * Mth.RAD_TO_DEG, 0.0f)
                bot.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.REINFORCEMENT, null)
                bot.owner = engineerZombie
                level.addFreshEntity(bot)
                level.gameEvent(GameEvent.ENTITY_PLACE, Vec3(x, pos.y+topOffset, z), GameEvent.Context.of(engineerZombie))
            }
        }
    }
}