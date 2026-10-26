package joshxviii.plantz.item

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazCriteria
import joshxviii.plantz.PazItems
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.block.entity.SunBatteryBlockEntity
import joshxviii.plantz.block.entity.TimeMachineBlockEntity
import joshxviii.plantz.item.component.StoredSun
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class FlagItem(
    block: Block,
    properties: Properties,
) : BlockItem(block, properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        if (player?.isShiftKeyDown==true) return super.useOn(context)
        return InteractionResult.PASS
    }
}