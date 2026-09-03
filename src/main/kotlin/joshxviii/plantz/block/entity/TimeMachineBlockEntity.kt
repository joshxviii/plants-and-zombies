package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazComponents
import joshxviii.plantz.TimeMachineData
import joshxviii.plantz.block.TimeMachineBlock
import joshxviii.plantz.block.TimeMachineBlock.Companion.STATE
import joshxviii.plantz.block.TimeMachineState
import joshxviii.plantz.inventory.TimeMachineMenu
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.ticks.ContainerSingleItem.BlockContainerSingleItem

class TimeMachineBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(PazBlocks.TIME_MACHINE_ENTITY, worldPosition, blockState), BlockContainerSingleItem, ExtendedMenuProvider<TimeMachineData> {
    var item: ItemStack = ItemStack.EMPTY
    var tickCount: Int = 0
    var activeTime: Int = 0

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: TimeMachineBlockEntity) {
            blockEntity.tickCount++
            if (state.getValue(STATE) == TimeMachineState.ACTIVE) blockEntity.activeTime++
            else blockEntity.activeTime = 0

            if (level.isClientSide) return
            blockEntity.item.get(PazComponents.STORED_SUN)?.let {
                level.setBlock(pos, state.setValue(TimeMachineBlock.LEVEL, it.getLevel()), 3)
                if (it.hasSun() && level.hasNeighborSignal(pos)) {
                    blockEntity.updateTimeMachineState(TimeMachineState.ACTIVE)
                    if (blockEntity.tickCount % 38 == 0) {
                        val newSun = it.removeSun()
                        blockEntity.item.set(PazComponents.STORED_SUN, newSun)
                    }
                }
                else blockEntity.updateTimeMachineState(TimeMachineState.BATTERY)
            } ?: {
                blockEntity.updateTimeMachineState(TimeMachineState.INACTIVE)
            }
        }
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        if (!item.isEmpty) output.store("Item", ItemStack.CODEC, this.item)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        this.item = input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY)?: ItemStack.EMPTY
    }

    override fun getContainerBlockEntity(): BlockEntity = this

    override fun getScreenOpeningData(player: ServerPlayer): TimeMachineData = TimeMachineData(blockPos)
    override fun getDisplayName(): Component  = Component.translatable("block.plantz.time_machine")
    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu = TimeMachineMenu(containerId, inventory, blockPos, this)

    override fun getTheItem(): ItemStack = item
    override fun setTheItem(itemStack: ItemStack) {
        item = itemStack
        if (itemStack.isEmpty) updateTimeMachineState(TimeMachineState.INACTIVE)
        setChanged()
    }

    fun updateTimeMachineState(newState: TimeMachineState) {
        val oldState = blockState.getValue(STATE)
        val level = level!!
        if (oldState == newState) return
        if (oldState == TimeMachineState.INACTIVE && newState == TimeMachineState.BATTERY) playSound(SoundEvents.COPPER_BULB_PLACE)
        else if (oldState == TimeMachineState.ACTIVE) playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.7f)
        else if (newState == TimeMachineState.ACTIVE) playSound(SoundEvents.BEACON_ACTIVATE, 1.9f)
        else if (oldState == TimeMachineState.BATTERY && newState == TimeMachineState.INACTIVE) playSound(SoundEvents.CRAFTER_CRAFT, 1.2f)

        if (newState == TimeMachineState.INACTIVE) level.setBlock(blockPos, blockState.setValue(TimeMachineBlock.LEVEL, 0), 3)

        level.setBlock(blockPos, blockState.setValue(STATE, newState), 3)
    }

    fun playSound(event: SoundEvent, pitch: Float = 1.0f) {
        level?.playSound(null, blockPos, event, SoundSource.BLOCKS, 1.0f, pitch)
    }
}