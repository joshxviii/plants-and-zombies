package joshxviii.plantz

import joshxviii.plantz.inventory.MailCollectionBoxMenu
import joshxviii.plantz.inventory.MailboxMenu
import joshxviii.plantz.inventory.TimeMachineMenu
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import java.util.Optional
import kotlin.jvm.optionals.getOrNull


object PazMenus {

    @JvmField val MAILBOX_MENU: ExtendedMenuType<MailboxMenu, MailboxData> = ExtendedMenuType(
        { containerId, inventory, data -> MailboxMenu(containerId, inventory, data) },
        MailboxData.STREAM_CODEC
    )

    @JvmField val MAIL_COLLECTION_BOX_MENU: ExtendedMenuType<MailCollectionBoxMenu, MailCollectionBoxData> = ExtendedMenuType(
        { containerId, inventory, data -> MailCollectionBoxMenu(containerId, inventory, data) },
        MailCollectionBoxData.STREAM_CODEC
    )

    @JvmField val TIME_MACHINE_MENU: ExtendedMenuType<TimeMachineMenu, TimeMachineData> = ExtendedMenuType(
        { containerId, inventory, data -> TimeMachineMenu(containerId, inventory, data.blockPos) },
        TimeMachineData.STREAM_CODEC
    )

    fun initialize() {
        Registry.register(BuiltInRegistries.MENU, pazResource("mailbox"), MAILBOX_MENU)
        Registry.register(BuiltInRegistries.MENU, pazResource("collection_box"), MAIL_COLLECTION_BOX_MENU)
        Registry.register(BuiltInRegistries.MENU, pazResource("time_machine"), TIME_MACHINE_MENU)
    }
}

@JvmRecord
data class TimeMachineData(val blockPos: BlockPos) {
    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, TimeMachineData> =
            StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                TimeMachineData::blockPos
            ) { blockPos: BlockPos -> TimeMachineData(blockPos) }
    }
}

@JvmRecord
data class MailboxData(val blockPos: BlockPos, val color: Int, val name: Component) {
    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MailboxData> =
            StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                MailboxData::blockPos,
                ByteBufCodecs.INT,
                MailboxData::color,
                ComponentSerialization.STREAM_CODEC,
                MailboxData::name,
                ::MailboxData
            )
    }
}

@JvmRecord
data class MailCollectionBoxData(val blockPos: BlockPos, val name: Component, val selectedMailbox: BlockPos?) {
    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MailCollectionBoxData> =
            StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                MailCollectionBoxData::blockPos,
                ComponentSerialization.STREAM_CODEC,
                MailCollectionBoxData::name,
                ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
                { Optional.ofNullable(it.selectedMailbox) },
                { pos, name, selectedMailbox -> MailCollectionBoxData(pos, name, selectedMailbox.getOrNull()) }
            )
    }
}