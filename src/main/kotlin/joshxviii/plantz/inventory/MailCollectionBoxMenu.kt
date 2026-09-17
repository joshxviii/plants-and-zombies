package joshxviii.plantz.inventory

import joshxviii.plantz.MailCollectionBoxData
import joshxviii.plantz.MailboxData
import joshxviii.plantz.PazMenus
import joshxviii.plantz.block.entity.MailCollectionBoxEntity
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketType
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class MailCollectionBoxMenu(
    containerId: Int,
    val inventory: Inventory,
    val data: MailCollectionBoxData,
    private val collectionBox: Container = SimpleContainer(MailCollectionBoxEntity.INVENTORY_SIZE),
) : AbstractMailboxMenu(PazMenus.MAIL_COLLECTION_BOX_MENU, inventory, containerId, collectionBox) {

    init {
        val size = collectionBox.containerSize
        val rows = 2
        for (i in 0 until rows) {
            for (j in 0 until size/rows) {
                addSlot(Slot(collectionBox, j*2+i, 31 + j * 18, 17 + i * 18))
            }
        }

        addStandardInventorySlots(inventory, 8, 98)
    }

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        var clicked: ItemStack = ItemStack.EMPTY
        val slot: Slot = this.slots[slotIndex]
        if (slot.hasItem()) {
            val stack = slot.item
            clicked = stack.copy()
            if (slotIndex < this.collectionBox.containerSize) {
                if (!this.moveItemStackTo(stack, this.collectionBox.containerSize, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(stack, 0, this.collectionBox.containerSize, false)) {
                return ItemStack.EMPTY
            }

            if (stack.isEmpty) slot.setByPlayer(ItemStack.EMPTY)
            else slot.setChanged()
        }

        return clicked
    }

    override fun stillValid(player: Player): Boolean = collectionBox.stillValid(player)
}