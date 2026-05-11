package joshxviii.plantz.block.entity

import joshxviii.plantz.MailboxData
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazEffects
import joshxviii.plantz.TimeMachineData
import joshxviii.plantz.effect.ZombieOmenMobEffect
import joshxviii.plantz.inventory.TimeMachineMenu
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block.getId
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import net.minecraft.world.ticks.ContainerSingleItem.BlockContainerSingleItem
import kotlin.math.floor

class TimeMachineBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(PazBlocks.TIME_MACHINE_ENTITY, worldPosition, blockState), BlockContainerSingleItem, ExtendedMenuProvider<TimeMachineData> {

    companion object {

    }

    var item: ItemStack = ItemStack.EMPTY

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        
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
    }
}