package joshxviii.plantz.item

import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BoneMealItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.gameevent.GameEvent

class SunItem(properties: Properties) : Item(properties) {

    // bone meal like behavior
    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val relative = pos.relative(context.clickedFace)
        val sunStack = context.itemInHand
        if (BoneMealItem.growCrop(sunStack, level, pos)) {
            if (!level.isClientSide && context.player!=null) {
                sunStack.causeUseVibration(context.player!!, GameEvent.ITEM_INTERACT_FINISH)
                level.levelEvent(1505, pos, 15)
            }

            return InteractionResult.SUCCESS
        } else {
            val clickedState = level.getBlockState(pos)
            val solidBlockFace = clickedState.isFaceSturdy(level, pos, context.clickedFace)
            if (solidBlockFace && BoneMealItem.growWaterPlant(
                    sunStack,
                    level,
                    relative,
                    context.clickedFace
                )
            ) {
                if (!level.isClientSide && context.player!=null) {
                    sunStack.causeUseVibration(context.player!!, GameEvent.ITEM_INTERACT_FINISH)
                    level.levelEvent(1505, relative, 15)
                }

                return InteractionResult.SUCCESS
            } else {
                return InteractionResult.PASS
            }
        }
    }
}