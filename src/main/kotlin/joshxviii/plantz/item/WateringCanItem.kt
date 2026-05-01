package joshxviii.plantz.item

import joshxviii.plantz.PazSounds
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BoneMealItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent

class WateringCanItem(properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        player.startUsingItem(hand)
        player.playSound(PazSounds.WATERING_CAN)
        return InteractionResult.CONSUME
    }

    override fun releaseUsing(
        itemStack: ItemStack,
        level: Level,
        entity: LivingEntity,
        remainingTime: Int
    ): Boolean {
        return super.releaseUsing(itemStack, level, entity, remainingTime)
    }

    override fun getUseDuration(itemStack: ItemStack, user: LivingEntity): Int {
        return 15
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, itemStack: ItemStack, ticksRemaining: Int) {
        super.onUseTick(level, livingEntity, itemStack, ticksRemaining)
    }

    override fun getUseAnimation(itemStack: ItemStack): ItemUseAnimation {
        return ItemUseAnimation.SPEAR
    }
}