package joshxviii.plantz.item

import joshxviii.plantz.PazSounds
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.level.Level

class SunStaffItem(properties: Properties) : Item(properties) {
    companion object {
        const val USE_DURATION = 8
        private const val BEAM_RANGE = 14.0
        private const val BEAM_WIDTH = 0.1
        private const val BEAM_DAMAGE = 7.0f
        private const val BEAM_COLOR = 0xFF00FF
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        player.startUsingItem(hand)
        return InteractionResult.CONSUME
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, itemStack: ItemStack, ticksRemaining: Int) {
        if (ticksRemaining==USE_DURATION)
            level.playSound(livingEntity, livingEntity.blockPosition(), PazSounds.PLANT_GENERATE_SUN, livingEntity.soundSource, 1.0f, 0.5f)
        if (ticksRemaining==1) healingBeam(level, livingEntity, itemStack, BEAM_DAMAGE)
    }

    private fun healingBeam(
        level: Level,
        livingEntity: LivingEntity,
        itemStack: ItemStack?,
        damage: Float
    ) {

    }

    override fun getUseDuration(itemStack: ItemStack, user: LivingEntity): Int {
        return USE_DURATION
    }

    override fun getUseAnimation(itemStack: ItemStack): ItemUseAnimation {
        return ItemUseAnimation.SPEAR
    }
}