package joshxviii.plantz.inventory

import joshxviii.plantz.PazMenus
import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.*

class TimeMachineMenu(
    containerId: Int,
    val inventory: Inventory,
    val blockPos: BlockPos,
    private val timeMachine: Container = SimpleContainer(1),
) : AbstractContainerMenu(PazMenus.TIME_MACHINE_MENU, containerId) {

    val batterySlot: Slot = addSlot(Slot(timeMachine, 0, 80, 39))

    init {
        addStandardInventorySlots(inventory, 8, 98)
    }

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        var clicked = ItemStack.EMPTY
        val slot: Slot = this.slots[slotIndex]
        if (slot.hasItem()) {
            val stack = slot.item
            clicked = stack.copy()
            if (slotIndex < this.timeMachine.containerSize) {
                if (!this.moveItemStackTo(stack, this.timeMachine.containerSize, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(stack, 0, this.timeMachine.containerSize, false)) {
                return ItemStack.EMPTY
            }

            if (stack.isEmpty) slot.setByPlayer(ItemStack.EMPTY)
            else slot.setChanged()
        }
        return clicked
    }

    override fun stillValid(player: Player): Boolean = timeMachine.stillValid(player)
}