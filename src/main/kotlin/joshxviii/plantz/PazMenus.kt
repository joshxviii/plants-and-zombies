package joshxviii.plantz

import joshxviii.plantz.inventory.MailboxMenu
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec


object PazMenus {

    @JvmField val MAILBOX_MENU: ExtendedMenuType<MailboxMenu, MailboxData> = ExtendedMenuType(
        { containerId, inventory, data ->
            MailboxMenu(containerId, inventory, data.blockPos)
        },
        MailboxData.STREAM_CODEC
    )

    fun initialize() {
        Registry.register(
            BuiltInRegistries.MENU,
            pazResource("mailbox"),
            MAILBOX_MENU
        )
    }
}

@JvmRecord
data class MailboxData(val blockPos: BlockPos) {
    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MailboxData> =
            StreamCodec.composite<RegistryFriendlyByteBuf, MailboxData, BlockPos>(
                BlockPos.STREAM_CODEC,
                MailboxData::blockPos
            ) { blockPos: BlockPos -> MailboxData(blockPos) }
    }
}