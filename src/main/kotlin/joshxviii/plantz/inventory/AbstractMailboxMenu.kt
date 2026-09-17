package joshxviii.plantz.inventory

import joshxviii.plantz.MailboxData
import joshxviii.plantz.block.entity.MailboxManager
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.AbstractContainerMenu
import kotlin.collections.indexOf

abstract class AbstractMailboxMenu(
    menu: ExtendedMenuType<*, *>,
    containerId: Int,
): AbstractContainerMenu(menu, containerId) {
    var selectedMailboxIndex: Int? = null
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

    fun getIndexFromPos(targetPos: BlockPos): Int {
        return availableMailboxes.map { it.blockPos }.filter { it == targetPos }.indexOf(targetPos)
    }
}