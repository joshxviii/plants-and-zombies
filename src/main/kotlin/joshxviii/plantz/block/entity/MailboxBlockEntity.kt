package joshxviii.plantz.block.entity

import com.mojang.serialization.Codec
import joshxviii.plantz.MailboxData
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.block.MailboxBlock.Companion.FACING
import joshxviii.plantz.block.MailboxBlock.Companion.STATE
import joshxviii.plantz.block.MailboxState
import joshxviii.plantz.inventory.MailboxMenu
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Containers
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class MailboxBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState,
    val color : DyeColor = DyeColor.WHITE,
) : BaseContainerBlockEntity(PazBlocks.MAILBOX_ENTITY, worldPosition, blockState), ExtendedMenuProvider<MailboxData> {
    private var name: Component? = null
    private var ejectTimer: Int = 0
    private var tickCount : Int = 0

    companion object {
        val DEFAULT_NAME = Component.translatable("item.plantz.mailbox");
    }

    init {

    }

    private val inventory = SimpleContainer(5)
    override fun getContainerSize(): Int = 5

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        tickCount++

        if (level.isClientSide && tickCount % 25 == 0) {
            tickCount += level.random.nextInt(3)
            if (blockState.getValue(STATE) == MailboxState.HAS_MAIL)
            level.addParticle(PazServerParticles.NOTIFY, pos.x+0.5, pos.y+0.8, pos.z+0.5, 0.0, 0.0, 0.0)
            return
        }

        if (state.getValue(STATE) == MailboxState.EJECTING) {
            if (ejectTimer > 0) {
                ejectTimer--
            } else {
                updateMailboxState(MailboxState.INACTIVE)
                ejectTimer = 0
                setChanged()
                playSound(SoundEvents.VAULT_CLOSE_SHUTTER, 1.7f)
            }
        }
    }

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu = MailboxMenu(containerId, inventory, blockPos)
    override fun getScreenOpeningData(player: ServerPlayer): MailboxData = MailboxData(blockPos)

    override fun setLevel(level: Level) {
        super.setLevel(level)
        MailboxManager.registerMailbox(level, worldPosition, this)
    }

    override fun setRemoved() {
        super.setRemoved()
        MailboxManager.unregisterMailbox(level!!, worldPosition)
    }

    fun tryToGetMail(): Boolean {
        val currentState = blockState.getValue(STATE)
        return when (currentState) {
            MailboxState.HAS_MAIL -> {
                val dropPos = blockState.getValue(FACING).unitVec3.scale(0.75).add(blockPos.center)
                items.forEach {
                    Containers.dropItemStack(level!!, dropPos.x, dropPos.y, dropPos.z, it)
                }
                playSound(SoundEvents.VAULT_EJECT_ITEM)
                updateMailboxState(MailboxState.EJECTING)
                ejectTimer = 25
                setChanged()
                true
            }
            else -> false
        }
    }

    fun updateMailboxState(newState: MailboxState) {
        level!!.setBlock(blockPos, blockState.setValue(STATE, newState), 3)
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.storeNullable<Component>("CustomName", ComponentSerialization.CODEC, this.name)
        output.store("EjectTimer", Codec.INT, ejectTimer)
        ContainerHelper.saveAllItems(output, inventory.items)
    }
    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        this.name = parseCustomNameSafe(input, "CustomName")
        ejectTimer = input.getInt("EjectTimer").get()
        ContainerHelper.loadAllItems(input, inventory.items)
    }

    override fun getName(): Component = this.name?: blockState.block.name
    override fun getDisplayName(): Component = this.name?: blockState.block.name
    override fun getDefaultName(): Component = DEFAULT_NAME

    override fun getItems(): NonNullList<ItemStack> = inventory.items
    override fun setItems(items: NonNullList<ItemStack>) {}

    override fun canPlaceItem(slot: Int, itemStack: ItemStack): Boolean {
        return if (blockState.getValue(STATE) == MailboxState.EJECTING) false
        else super.canPlaceItem(slot, itemStack)
    }
    override fun canTakeItem(into: Container, slot: Int, itemStack: ItemStack): Boolean {
        return if (blockState.getValue(STATE) == MailboxState.EJECTING) false
        else super.canTakeItem(into, slot, itemStack)
    }

    override fun setChanged() {
        super.setChanged()
        if (blockState.getValue(STATE) == MailboxState.EJECTING) return
        if (isEmpty) updateMailboxState(MailboxState.INACTIVE)
        else updateMailboxState(MailboxState.HAS_MAIL)
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }
    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return this.saveWithoutMetadata(registries)
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
    private fun playSound(event: SoundEvent, pitch: Float = 0.9f) {
        val direction = blockState.getValue(FACING).unitVec3i
        val x = worldPosition.x + 0.5 + direction.x / 2.0
        val y = worldPosition.y + 0.5 + direction.y / 2.0
        val z = worldPosition.z + 0.5 + direction.z / 2.0
        level!!.playSound(
            null, x, y, z, event, SoundSource.BLOCKS, 0.5f, level!!.getRandom().nextFloat() * 0.1f + pitch
        )
    }
}