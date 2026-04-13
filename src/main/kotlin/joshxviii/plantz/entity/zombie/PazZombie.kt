package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazDataSerializers.DATA_ZOMBIE_STATE
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

abstract class PazZombie(type: EntityType<out PazZombie>, level: Level) : Zombie(type, level) {

    val emergeAnimation : AnimationState = AnimationState()

    companion object {
        fun checkZombieSpawnRules(
            type: EntityType<out Mob>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            return level.difficulty != Difficulty.PEACEFUL
                    && (EntitySpawnReason.ignoresLightRequirements(spawnReason) || isDarkEnoughToSpawn(level, pos, random))
                    && checkMobSpawnRules(type, level, spawnReason, pos, random)
        }

        val ZOMBIE_STATE: EntityDataAccessor<ZombieState> = SynchedEntityData.defineId<ZombieState>(PazZombie::class.java, DATA_ZOMBIE_STATE)
    }

    var state: ZombieState
        get() = this.entityData.get(ZOMBIE_STATE)
        set(value) { this.entityData.set(ZOMBIE_STATE, value) }

    override fun tick() {
        super.tick()
        when (state) {
            ZombieState.EMERGING -> {
                isImmobile
                emergeAnimation.startIfStopped(tickCount)
                if (tickCount > emergingTime()) {
                    emergeAnimation.stop()
                    state = ZombieState.IDLE
                }
            }
            else -> {}
        }
    }

    override fun isImmobile(): Boolean {
        return if (state==ZombieState.EMERGING) true else super.isImmobile()
    }

    open fun emergingTime(): Int = 40

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(ZOMBIE_STATE, ZombieState.IDLE)
    }

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        return if (source.`is`(PazDamageTypes.ZOMBIE_SMASH)) false else super.hurtServer(level, source, damage)
    }
    override fun hurtClient(source: DamageSource): Boolean {
        return if (source.`is`(PazDamageTypes.ZOMBIE_SMASH)) false else super.hurtClient(source)
    }
    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        if (source.`is`(PazDamageTypes.ZOMBIE_SMASH)) return
        super.actuallyHurt(level, source, damage)
    }

    override fun wantsToPickUp(level: ServerLevel, itemStack: ItemStack): Boolean {
        if (itemStack.`is`(PazBlocks.PLANTZ_FLAG.asItem())) return false
        return super.wantsToPickUp(level, itemStack)
    }

    override fun isSunSensitive(): Boolean = false
    override fun convertsInWater(): Boolean = false
}