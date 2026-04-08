package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazItems
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.Difficulty
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

abstract class PazZombie(type: EntityType<out PazZombie>, level: Level) : Zombie(type, level) {

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
    }

    override fun wantsToPickUp(level: ServerLevel, itemStack: ItemStack): Boolean {
        if (itemStack.`is`(PazBlocks.PLANTZ_FLAG.asItem())) return false
        return super.wantsToPickUp(level, itemStack)
    }

    override fun isSunSensitive(): Boolean = false
    override fun convertsInWater(): Boolean = false
}