package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.TimeMachineData
import joshxviii.plantz.block.TimeMachineBlock
import joshxviii.plantz.block.TimeMachineBlock.Companion.FACING
import joshxviii.plantz.block.TimeMachineBlock.Companion.STATE
import joshxviii.plantz.block.TimeMachineState
import joshxviii.plantz.block.TimePortalBlock
import joshxviii.plantz.inventory.TimeMachineMenu
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.ticks.ContainerSingleItem.BlockContainerSingleItem
import net.minecraft.core.Direction
import net.minecraft.world.Containers
import joshxviii.plantz.PazItems
import kotlin.jvm.optionals.getOrDefault

class TimeMachineBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(PazBlocks.TIME_MACHINE_ENTITY, worldPosition, blockState), BlockContainerSingleItem, ExtendedMenuProvider<TimeMachineData> {
    internal var legacyItem: ItemStack = ItemStack.EMPTY
    private var detached = false
    var item: ItemStack
        get() {
            if (detached || isRemoved) return ItemStack.EMPTY
            val serverLevel = level as? ServerLevel ?: return legacyItem
            return serverLevel.getTimeMachineManager().register(this).battery
        }
        set(value) { setTheItem(value) }
    var tickCount: Int = 0
    var activeTime: Int = 0

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: TimeMachineBlockEntity) {
            blockEntity.tickCount++
            if (state.getValue(STATE) == TimeMachineState.ACTIVE) blockEntity.activeTime++
            else blockEntity.activeTime = 0
            if (level is ServerLevel) level.getTimeMachineManager().register(blockEntity)
        }
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        if (!legacyItem.isEmpty) output.store("Item", ItemStack.CODEC, legacyItem)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        legacyItem = input.read("Item", ItemStack.CODEC).getOrDefault(ItemStack.EMPTY)
    }

    override fun getContainerBlockEntity(): BlockEntity = this

    override fun getScreenOpeningData(player: ServerPlayer): TimeMachineData = TimeMachineData(blockPos)
    override fun getDisplayName(): Component  = Component.translatable("block.plantz.time_machine")
    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu = TimeMachineMenu(containerId, inventory, blockPos, this)

    override fun getTheItem(): ItemStack = item
    override fun setTheItem(itemStack: ItemStack) {
        if (detached || isRemoved || (!itemStack.isEmpty && !itemStack.`is`(PazItems.SUN_BATTERY))) return
        val serverLevel = level as? ServerLevel
        if (serverLevel == null) legacyItem = itemStack
        else serverLevel.getTimeMachineManager().register(this).battery = itemStack
        setChanged()
    }

    override fun splitTheItem(count: Int): ItemStack = item.split(count).also { setChanged() }

    override fun setChanged() {
        super.setChanged()
        (level as? ServerLevel)?.getTimeMachineManager()?.changed()
    }

    override fun getMaxStackSize(): Int = 1
    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = slot == 0 && stack.`is`(PazItems.SUN_BATTERY)

    override fun preRemoveSideEffects(pos: BlockPos, state: BlockState) {
        val serverLevel = level as? ServerLevel ?: return
        val drop = serverLevel.getTimeMachineManager().remove(this)
        detached = true
        if (!drop.isEmpty) Containers.dropItemStack(serverLevel, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), drop)
    }

    internal fun applySharedState(newState: TimeMachineState, charge: Int, facing: Direction) {
        val level = level as? ServerLevel ?: return
        val oldState = blockState.getValue(STATE)
        val newBlockState = blockState.setValue(STATE, newState)
            .setValue(TimeMachineBlock.LEVEL, charge).setValue(FACING, facing)
        if (blockState != newBlockState) {
            if (oldState == TimeMachineState.INACTIVE && newState != TimeMachineState.INACTIVE) playSound(SoundEvents.COPPER_BULB_PLACE)
            else if (oldState != TimeMachineState.INACTIVE && newState == TimeMachineState.INACTIVE) playSound(SoundEvents.CRAFTER_CRAFT, 1.2f)
            level.setBlock(blockPos, newBlockState, 3)
        }
        updatePortal(newState)
    }

    fun updatePortal(state: TimeMachineState = blockState.getValue(STATE)) {
        val level = level as? ServerLevel ?: return
        val portalPos = blockPos.above().above()
        val portalState = level.getBlockState(portalPos)

        if (state == TimeMachineState.ACTIVE) {
            if (portalState.`is`(PazBlocks.TIME_PORTAL)) {
                val facing = blockState.getValue(FACING)
                if (portalState.getValue(TimePortalBlock.FACING) != facing) {
                    level.setBlockAndUpdate(portalPos, portalState.setValue(TimePortalBlock.FACING, facing))
                }
                return
            }
            if (!portalState.canBeReplaced()) return
            playSound(SoundEvents.BEACON_ACTIVATE, 1.9f)
            level.setBlockAndUpdate(portalPos, PazBlocks.TIME_PORTAL.defaultBlockState().setValue(TimePortalBlock.FACING, blockState.getValue(FACING)))
        }
        else if (portalState.`is`(PazBlocks.TIME_PORTAL)) {
            playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.7f)
            level.setBlockAndUpdate(portalPos, Blocks.AIR.defaultBlockState())
        }
    }

    fun playSound(event: SoundEvent, pitch: Float = 1.0f) {
        level?.playSound(null, blockPos, event, SoundSource.BLOCKS, 1.0f, pitch)
    }
}