package joshxviii.plantz.item

import joshxviii.plantz.PazComponents
import joshxviii.plantz.inventory.AlmanacMenu
import joshxviii.plantz.item.component.AlmanacDataComponent
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class AlmanacItem(properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (!level.isClientSide && player is ServerPlayer) {
            val stack = player.getItemInHand(hand)
            val data = stack.get(PazComponents.ALMANAC_DATA) ?: AlmanacDataComponent()
            if (data.owner == null) stack.set(
                PazComponents.ALMANAC_DATA,
                data.copy(owner = player.uuid, ownerName = player.gameProfile.name)
            )
            else if (data.owner == player.uuid && data.ownerName != player.gameProfile.name) stack.set(
                PazComponents.ALMANAC_DATA, data.copy(ownerName = player.gameProfile.name)
            )
            val handIndex = if (hand == InteractionHand.MAIN_HAND) 0 else 1
            player.openMenu(object : ExtendedMenuProvider<Int> {
                override fun getDisplayName(): Component = Component.translatable("gui.plantz.almanac")

                override fun createMenu(
                    containerId: Int,
                    inventory: Inventory,
                    player: Player
                ): AbstractContainerMenu = AlmanacMenu(containerId, inventory, handIndex)

                override fun getScreenOpeningData(player: ServerPlayer): Int = handIndex
            })
        }
        player.playSound(SoundEvents.BOOK_PAGE_TURN, 1f, 1f)// TODO custom sounds
        player.awardStat(Stats.ITEM_USED.get(this))
        return InteractionResult.SUCCESS
    }

    override fun onCraftedBy(itemStack: ItemStack, player: Player) {
        super.onCraftedBy(itemStack, player)
        val data = itemStack.get(PazComponents.ALMANAC_DATA) ?: AlmanacDataComponent()
        itemStack.set(PazComponents.ALMANAC_DATA, data.copy(owner = player.uuid, ownerName = player.gameProfile.name))
    }
}
