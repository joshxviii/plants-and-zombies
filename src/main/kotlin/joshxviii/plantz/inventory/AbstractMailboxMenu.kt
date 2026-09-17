package joshxviii.plantz.inventory

import joshxviii.plantz.MailboxData
import joshxviii.plantz.block.entity.MailboxManager
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu

abstract class AbstractMailboxMenu(
    menu: ExtendedMenuType<*, *>,
    inventory: Inventory,
    containerId: Int,
    var container: Container
): AbstractContainerMenu(menu, containerId) {

    init {
        container.startOpen(inventory.player)
    }

    var selectedMailboxPos: BlockPos? = null
    var slotUpdateListener = Runnable {}
    var mailboxListUpdateListener = Runnable {}
    var availableMailboxes: List<MailboxData> = emptyList()
        set(value) {
            field = value
            updateFilteredMailboxes()
            mailboxListUpdateListener.run()
        }
    var filteredMailboxes: List<MailboxData> = emptyList()
    var searchFilter: String = ""
        set(value) {
            field = value
            updateFilteredMailboxes()
            slotUpdateListener.run()
        }

    var responseMessage: Component = Component.empty()
    var responseTimeout: Int = 0

    fun updateFilteredMailboxes() {
        filteredMailboxes = MailboxManager.searchMailboxes(availableMailboxes, searchFilter)
    }

    fun getMailbox(index: Int?): MailboxData? {
        if (index == null ) return null
        if (index < 0 || index >= filteredMailboxes.size) return null
        return filteredMailboxes[index]
    }

    override fun removed(player: Player) {
        super.removed(player)
        container.stopOpen(player)
    }
}