package joshxviii.plantz.item

import joshxviii.plantz.PazComponents
import net.minecraft.util.Mth
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

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

}