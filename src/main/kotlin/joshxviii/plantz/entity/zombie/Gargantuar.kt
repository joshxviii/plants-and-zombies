package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.canReachTarget
import joshxviii.plantz.entity.projectile.PowderSnowChunk
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FallingBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.apache.logging.log4j.core.jmx.Server

class Gargantuar(type: EntityType<out Gargantuar>, level: Level) : PazZombie(type, level) {

    init {
        xpReward = 200
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = {
                PowderSnowChunk(level(), this)
            },
            velocity = 1.0,
            inaccuracy = 1.2f,
            actionEndEffect = {
                this.swing(this.usedItemHand)
            },
            actionPredicate = { it.random.nextDouble() < 0.25
                                && it.target!=null
                                && it.distanceTo(it.target!!) > 6.5f
                                && !this.navigation.path.canReachTarget(target!!.blockPosition())
                              },
            actionDelay = 35))
    }

    private val noMoveControl = object : MoveControl(this) { override fun getSpeedModifier(): Double = 0.0 }

    private val noLookControl = object : LookControl(this) {}

    override fun getLookControl(): LookControl =  if (tickCount < 60) noLookControl else super.getLookControl()
    override fun getMoveControl(): MoveControl = if (tickCount < 60) noMoveControl else super.getMoveControl()

    override fun tick() {
        super.tick()
        val level = level()
        if (tickCount < 26) {
            if (level is ServerLevel) level.sendParticles(
                BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockPosition().below())),
                x, y + 0.05, z, 10, 0.6, 0.0, 0.6, 0.4
            )
        }
        if (tickCount in 43..<55) {
            val direction = calculateViewVector(0f, this.yRot-35).scale(2.5)
            val pos = Vec3(
                direction.x + x,
                direction.y + y,
                direction.z + z
            )
            if (level is ServerLevel) level.sendParticles(
//                ParticleTypes.CLOUD,
                BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(BlockPos.containing(pos).below())),
                pos.x, pos.y, pos.z,
                5, 0.3, 0.0, 0.3, 0.0
            )
        }
    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.ZOMBIE_YETI_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.ZOMBIE_YETI_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.ZOMBIE_YETI_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    override fun isBaby(): Boolean = false
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {}
    override fun canPickUpLoot(): Boolean = false
    override fun isSunSensitive(): Boolean = false
    override fun convertsInWater(): Boolean = false
    override fun randomizeReinforcementsChance() {}
    override fun getPassengerRidingPosition(passenger: Entity): Vec3 {
        return super.getPassengerRidingPosition(passenger)
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
        return data
    }
}