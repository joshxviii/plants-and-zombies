package joshxviii.plantz.item

import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.item.component.StoredSun
import net.minecraft.core.component.DataComponents
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.BundleItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.BundleContents

class SunBatteryItem(properties: Properties) : Item(properties) {

    override fun isBarVisible(stack: ItemStack): Boolean {
        stack.get(PazComponents.STORED_SUN)?.let {
            return it.hasSun()
        }
        return false
    }
    override fun getBarColor(stack: ItemStack): Int {
        stack.get(PazComponents.STORED_SUN)?.let {
            return if (it.isFull()) 14369328 else 16768870
        }
        return super.getBarColor(stack)
    }
    override fun getBarWidth(stack: ItemStack): Int {
        stack.get(PazComponents.STORED_SUN)?.let {
            return Mth.ceil(it.storagePercentage() * 13)
        }
        return super.getBarWidth(stack)
    }

    override fun overrideOtherStackedOnMe(
        self: ItemStack,
        other: ItemStack,
        slot: Slot,
        clickAction: ClickAction,
        player: Player,
        carriedItem: SlotAccess
    ): Boolean {
        return handleSunStorageInteraction(
            targetStack = self,
            sourceStack = other,
            slot = slot,
            clickAction = clickAction,
            player = player,
            destinationSetter = carriedItem::set
        ) ?: super.overrideOtherStackedOnMe(self, other, slot, clickAction, player, carriedItem)
    }

    override fun overrideStackedOnOther(self: ItemStack, slot: Slot, clickAction: ClickAction, player: Player): Boolean {
        return handleSunStorageInteraction(
            targetStack = self,
            sourceStack = slot.item,
            slot = slot,
            clickAction = clickAction,
            player = player,
            destinationSetter = slot::set
        ) ?: super.overrideStackedOnOther(self, slot, clickAction, player)
    }

    private fun handleSunStorageInteraction(
        targetStack: ItemStack,
        sourceStack: ItemStack,
        slot: Slot,
        clickAction: ClickAction,
        player: Player,
        destinationSetter: (ItemStack) -> Unit
    ): Boolean? {
        val sunStorage = targetStack.get(PazComponents.STORED_SUN) ?: return false
        return when (clickAction) {
            ClickAction.PRIMARY if sourceStack.`is`(PazItems.SUN) -> {
                if (slot.allowModification(player)) tryAddSunToStorage(targetStack, sourceStack, sunStorage)
                true
            }
            ClickAction.SECONDARY if sourceStack.isEmpty -> {
                if (slot.allowModification(player)) tryRemoveSunFromStorage(targetStack, sunStorage, destinationSetter)
                true
            }
            else -> null
        }
    }

    private fun tryAddSunToStorage(
        targetStack: ItemStack,
        sunStack: ItemStack,
        sunStorage: StoredSun
    ) {
        if (sunStorage.isFull()) return

        val availableSpace = sunStorage.max - sunStorage.storedSun
        val amountToAdd = sunStack.count.coerceAtMost(availableSpace)

        sunStack.shrink(amountToAdd)
        targetStack.set(PazComponents.STORED_SUN, sunStorage.addSun(amountToAdd))
    }

    private fun tryRemoveSunFromStorage(
        targetStack: ItemStack,
        sunStorage: StoredSun,
        destinationSetter: (ItemStack) -> Unit
    ) {
        if (!sunStorage.hasSun()) return

        val sunItem = PazItems.SUN.defaultInstance
        val amountToRemove = sunItem.maxStackSize.coerceAtMost(sunStorage.storedSun)

        targetStack.set(PazComponents.STORED_SUN, sunStorage.removeSun(amountToRemove))
        destinationSetter(sunItem.copyWithCount(amountToRemove))
    }

    override fun getUseDuration(itemStack: ItemStack, user: LivingEntity): Int {
        return super.getUseDuration(itemStack, user)
    }

}