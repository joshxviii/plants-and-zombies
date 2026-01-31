package joshxviii.plantz.inventory

import joshxviii.plantz.PazMenus
import joshxviii.plantz.PazTags
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.block.entity.MailboxManager
import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import java.util.function.BiConsumer

class MailboxMenu(
    containerId: Int,
    val inventory: Inventory,
    private val access: ContainerLevelAccess = ContainerLevelAccess.NULL
) : AbstractContainerMenu(PazMenus.MAILBOX_MENU, containerId) {

    private var slotUpdateListener = Runnable {}
    var selectedMailboxIndex: Int? = null
    val mailSlot: Slot
    private var availableMailboxes: List<MailboxBlockEntity> = emptyList()
    var filteredMailboxes: List<MailboxBlockEntity> = emptyList()
    var searchFilter: String = ""
        set(value) {
            field = value
            updateFilteredMailboxes()
            this.slotUpdateListener.run()
        }

    init {

    }

    private val inputContainer: Container = object : SimpleContainer(1) {
        init { Objects.requireNonNull<MailboxMenu>(this@MailboxMenu) }
        override fun setChanged() {
            super.setChanged()
            this@MailboxMenu.slotsChanged(this)
            this@MailboxMenu.slotUpdateListener.run()
        }
    }

    init {
        mailSlot = addSlot(object : Slot(inputContainer, 0, 20, 32) {
            init { Objects.requireNonNull(this@MailboxMenu) }
            override fun mayPlace(itemStack: ItemStack): Boolean = true
        })
        addStandardInventorySlots(inventory, 8, 98)
    }

    fun getMailbox(index: Int?): MailboxBlockEntity? {
        if (index == null ) return null
        if (index < 0 || index >= filteredMailboxes.size) return null
        return filteredMailboxes[index]
    }

    override fun stillValid(player: Player): Boolean {
        return access.evaluate( { level: Level, pos: BlockPos ->
            if (!isValidBlock(level.getBlockState(pos))) false
            else player.isWithinBlockInteractionRange(pos, 4.0)
        }, true)
    }
    fun isValidBlock(state: BlockState): Boolean = state.`is`(PazTags.BlockTags.MAILBOX)

    fun refreshMailboxList() {
        val level = inventory.player.level()

        availableMailboxes = MailboxManager.getMailboxesInLevel(level)
            //TODO find a way to get the blockPos of this mailbox from the menu
            .filterNot { mailbox -> mailbox.blockPos.distSqr(inventory.player.blockPosition()) < 32 }
        updateFilteredMailboxes()
    }

    fun updateFilteredMailboxes() {
        filteredMailboxes = MailboxManager.searchMailboxes(availableMailboxes, searchFilter)
    }

    override fun slotsChanged(container: Container) {
        val mailStack = this.mailSlot.item
        if (!mailStack.isEmpty) {
            //TODO
            this.broadcastChanges()
        }
    }

    fun registerUpdateListener(slotUpdateListener: Runnable) {
        this.slotUpdateListener = slotUpdateListener
    }

    fun sendMail(): Boolean {
        if (selectedMailboxIndex == null || mailSlot.item.isEmpty) return false

        val targetMailbox = getMailbox(selectedMailboxIndex)
        if (targetMailbox != null) {
            // TODO: Implement mail transfer
            targetMailbox.setItem(0, mailSlot.item.copy())

            mailSlot.set(ItemStack.EMPTY)
        }

        return true
    }

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        val clicked = ItemStack.EMPTY
        val slot = slots[slotIndex]?: return clicked
        if (slot.hasItem()) {
            val stack = slot.item
            if (slotIndex == 0) {
                if (!this.moveItemStackTo(stack, 1, 37, false)) return ItemStack.EMPTY
            } else {
                if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY
            }
        }
        return clicked
    }

    override fun removed(player: Player) {
        super.removed(player)
        if(mailSlot.hasItem()) clearContainer(player, inputContainer)
    }
}