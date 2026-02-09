package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import joshxviii.plantz.ai.goal.MineBlocksToTargetGoal
import joshxviii.plantz.ai.pathfinding.MinerNavigation
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor


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
            return level.difficulty != Difficulty.PEACEFUL
                    && (EntitySpawnReason.ignoresLightRequirements(spawnReason) || isDarkEnoughToSpawn(level, pos, random))
                    && checkMobSpawnRules(type, level, spawnReason, pos, random) || level.getBlockState(below).`is`(PazTags.BlockTags.YETI_SPAWNABLE_ON)
                    && pos.y < 10
        }
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(2, MineBlocksToTargetGoal(this))
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
    override fun canPickUpLoot(): Boolean = true
    override fun isSunSensitive(): Boolean = false
    override fun convertsInWater(): Boolean = false
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
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))
        if (spawnReason != EntitySpawnReason.CONVERSION) {
            isLeftHanded = false
            setCanBreakDoors(true)
            setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_PICKAXE.defaultInstance)
            setDropChance(EquipmentSlot.MAINHAND, 0.0f)
        }

        return data
    }
}