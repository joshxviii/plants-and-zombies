package joshxviii.plantz.item.component

import joshxviii.plantz.PazComponents
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

object BlockingItemBreakChance {
    @JvmStatic
    fun handle(level: Level, item: ItemStack, user: LivingEntity, damage: Float): Boolean {
        val breakChance = item.get(PazComponents.BLOCKING_ITEM_BREAK_CHANCE) ?: return false
        if (level !is ServerLevel) return true

        if (user is Player) {
            user.awardStat(Stats.ITEM_USED.get(item.item))
        }

        if (damage > 0.0f && user.random.nextFloat() < breakChance) {
            val breakSound = item.get(DataComponents.BREAK_SOUND) ?: SoundEvents.ITEM_BREAK
            item.shrink(1)
            if (!user.isSilent) {
                level.playSound(
                    null,
                    user.x,
                    user.y,
                    user.z,
                    breakSound,
                    user.soundSource,
                    0.8f,
                    0.8f + user.random.nextFloat() * 0.4f
                )
            }
        }

        return true
    }
}
