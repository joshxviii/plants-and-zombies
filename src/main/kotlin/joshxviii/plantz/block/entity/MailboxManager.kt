package joshxviii.plantz.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import java.util.Locale

object MailboxManager {

    private val mailboxes: MutableMap<ResourceKey<Level>, MutableSet<BlockPos>> = mutableMapOf()
    private val mailboxData: MutableMap<BlockPos, MailboxBlockEntity> = mutableMapOf()

    fun registerMailbox(level: Level, pos: BlockPos, blockEntity: MailboxBlockEntity) {
        val levelKey = level.dimension()
        mailboxes.getOrPut(levelKey) { mutableSetOf() }.add(pos)
        mailboxData[pos] = blockEntity
    }

    fun unregisterMailbox(level: Level, pos: BlockPos) {
        val levelKey = level.dimension()
        mailboxes[levelKey]?.remove(pos)
        mailboxData.remove(pos)
    }

    fun getMailboxesInLevel(level: Level): List<MailboxBlockEntity> {
        val levelKey = level.dimension()

        return mailboxes[levelKey]?.mapNotNull { pos ->
            mailboxData[pos] ?: level.getBlockEntity(pos) as? MailboxBlockEntity
        } ?: emptyList()
    }

    private fun createDisplayName(mailbox: MailboxBlockEntity, pos: BlockPos): MutableComponent {
        val customName = mailbox.name

        return if (customName != MailboxBlockEntity.DEFAULT_NAME) {
            customName.copy()
        } else {
            Component.translatable(
                "container.plantz.mailbox_coords",
                pos.x, pos.y, pos.z
            ).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(mailbox.color.textColor)))
        }
    }

    fun searchMailboxes(mailboxes: List<MailboxBlockEntity>, search: String): List<MailboxBlockEntity> {
        if (search.isEmpty()) return mailboxes

        return mailboxes.filter { mailbox ->
            mailbox.name.string.lowercase(Locale.getDefault())
                .contains(search.lowercase(Locale.getDefault()))
        }
    }
}