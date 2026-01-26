package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazSounds
import joshxviii.plantz.ai.goal.MineBlocksToTargetGoal
import joshxviii.plantz.ai.pathfinding.MinerNavigation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor


class Miner(type: EntityType<out Miner>, level: Level) : Zombie(type, level) {

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(2, MineBlocksToTargetGoal(this))
    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.MINER_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.MINER_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.MINER_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    override fun getAttackAnim(a: Float): Float {
        return super.getAttackAnim(a)
    }

    override fun hasLineOfSight(target: Entity): Boolean = true

    override fun createNavigation(level: Level): PathNavigation {
        return MinerNavigation(this, level)
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

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        var groupData = groupData
        groupData = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        if (spawnReason != EntitySpawnReason.CONVERSION) {
            isLeftHanded = false
            setCanBreakDoors(true)
            setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_PICKAXE.defaultInstance)
        }

        return groupData
    }
}