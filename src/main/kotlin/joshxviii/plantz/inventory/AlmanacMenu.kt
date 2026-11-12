package joshxviii.plantz.inventory

import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazMenus
import joshxviii.plantz.PazTags
import joshxviii.plantz.item.SeedPacketItem
import joshxviii.plantz.item.component.AlmanacDataComponent
import joshxviii.plantz.item.component.AlmanacTab
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class AlmanacMenu(
    containerId: Int,
    val inventory: Inventory,
    val handIndex: Int,
) : AbstractContainerMenu(PazMenus.ALMANAC_MENU, containerId) {
    companion object {
        const val COLUMNS = 3
        const val ROWS = 5
        const val SLOTS_PER_PAGE = COLUMNS * ROWS
        const val SELECT_BASE = 1 shl 16
        const val TAKE_BASE = 1 shl 17

        private const val GRID_LEFT = 17
        private const val GRID_TOP = 15

        const val INVENTORY_X = 20
        const val INVENTORY_Y = 153

        fun plantTypes(): List<EntityType<*>> =
            BuiltInRegistries.ENTITY_TYPE.getTagOrEmpty(PazTags.EntityTypes.PLANT).map { it.value() }

        fun pageCount(tab: Int, plantCount: Int): Int =
            if (tab == AlmanacTab.SEED_PACKETS.ordinal) maxOf(1, (plantCount + SLOTS_PER_PAGE - 1) / SLOTS_PER_PAGE) else 1

        fun navigationId(tab: Int, page: Int): Int = tab * 256 + page

        fun slotX(index: Int): Int = GRID_LEFT + (index % SLOTS_PER_PAGE % COLUMNS) * 27
        fun slotY(index: Int): Int = GRID_TOP + (index % SLOTS_PER_PAGE / COLUMNS) * 24
    }

    var responseMessage: Component = Component.empty()
    var responseTimeout: Int = 0

    val hand: InteractionHand = if (handIndex == 0) InteractionHand.MAIN_HAND else InteractionHand.OFF_HAND
    private val heldInventorySlot = if (hand == InteractionHand.MAIN_HAND) inventory.selectedSlot else 40

    val plantTypes: List<EntityType<*>> = plantTypes()
    val plantCount: Int = plantTypes.size

    val almanacStack: ItemStack
        get() = inventory.player.getItemInHand(hand).takeIf { it.`is`(PazItems.ALMANAC) } ?: ItemStack.EMPTY
    val almanacData: AlmanacDataComponent
        get() = almanacStack.get(PazComponents.ALMANAC_DATA) ?: AlmanacDataComponent()

    private val tabData = addDataSlot(DataSlot.standalone())
    private val pageData = addDataSlot(DataSlot.standalone())
    private val selectedData = addDataSlot(DataSlot.standalone())

    val tab: Int get() = tabData.get()
    val page: Int get() = pageData.get()
    val selected: Int get() = selectedData.get()
    val pageCount: Int get() = pageCount(tab, plantCount)

    private var loading = true
    val collection = object : SimpleContainer(plantCount) {
        override fun setChanged() {
            super.setChanged()
            if (!loading) save()
        }
    }

    inner class PlantSlot(private val plantIndex: Int) :
        Slot(collection, plantIndex, slotX(plantIndex), slotY(plantIndex)) {
        override fun mayPlace(itemStack: ItemStack): Boolean = canHold(itemStack, plantIndex)
        override fun set(itemStack: ItemStack) {
            super.set(itemStack)
            if (!itemStack.isEmpty && !loading) selectedData.set(plantIndex)
        }
        override fun getMaxStackSize(): Int = 1
        override fun getMaxStackSize(itemStack: ItemStack): Int = 1
        override fun isActive(): Boolean = tab == AlmanacTab.SEED_PACKETS.ordinal && plantIndex / SLOTS_PER_PAGE == page
    }

    init {
        val data = almanacData
        tabData.set(data.tab.coerceIn(0, AlmanacTab.entries.lastIndex))
        pageData.set(data.page.coerceIn(0, pageCount - 1))
        selectedData.set(0)

        for (index in 0 until plantCount) {
            addSlot(PlantSlot(index))
            collection.setItem(index, data.seedPackets.getOrNull(index)?.copy() ?: ItemStack.EMPTY)
        }
        loading = false

        addStandardInventorySlots(inventory, INVENTORY_X, INVENTORY_Y)
        for (slotIndex in plantCount until slots.size) {
            val standardSlot = slots[slotIndex]
            val slot = object : Slot(inventory, standardSlot.containerSlot, standardSlot.x, standardSlot.y) {
                override fun mayPlace(itemStack: ItemStack): Boolean = standardSlot.containerSlot != heldInventorySlot
                override fun mayPickup(player: Player): Boolean = standardSlot.containerSlot != heldInventorySlot
            }
            slot.index = slotIndex
            slots[slotIndex] = slot
        }
    }

    override fun canTakeItemForPickAll(stack: ItemStack, slot: Slot): Boolean = slot !is PlantSlot

    fun plantTypeOf(stack: ItemStack?): EntityType<*>? =
        if (stack == null || !stack.`is`(PazItems.SEED_PACKET)) null else SeedPacketItem.typeFromStack(stack)

    private fun duplicateWarning(type: EntityType<*>) {
        responseMessage = Component.translatable("gui.plantz.almanac.duplicate", type.description).withStyle(ChatFormatting.RED)
        responseTimeout = 50
    }

    fun canHold(stack: ItemStack, index: Int): Boolean {
        val type = plantTypeOf(stack) ?: return false
        if (type !in plantTypes) return false
        val result = (0 until plantCount).none { it != index && plantTypeOf(collection.getItem(it)) == type }
        if (!result) duplicateWarning(type)
        return result
    }

    fun selectedType(): EntityType<*>? = plantTypeOf(selectedItem()) ?: return null

    fun selectedItem(index: Int = selected): ItemStack? =
        if (index in 0 until plantCount) collection.getItem(index) else null

    private fun save() {
        if (inventory.player.level().isClientSide) return
        val almanac = almanacStack
        if (almanac.isEmpty) return
        val stacks = (0 until plantCount).map { collection.getItem(it).copy() }.dropLastWhile { it.isEmpty }
        almanac.set(PazComponents.ALMANAC_DATA, almanacData.copy(seedPackets = stacks))
        inventory.setChanged()
    }

    private fun tryInsert(stack: ItemStack): Boolean {
        for (index in 0 until plantCount) {
            if (!collection.getItem(index).isEmpty || !canHold(stack, index)) continue
            collection.setItem(index, stack.copyWithCount(1))
            stack.shrink(1)
            selectedData.set(index)
            return true
        }
        return false
    }

    private fun setTabAndPage(newTab: Int, newPage: Int) {
        val safeTab = newTab.coerceIn(0, AlmanacTab.entries.lastIndex)
        val safePage = newPage.coerceIn(0, pageCount(safeTab, plantCount) - 1)
        tabData.set(safeTab)
        pageData.set(safePage)
        val almanac = almanacStack
        if (almanac.isEmpty || inventory.player.level().isClientSide) return
        val data = almanacData
        if (data.tab == safeTab && data.page == safePage) return
        almanac.set(PazComponents.ALMANAC_DATA, data.copy(tab = safeTab, page = safePage))
        inventory.setChanged()
    }

    override fun clickMenuButton(player: Player, buttonId: Int): Boolean {
        if (buttonId < 0) return false
        if (buttonId >= TAKE_BASE) {
            val slot = slots.getOrNull(buttonId - TAKE_BASE) as? PlantSlot ?: return false
            if (!carried.isEmpty || !slot.hasItem() || !slot.isActive) return false
            setCarried(slot.item.copy())
            slot.set(ItemStack.EMPTY)
            return true
        }
        if (buttonId >= SELECT_BASE) {
            val index = buttonId - SELECT_BASE
            if (index !in 0 until plantCount) return false
            selectedData.set(index)
            return true
        }
        setTabAndPage(buttonId / 256, buttonId % 256)
        return true
    }

    override fun stillValid(player: Player): Boolean = player.getItemInHand(hand).`is`(PazItems.ALMANAC)

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        if (slotIndex !in slots.indices) return ItemStack.EMPTY
        val slot = slots[slotIndex]
        if (!slot.hasItem()) return ItemStack.EMPTY
        if (slotIndex >= plantCount && slot.containerSlot == heldInventorySlot) return ItemStack.EMPTY

        val stack = slot.item
        val original = stack.copy()
        val moved = when {
            slotIndex < plantCount -> moveItemStackTo(stack, plantCount, slots.size, true)
            tryInsert(stack) -> true
            slotIndex < plantCount + 27 -> moveItemStackTo(stack, plantCount + 27, slots.size, false)
            else -> moveItemStackTo(stack, plantCount, plantCount + 27, false)
        }
        if (!moved) return ItemStack.EMPTY
        if (stack.isEmpty) slot.setByPlayer(ItemStack.EMPTY) else slot.setChanged()
        return original
    }
}
