package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazSounds
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3
import kotlin.math.max
import kotlin.math.min

class DiscoZombie(type: EntityType<out DiscoZombie>, level: Level) : PazZombie(type, level) {

    companion object {
        val SUMMON_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(DiscoZombie::class.java, EntityDataSerializers.INT)
    }

    init {
        xpReward = 12
    }
    
    val summonAnimation : AnimationState = AnimationState()
    var summoningTime: Int
        get() = this.entityData.get(SUMMON_TIME_ID)
        set(value) = this.entityData.set(SUMMON_TIME_ID, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(SUMMON_TIME_ID, 0)
    }

    private val noMoveControl = object : MoveControl(this) {
        override fun getSpeedModifier(): Double = 0.0
    }

    override fun tick() {
        super.tick()
        if(summoningTime>0) {
            summonAnimation.startIfStopped(tickCount)
            summoningTime++
        }
        if (summoningTime>40) {
            summonAnimation.stop()
            summoningTime=0
        }
    }

    override fun getMoveControl(): MoveControl= if (summoningTime>0) noMoveControl else super.getMoveControl()

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(1,SummonBackupGoal(this))
    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.DISCO_ZOMBIE_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.DISCO_ZOMBIE_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.DISCO_ZOMBIE_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun isBaby(): Boolean = false
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {}
    override fun canPickUpLoot(): Boolean = false
    override fun isSunSensitive(): Boolean = false
    override fun convertsInWater(): Boolean = false
    override fun randomizeReinforcementsChance() {}

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))
        if (spawnReason != EntitySpawnReason.CONVERSION) setCanBreakDoors(true)
        return data
    }

    private class SummonBackupGoal(
        val summoner: DiscoZombie,
    ) : Goal() {
        companion object {
            const val DEFAULT_AMOUNT = 2
            const val SUMMON_DELAY_TIME = 70
            val backupTargeting: TargetingConditions = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight().ignoreInvisibilityTesting()
        }
        var summonTime = summoner.random.nextInt(20,60)

        override fun canUse(): Boolean {
            if (summoner.summoningTime>0) return true
            val backupDancers: Int = getServerLevel(summoner.level()).getNearbyEntities<BackupDancer>(BackupDancer::class.java, backupTargeting, summoner, summoner.boundingBox.inflate(16.0)).size
            return summoner.isAggressive && !summoner.isDeadOrDying && backupDancers < 4
        }

        override fun tick() {
            super.tick()
            if (--summonTime == 0) summoner.summoningTime=1
            if (summonTime<-8) trySummonBackup()
        }

        private fun getSummonAmount(): Int {
            var finalAmount = DEFAULT_AMOUNT
            val level = summoner.level() as ServerLevelAccessor
            val multi = level.getCurrentDifficultyAt(summoner.blockPosition()).specialMultiplier
            if(multi > 0.0) repeat(3) {
                if(summoner.random.nextFloat() < 0.25 * multi) finalAmount++
            }
            return finalAmount
        }

        private fun trySummonBackup() {
            summonTime = SUMMON_DELAY_TIME + summoner.random.nextInt(40)
            val target = summoner.target?: return
            val angleToTarget = (summoner.yRot * Math.PI.toFloat()) / 180.0f
            val minY = min(target.y, summoner.y)
            val maxY = max(target.y, summoner.y) + 1.0
            val amount = getSummonAmount()
            val a = (2*Math.PI / amount)// spread angle evenly based on summons amount
            for(i in 1..amount) {
                // get x/z coords based on angle
                val b = a*i-a*(amount+1)*.5
                val x = summoner.x + Mth.cos(b+angleToTarget)*2
                val z = summoner.z + Mth.sin(b+angleToTarget)*2
                tryCreateBackupDancer(x, z, minY, maxY, angleToTarget)
            }
        }

        private fun tryCreateBackupDancer(x: Double, z: Double, maxY: Double, minY: Double, angle: Float) {
            val level = summoner.level() as ServerLevel
            var pos = BlockPos.containing(x, maxY, z)
            var success = false
            var topOffset = 0.0
            do {
                val below = pos.below()
                if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                    if (!level.isEmptyBlock(pos)) {
                        val blockState: BlockState = level.getBlockState(pos)
                        val shape = blockState.getCollisionShape(level, pos)
                        if (!shape.isEmpty) topOffset = shape.max(Direction.Axis.Y)
                    }
                    success = true; break
                }
                pos = pos.below()
            } while (pos.y >= Mth.floor(minY) - 1)

            if (success) {
                val backup = BackupDancer(level = level, position = Vec3(x,pos.y+topOffset,z), rotation = angle * (180.0f / Math.PI.toFloat()))
                if (level.addFreshEntity(backup)) {// apply hypnotize-effect if present
                    val effect = summoner.activeEffects.find { it.effect.`is`(PazEffects.HYPNOTIZE.unwrapKey().get()) }
                    if (effect!=null) backup.addEffect(effect)
                }
                level.gameEvent(GameEvent.ENTITY_PLACE, Vec3(x, pos.y+topOffset, z), GameEvent.Context.of(summoner))
            }
        }
    }

}