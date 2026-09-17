package joshxviii.plantz.block.entity

import joshxviii.plantz.MailCollectionBoxData
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.MailboxBlock
import joshxviii.plantz.block.MailboxState
import joshxviii.plantz.inventory.MailCollectionBoxMenu
import joshxviii.plantz.networking.MailboxSendResult
import joshxviii.plantz.networking.SendMailRequestPayload.Companion.trySendStack
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.ContainerHelper
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrNull


class MailCollectionBoxEntity(
    worldPosition: BlockPos,
    blockState: BlockState,
): BaseContainerBlockEntity(PazBlocks.MAIL_COLLECTION_BOX_ENTITY, worldPosition, blockState), ExtendedMenuProvider<MailCollectionBoxData> {

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: MailCollectionBoxEntity) {

        }

        const val INVENTORY_SIZE = 10
    }

    private val inventory = SimpleContainer(INVENTORY_SIZE)
    var selectedMailbox: BlockPos? = null
    var wasPowered: Boolean = false

    override fun getContainerSize(): Int = INVENTORY_SIZE

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveWithFullMetadata(registries)
    }

    fun trySendMail() {
        val level = level as? ServerLevel ?: return
        val targetPos = selectedMailbox ?: return

        var slotIndex = -1
        var stack = ItemStack.EMPTY
        for (i in 0 until containerSize) {
            val candidate = getItem(i)
            if (!candidate.isEmpty) {
                slotIndex = i
                stack = candidate
                break
            }
        }
        if (slotIndex == -1 || stack.isEmpty) return

        val targetBE = level.getBlockEntity(targetPos) as? MailboxBlockEntity
        val result = trySendStack(level, stack, targetPos)

        when (result) {
            MailboxSendResult.SUCCESS -> {
                setItem(slotIndex, ItemStack.EMPTY)
                setChanged()
                targetBE?.setChanged()
                targetBE?.updateMailboxState(MailboxState.HAS_MAIL)
                playSound(SoundEvents.UI_LOOM_SELECT_PATTERN, 0.3f, 1.2f)
            }
            MailboxSendResult.DISCARDED -> {
                selectedMailbox = null
                setChanged()
            }
            else -> {}
        }

        if (result != MailboxSendResult.SUCCESS) playSound(SoundEvents.BARREL_CLOSE, 0.3f, 1.2f)
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.storeNullable("SelectedMailbox", BlockPos.CODEC, selectedMailbox)
        ContainerHelper.saveAllItems(output, inventory.items)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        selectedMailbox = input.read("SelectedMailbox", BlockPos.CODEC).getOrNull()
        ContainerHelper.loadAllItems(input, inventory.items)
    }

    override fun getDefaultName(): Component = PazBlocks.MAIL_COLLECTION_BOX.name

    override fun getItems(): NonNullList<ItemStack> = inventory.items
    override fun setItems(items: NonNullList<ItemStack>) {}

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu = MailCollectionBoxMenu(containerId, inventory, MailCollectionBoxData(blockPos, name, selectedMailbox), this)
    override fun getScreenOpeningData(player: ServerPlayer): MailCollectionBoxData = MailCollectionBoxData(blockPos, name, selectedMailbox)

    fun playSound(event: SoundEvent, volume: Float = 0.5f, pitch: Float = 0.9f) {
        val direction = blockState.getValue(MailboxBlock.FACING).unitVec3i
        val x = worldPosition.x + 0.5 + direction.x / 2.0
        val y = worldPosition.y + 0.5 + direction.y / 2.0
        val z = worldPosition.z + 0.5 + direction.z / 2.0
        level!!.playSound(
            null, x, y, z, event, SoundSource.BLOCKS, volume, level!!.getRandom().nextFloat() * 0.1f + pitch
        )
    }
}