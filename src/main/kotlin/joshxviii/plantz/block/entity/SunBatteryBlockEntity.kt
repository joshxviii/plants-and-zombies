package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazItems
import joshxviii.plantz.block.SunBatteryBlock
import joshxviii.plantz.item.component.StoredSun
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
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
    private var item: ItemStack = PazItems.SUN_BATTERY.defaultInstance

    fun getStoredSunPercent(): Float {
        return item.get(PazComponents.STORED_SUN)?.storagePercentage() ?: 0f
    }

    fun addSun(amount: Int) {
        val newStoredSun = item.get(PazComponents.STORED_SUN)?.addSun(amount) ?: StoredSun(amount)
        item.set(PazComponents.STORED_SUN, newStoredSun)
        level?.let {
            it.playSound(null, blockPos, SoundEvents.BUBBLE_POP, SoundSource.BLOCKS, 0.6f, getStoredSunPercent()+0.5f+(it.random.nextFloat()*0.1f))
        }
        updateLevel(newStoredSun.getLevel())
    }

    fun isFull(): Boolean {
        return item.get(PazComponents.STORED_SUN)?.isFull() ?: false
    }

    fun updateLevel(sunLevel: Int) {
        level?.setBlock(blockPos, blockState.setValue(SunBatteryBlock.LEVEL, sunLevel), 3)
    }

    override fun canTakeItem(into: Container, slot: Int, itemStack: ItemStack): Boolean {
        return false
    }

    override fun applyImplicitComponents(components: DataComponentGetter) {
        super.applyImplicitComponents(components)
        item.set(PazComponents.STORED_SUN, components.get(PazComponents.STORED_SUN) ?: StoredSun())
    }

    override fun getContainerBlockEntity(): BlockEntity = this

    override fun preRemoveSideEffects(pos: BlockPos, state: BlockState) {
        super.preRemoveSideEffects(pos, state)// drops items
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.store("Item", ItemStack.CODEC, this.item?: ItemStack.EMPTY)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        this.item = input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY)?: ItemStack.EMPTY
    }

    override fun getTheItem(): ItemStack {
        return this.item
    }

    override fun setTheItem(itemStack: ItemStack) {
        if (!itemStack.`is`(PazItems.SUN_BATTERY)) return
        this.item = itemStack
    }

}