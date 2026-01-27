package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.PowderSnowChunk
import net.minecraft.core.BlockPos
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
import org.apache.logging.log4j.core.jmx.Server

class ZombieYeti(type: EntityType<out ZombieYeti>, level: Level) : Zombie(type, level) {

    companion object {
        fun checkZombieYetiSpawnRules(
            type: EntityType<out Mob>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val below = pos.below()
            return level.difficulty != Difficulty.PEACEFUL
                    && (EntitySpawnReason.ignoresLightRequirements(spawnReason) || isDarkEnoughToSpawn(level, pos, random))
                    && checkMobSpawnRules(type, level, spawnReason, pos, random) || level.getBlockState(below).`is`(PazTags.BlockTags.YETI_SPAWNABLE_ON)
                    && pos.y > level.seaLevel
        }
    }

    init {
       xpReward = 15
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory = {
                PowderSnowChunk(level(), this)
//                val block = FallingBlockEntity.fall(this.level(), this.blockPosition().above(), Blocks.POWDER_SNOW.defaultBlockState())
//                block.setPos(this.eyePosition)
//                return@ProjectileAttackGoal block
            },
            velocity = 1.0,
            inaccuracy = 1.2f,
            actionEndEffect = {
                this.swing(this.usedItemHand)
            },
            actionPredicate = { it.random.nextDouble() < 0.2 && it.target!=null && it.distanceTo(it.target!!) > 12f },
            actionDelay = 35))
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
        val random = level.random
        if (spawnReason != EntitySpawnReason.CONVERSION) {

            if (random.nextFloat() < 0.06 && getItemBySlot(EquipmentSlot.HEAD).isEmpty) {
                setItemSlot(EquipmentSlot.HEAD, Items.BUCKET.defaultInstance)
            }

            if (random.nextFloat() < 0.0075) {
                val polarBear = EntityType.POLAR_BEAR.create(level(), EntitySpawnReason.JOCKEY)
                if (polarBear != null) {
                    polarBear.snapTo(x, y, z, yRot, 0.0f)
                    polarBear.finalizeSpawn(level, difficulty, EntitySpawnReason.JOCKEY, null)
                    startRiding(polarBear, false, false)
                    level.addFreshEntity(polarBear)
                }
            }
        }

        return data
    }
}