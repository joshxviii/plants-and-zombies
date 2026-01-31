package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.MailboxBlock.Companion.FACING
import joshxviii.plantz.block.MailboxBlock.Companion.STATE
import joshxviii.plantz.block.MailboxState
import joshxviii.plantz.inventory.MailboxMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.Vec3i
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.MenuProvider
import net.minecraft.world.Nameable
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class MailboxBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState,
    val color : DyeColor = DyeColor.WHITE,
) : BlockEntity(PazBlocks.MAILBOX_ENTITY, worldPosition, blockState), MenuProvider, Container, Nameable {

    private var name: Component? = null

    companion object {
        val DEFAULT_NAME = Component.translatable("item.plantz.mailbox");
    }

    init {

    }

    override fun createMenu(
        containerId: Int,
        inventory: Inventory,
        player: Player
    ): AbstractContainerMenu {
        return MailboxMenu(containerId, inventory)
    }

    override fun setLevel(level: Level) {
        super.setLevel(level)
        MailboxManager.registerMailbox(level, worldPosition, this)
    }

    override fun setRemoved() {
        super.setRemoved()
        MailboxManager.unregisterMailbox(level!!, worldPosition)
    }

    fun tryToGetMail() {
        if (blockState.getValue(STATE)== MailboxState.INACTIVE) {
            updateMailboxState(blockState, MailboxState.HAS_MAIL)
        }
        else if (blockState.getValue(STATE)== MailboxState.HAS_MAIL) {
            updateMailboxState(blockState, MailboxState.EJECTING)
        }
        else if (blockState.getValue(STATE)== MailboxState.EJECTING) {
            updateMailboxState(blockState, MailboxState.INACTIVE)
        }
    }

    private fun updateMailboxState(state: BlockState, newState: MailboxState) {
        level!!.setBlock(blockPos, state.setValue(STATE, newState), 3)
    }

    private fun playSound(state: BlockState, event: SoundEvent) {
        val direction = (state.getValue(FACING) as Direction).unitVec3i
        val x = worldPosition.x + 0.5 + direction.x / 2.0
        val y = worldPosition.y + 0.5 + direction.y / 2.0
        val z = worldPosition.z + 0.5 + direction.z / 2.0
        level!!.playSound(
            null, x, y, z, event, SoundSource.BLOCKS, 0.5f, level!!.getRandom().nextFloat() * 0.1f + 0.9f
        )
    }

    override fun getContainerSize(): Int = 5

    override fun isEmpty(): Boolean {
        return true
    }

    override fun getItem(slot: Int): ItemStack {
        return Items.AIR.defaultInstance
    }

    override fun removeItem(slot: Int, count: Int): ItemStack {
        return Items.AIR.defaultInstance
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return Items.AIR.defaultInstance
    }

    override fun setItem(slot: Int, itemStack: ItemStack) {
    }

    override fun stillValid(player: Player): Boolean {
        return true
    }

    override fun clearContent() {
    }

    override fun getDisplayName(): Component {
        return this.name?: blockState.block.name
    }
    override fun getName(): Component {
        return this.name?: blockState.block.name
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.storeNullable<Component>("CustomName", ComponentSerialization.CODEC, this.name)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        this.name = parseCustomNameSafe(input, "CustomName")
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return this.saveWithoutMetadata(registries)
    }

    fun getItem(): ItemStack {
        val block = PazBlocks.mailboxByColor[color]?: return ItemStack.EMPTY
        val itemStack = ItemStack(block)
        itemStack.applyComponents(collectComponents())
        return itemStack
    }

    override fun applyImplicitComponents(components: DataComponentGetter) {
        super.applyImplicitComponents(components)
        this.name = components.get<Component>(DataComponents.CUSTOM_NAME)
    }

    override fun collectImplicitComponents(components: DataComponentMap.Builder) {
        super.collectImplicitComponents(components)
        components.set<Component>(DataComponents.CUSTOM_NAME, this.name)
    }

    override fun removeComponentsFromTag(output: ValueOutput) {
        output.discard("CustomName")
    }
}