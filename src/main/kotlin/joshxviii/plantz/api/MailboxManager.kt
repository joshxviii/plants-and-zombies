package joshxviii.plantz.api

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.MailboxData
import joshxviii.plantz.block.MailboxState
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.pazResource
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.Locale
import java.util.function.Function

fun ServerLevel.getMailboxMailQueue(): MailboxMailQueue {
    return dataStorage.computeIfAbsent(MailboxMailQueue.TYPE)
}

class MailboxMailQueue private constructor(
    private val queuedMail: MutableList<QueuedMail> = mutableListOf()
) : SavedData() {

    init {
        setDirty()
    }

    fun queue(targetPos: BlockPos, stack: ItemStack) {
        if (stack.isEmpty) return
        queuedMail.add(QueuedMail(targetPos.immutable(), stack.copy()))
        setDirty()
    }

    fun discardFor(targetPos: BlockPos): Boolean {
        val removed = queuedMail.removeIf { it.targetPos == targetPos }
        if (removed) setDirty()
        return removed
    }

    fun deliverTo(target: MailboxBlockEntity): Boolean {
        var delivered = false
        val iterator = queuedMail.iterator()

        while (iterator.hasNext()) {
            val mail = iterator.next()
            if (mail.targetPos != target.blockPos) continue

            if (tryInsertIntoMailbox(target, mail.stack.copy())) {
                iterator.remove()
                delivered = true
            }
        }

        if (delivered) {
            target.setChanged()
            target.updateMailboxState(MailboxState.HAS_MAIL)
            setDirty()
        }

        return delivered
    }

    companion object {
        private val MAIL_QUEUE_FILE_ID = pazResource("mailbox_mail_queue")

        private val QUEUED_MAIL_CODEC: Codec<QueuedMail> = RecordCodecBuilder.create<QueuedMail>(
            Function { instance ->
                instance.group(
                    BlockPos.CODEC.fieldOf("target_pos").forGetter<QueuedMail>(QueuedMail::targetPos),
                    ItemStack.CODEC.fieldOf("stack").forGetter<QueuedMail>(QueuedMail::stack)
                ).apply<QueuedMail>(instance, ::QueuedMail)
            }
        )

        val CODEC: Codec<MailboxMailQueue> = RecordCodecBuilder.create<MailboxMailQueue>(
            Function { instance ->
                instance.group(
                    QUEUED_MAIL_CODEC
                        .listOf()
                        .optionalFieldOf("queued_mail", emptyList())
                        .forGetter<MailboxMailQueue> { it.queuedMail }
                ).apply<MailboxMailQueue>(instance) { mail -> MailboxMailQueue(mail.toMutableList()) }
            }
        )

        val TYPE: SavedDataType<MailboxMailQueue> =
            SavedDataType<MailboxMailQueue>(MAIL_QUEUE_FILE_ID, ::MailboxMailQueue, CODEC, DataFixTypes.LEVEL)

        fun tryInsertIntoMailbox(target: MailboxBlockEntity, stack: ItemStack): Boolean {
            if (stack.isEmpty) return false
            if (!hasSpaceFor(target, stack)) return false

            val inserted = stack.copy()
            for (i in 0 until target.containerSize) {
                val existing = target.getItem(i)

                if (existing.isEmpty) {
                    target.setItem(i, inserted.copy())
                    return true
                }

                if (ItemStack.isSameItemSameComponents(existing, inserted) && existing.count < existing.maxStackSize) {
                    val space = existing.maxStackSize - existing.count
                    val toAdd = minOf(space, inserted.count)
                    existing.grow(toAdd)
                    inserted.shrink(toAdd)
                    target.setItem(i, existing)

                    if (inserted.isEmpty) return true
                }
            }

            return false
        }

        private fun hasSpaceFor(target: MailboxBlockEntity, stack: ItemStack): Boolean {
            var space = 0
            for (i in 0 until target.containerSize) {
                val existing = target.getItem(i)
                space += when {
                    existing.isEmpty -> stack.maxStackSize
                    ItemStack.isSameItemSameComponents(existing, stack) -> existing.maxStackSize - existing.count
                    else -> 0
                }

                if (space >= stack.count) return true
            }

            return false
        }
    }

    private data class QueuedMail(val targetPos: BlockPos, val stack: ItemStack)
}

object MailboxManager {

    private val mailboxes: MutableMap<ResourceKey<Level>, MutableSet<BlockPos>> = mutableMapOf()

    fun clearMailboxes() {
        mailboxes.clear()
    }

    fun registerMailbox(level: Level, blockEntity: MailboxBlockEntity) {
        val levelKey = level.dimension()
        val pos = blockEntity.blockPos
        mailboxes.getOrPut(levelKey) { mutableSetOf() }.add(pos)
    }

    fun unregisterMailbox(level: Level, pos: BlockPos) {
        val levelKey = level.dimension()
        mailboxes[levelKey]?.remove(pos)
    }

    fun getMailboxesInLevel(level: Level): List<MailboxData> {
        val levelKey = level.dimension()

        return mailboxes[levelKey]?.mapNotNull { pos ->
            (level.getBlockEntity(pos) as? MailboxBlockEntity)?.asMailBoxData()
        } ?: emptyList()
    }

    fun searchMailboxes(mailboxes: List<MailboxData>, search: String): List<MailboxData> {
        if (search.isEmpty()) return mailboxes

        return mailboxes.filter { mailbox ->
            mailbox.name.string.lowercase(Locale.getDefault())
                .contains(search.lowercase(Locale.getDefault()))
        }
    }
}