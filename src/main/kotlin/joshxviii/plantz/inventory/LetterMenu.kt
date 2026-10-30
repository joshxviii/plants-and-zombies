package joshxviii.plantz.inventory

import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazMenus
import joshxviii.plantz.item.component.LetterDataComponent
import net.minecraft.core.component.DataComponents
import net.minecraft.world.InteractionHand
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class LetterMenu(
    containerId: Int,
    val inventory: Inventory,
    val handIndex: Int,
) : AbstractContainerMenu(PazMenus.LETTER_MENU, containerId) {
    val hand: InteractionHand = if (handIndex == 0) InteractionHand.MAIN_HAND else InteractionHand.OFF_HAND
    private val heldInventorySlot = if (hand == InteractionHand.MAIN_HAND) inventory.selectedSlot else 40
    val letterStack: ItemStack
        get() = inventory.player.getItemInHand(hand).takeIf { it.`is`(PazItems.LETTER) } ?: ItemStack.EMPTY
    val letterData: LetterDataComponent
        get() = letterStack.get(PazComponents.LETTER_DATA) ?: LetterDataComponent()
    val editable: Boolean
        get() = !letterData.sign && letterStack.get(DataComponents.WRITTEN_BOOK_CONTENT) == null

    val inputContainer = object : SimpleContainer(1) {
        override fun setChanged() {
            super.setChanged()
            this@LetterMenu.slotsChanged(this)
        }
    }
    val attachmentSlot: Slot = addSlot(object : Slot(inputContainer, 0, if (editable) 10 else 56, 109) {
        override fun mayPlace(itemStack: ItemStack): Boolean =
            editable && !itemStack.`is`(PazItems.LETTER)

        override fun mayPickup(player: Player): Boolean = true

        override fun getMaxStackSize(): Int = 1

        override fun getMaxStackSize(itemStack: ItemStack): Int = 1

        override fun isActive(): Boolean = editable || this.hasItem()
    })

    init {
        addStandardInventorySlots(inventory, 59, 141)
        for (slotIndex in 1 until slots.size) {
            val standardSlot = slots[slotIndex]
            val slot = object : Slot(
                inventory,
                standardSlot.containerSlot,
                standardSlot.x,
                standardSlot.y
            ) {
                override fun isActive(): Boolean = true
                override fun mayPlace(itemStack: ItemStack): Boolean =
                    standardSlot.containerSlot != heldInventorySlot && itemStack !== letterStack

                override fun mayPickup(player: Player): Boolean = standardSlot.containerSlot != heldInventorySlot
            }
            slot.index = slotIndex
            slots[slotIndex] = slot
        }

        inputContainer.setItem(0, letterData.item.copy())
    }

    override fun stillValid(player: Player): Boolean {
        return player.getItemInHand(hand).`is`(PazItems.LETTER)
    }

    override fun slotsChanged(container: Container) {
        if (container !== inputContainer) return
        val letter = letterStack
        if (letter.isEmpty) return

        val current = letterData
        val attachment = attachmentSlot.item.copy()
        if (current.sign && !attachment.isEmpty) return
        if (attachment.count > 1) attachment.count = 1
        letter.set(PazComponents.LETTER_DATA, current.copy(item = attachment))
        inventory.setChanged()
        broadcastChanges()
    }

    override fun removed(player: Player) {
        super.removed(player)
        val letter = letterStack
        if (letter.isEmpty) return
        val current = letterData
        val attachment = attachmentSlot.item.copy()
        if (current.sign && !attachment.isEmpty) return
        letter.set(PazComponents.LETTER_DATA, current.copy(item = attachment))
        inventory.setChanged()
    }

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        if (slotIndex !in slots.indices) return ItemStack.EMPTY
        val slot = slots[slotIndex]
        if (!slot.hasItem()) return ItemStack.EMPTY

        val stack = slot.item
        val original = stack.copy()
        if (slotIndex == 0) {
            if (!moveItemStackTo(stack, 1, slots.size, true)) return ItemStack.EMPTY
        } else if (editable && attachmentSlot.item.isEmpty && attachmentSlot.mayPlace(stack)) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY
        } else if (slotIndex in 1 until 28) {
            if (!moveItemStackTo(stack, 28, 37, false)) return ItemStack.EMPTY
        } else if (slotIndex in 28 until 37) {
            if (!moveItemStackTo(stack, 1, 28, false)) return ItemStack.EMPTY
        } else {
            return ItemStack.EMPTY
        }

        if (stack.isEmpty) slot.setByPlayer(ItemStack.EMPTY)
        else slot.setChanged()
        if (stack.count == original.count) return ItemStack.EMPTY
        slot.onTake(player, stack)
        return original
    }
}