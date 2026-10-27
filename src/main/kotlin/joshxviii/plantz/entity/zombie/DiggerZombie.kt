package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import joshxviii.plantz.raid.ZombieRaid
import joshxviii.plantz.raid.getZombieRaids
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.ItemTags
import net.minecraft.tags.StructureTags
import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput


class DiggerZombie(type: EntityType<out DiggerZombie>, level: Level) : PazZombie(type, level) {

    companion object {
        fun checkMinerSpawnRules(
            type: EntityType<out Mob>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val below = pos.below()
            val isMineshaftSpawn = (level as ServerLevel).structureManager().getStructureWithPieceAt(pos, StructureTags.MINESHAFT).isValid

            return level.difficulty != Difficulty.PEACEFUL
                    && ((EntitySpawnReason.ignoresLightRequirements(spawnReason) || isDarkEnoughToSpawn(level, pos, random))
                    && checkMobSpawnRules(type, level, spawnReason, pos, random)
                    && pos.y < 10) || isMineshaftSpawn
        }
        val TUNNEL_COOLDOWN_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(DiggerZombie::class.java, EntityDataSerializers.INT)
        val MINE_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(DiggerZombie::class.java, EntityDataSerializers.INT)
        val TUNNEL_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(DiggerZombie::class.java, EntityDataSerializers.INT)
        const val TUNNELING_DURATION: Int = 82
        const val TUNNELING_COOLDOWN = 300
    }

    val mineBlockAnimation : AnimationState = AnimationState()
    val tunnelAnimation : AnimationState = AnimationState()
    private var tunnelExitPos: BlockPos? = null

    var mineBlockTime: Int
        get() = this.entityData.get(MINE_TIME_ID)
        set(value) = this.entityData.set(MINE_TIME_ID, value)

    var tunnelTime: Int
        get() = this.entityData.get(TUNNEL_TIME_ID)
        set(value) = this.entityData.set(TUNNEL_TIME_ID, value)

    var tunnelCooldown: Int
        get() = this.entityData.get(TUNNEL_COOLDOWN_ID)
        set(value) = this.entityData.set(TUNNEL_COOLDOWN_ID, value)

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putInt("tunnelCooldown", tunnelCooldown)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        tunnelCooldown = input.getIntOr("tunnelCooldown", TUNNELING_COOLDOWN)
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(1, TunnelNearTargetGoal(this))
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

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(MINE_TIME_ID, 0)
        entityData.define(TUNNEL_TIME_ID, 0)
        entityData.define(TUNNEL_COOLDOWN_ID, 0)
    }

    override fun isImmobile(): Boolean = super.isImmobile() || tunnelTime>0

    override fun tick() {
        super.tick()
        if (mineBlockTime>0 || swinging) {
            mineBlockAnimation.startIfStopped(tickCount)
            if (mineBlockTime++>20) {
                mineBlockAnimation.stop()
                mineBlockTime=0
            }
        }
        if (tunnelCooldown>0) tunnelCooldown--
        if (tunnelTime>0) {
            val serverLevel = level() as? ServerLevel
            tunnelAnimation.startIfStopped(tickCount)
            if(tickCount%6==0) playSound(SoundEvents.ROOTED_DIRT_HIT, 0.7f, 0.2f)
            if(tickCount%3==0) serverLevel?.sendParticles(
                BlockParticleOption(ParticleTypes.BLOCK, serverLevel.getBlockState(blockPosition().below())),
                x, y + 0.05, z, 8, 0.25, 0.0, 0.25, 0.4
            )
            if (tunnelTime == TUNNELING_DURATION / 2) {
                tunnelAnimation.start(tickCount)
                tunnelExitPos?.let { dest ->
                    teleportTo(dest.x + 0.5, dest.y.toDouble(), dest.z + 0.5)
                    tunnelCooldown = TUNNELING_COOLDOWN + random.nextInt(20, 100)
                }
            }
            if (tunnelTime++>=TUNNELING_DURATION) {
                tunnelAnimation.stop()
                tunnelExitPos = null
                tunnelTime=0
            }
        } else {
            tunnelAnimation.stop()
        }
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun isLeftHanded(): Boolean = false
    override fun getPreferredWeaponType(): TagKey<Item> = PazTags.ItemTags.DIGGER_PREFERRED_WEAPONS
    override fun wantsToPickUp(level: ServerLevel, itemStack: ItemStack): Boolean {
        if(itemStack.`is`(ItemTags.ARMOR_ENCHANTABLE)) return false
        return super.wantsToPickUp(level, itemStack)
    }


    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)

        tunnelCooldown = TUNNELING_COOLDOWN + random.nextInt(20, 100)

        setCanBreakDoors(true)
        setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_PICKAXE.defaultInstance)
        setDropChance(EquipmentSlot.MAINHAND, 0.0f)

        return data
    }

    private class TunnelNearTargetGoal(
        val digger: DiggerZombie,
    ) : Goal() {

        companion object {
            const val MIN_DIST = 16.0
            const val MAX_DIST = 40.0
        }

        private var exitPos: BlockPos? = null

        override fun canUse(): Boolean {
            if (digger.tunnelTime > 0) return false
            if (digger.tunnelCooldown > 0) return false
            if (digger.isInWater) return false

            val targetPos = digger.target?.takeIf { it.isAlive }?.blockPosition()
                ?: (digger.level() as? ServerLevel)
                    ?.getZombieRaids()
                    ?.getNearbyRaid(digger.blockPosition(), ZombieRaid.RAID_DISTANCE)
                    ?.center
                ?: return false

            val dist = targetPos.distSqr(digger.blockPosition())
            if (dist <= MIN_DIST * MIN_DIST || dist >= MAX_DIST * MAX_DIST) return false

            exitPos = findExitNearTarget(targetPos)?: return false
            return true
        }

        override fun canContinueToUse(): Boolean {
            return digger.tunnelTime > 0
        }

        override fun start() {
            digger.tunnelExitPos = exitPos
            digger.tunnelTime = 1
            digger.navigation.stop()
        }

        override fun requiresUpdateEveryTick(): Boolean = true

        private fun findExitNearTarget(targetPos: BlockPos): BlockPos? {
            val level = digger.level()
            val from = digger.blockPosition()
            val random = digger.random

            for (attempt in 0 until 10) {
                val t = 0.78 + random.nextDouble() * 0.15
                val baseX = from.x + (targetPos.x - from.x) * t
                val baseZ = from.z + (targetPos.z - from.z) * t
                val baseY = from.y + (targetPos.y - from.y) * t

                val ox = random.nextInt(5) - 2
                val oz = random.nextInt(5) - 2
                val oy = random.nextInt(3) - 1

                level.addParticle(
                    ParticleTypes.SMOKE, baseX + 0.5, baseY + 0.5, baseZ + 0.5, 0.0, 0.0, 0.0
                )

                val feet = BlockPos.containing(baseX + ox, baseY + oy, baseZ + oz)
                if (isValidExit(level, feet)) return feet

                val lower = feet.below()
                if (isValidExit(level, lower)) return lower
            }
            return null
        }

        private fun isValidExit(level: Level, feet: BlockPos): Boolean {
            val below = level.getBlockState(feet.below())
            val at = level.getBlockState(feet)
            val above = level.getBlockState(feet.above())

            if (!below.isFaceSturdy(level, feet.below(), Direction.UP)) return false
            if (!at.getCollisionShape(level, feet).isEmpty) return false
            if (!above.getCollisionShape(level, feet.above()).isEmpty) return false

            if (!level.isLoaded(feet)) return false
            return true
        }
    }
}