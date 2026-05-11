package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.block.SunBatteryBlock
import joshxviii.plantz.item.component.StoredSun
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.ticks.ContainerSingleItem.BlockContainerSingleItem

class SunBatteryBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState,
) : BlockEntity(
    PazBlocks.SUN_BATTERY_BLOCK_ENTITY, worldPosition, blockState
), BlockContainerSingleItem {
    private var batteryItem: ItemStack = PazItems.SUN_BATTERY.defaultInstance

    fun getStoredSunPercent(): Float {
        return batteryItem.get(PazComponents.STORED_SUN)?.storagePercentage() ?: 0f
    }

    fun addSun(amount: Int) {
        val newStoredSun = batteryItem.get(PazComponents.STORED_SUN)?.addSun(amount) ?: StoredSun(amount)
        batteryItem.set(PazComponents.STORED_SUN, newStoredSun)
        level?.let {
            it.playSound(null, blockPos, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 0.6f, getStoredSunPercent()+0.5f+(it.random.nextFloat()*0.1f))
        }
        updateLevel(newStoredSun.getLevel())
    }

    fun isFull(): Boolean {
        return batteryItem.get(PazComponents.STORED_SUN)?.isFull() ?: false
    }

    fun updateLevel(sunLevel: Int) {
        level?.setBlock(blockPos, blockState.setValue(SunBatteryBlock.LEVEL, sunLevel), 3)
    }

    override fun canTakeItem(into: Container, slot: Int, itemStack: ItemStack): Boolean {
        return false
    }

    override fun applyImplicitComponents(components: DataComponentGetter) {
        super.applyImplicitComponents(components)
        batteryItem.set(PazComponents.STORED_SUN, components.get(PazComponents.STORED_SUN) ?: StoredSun())
    }

    override fun getContainerBlockEntity(): BlockEntity = this

    override fun preRemoveSideEffects(pos: BlockPos, state: BlockState) {
        super.preRemoveSideEffects(pos, state)// drops items
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        if (!batteryItem.isEmpty) output.store("BatteryItem", ItemStack.CODEC, batteryItem)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        batteryItem = input.read("BatteryItem", ItemStack.CODEC).orElse(ItemStack.EMPTY)?: ItemStack.EMPTY
    }

    override fun getTheItem(): ItemStack {
        return batteryItem
    }

    override fun setTheItem(itemStack: ItemStack) {
        if (!itemStack.`is`(PazItems.SUN_BATTERY)) return
        batteryItem = itemStack
    }

}