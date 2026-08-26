package joshxviii.plantz

import joshxviii.plantz.networking.MailboxListResponsePayload
import joshxviii.plantz.networking.SendMailRequestPayload
import joshxviii.plantz.networking.SendMailRequestPayload.Companion.handleSendMailPacket
import joshxviii.plantz.networking.SendMailResponsePayload
import joshxviii.plantz.networking.ServerConfigResponsePayload
import joshxviii.plantz.networking.ZombieRaidClientData
import joshxviii.plantz.networking.ZombieRaidResponsePayload
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import java.util.UUID

object PazNetwork {

    object ZombieRaidClientCache {
        private val active = mutableMapOf<UUID, ZombieRaidClientData>()

        fun get() = active.values.firstOrNull()
        fun get(id: UUID) = active[id]
        fun put(data: ZombieRaidClientData) { active[data.id] = data }
        fun remove(id: UUID) { active.remove(id) }
        fun clear() { active.clear() }
    }

    fun initialize() {
        // Register payloads
        PayloadTypeRegistry.serverboundPlay().register(SendMailRequestPayload.ID, SendMailRequestPayload.STREAM_CODEC)

        PayloadTypeRegistry.clientboundPlay().register(SendMailRequestPayload.ID, SendMailRequestPayload.STREAM_CODEC)
        PayloadTypeRegistry.clientboundPlay().register(SendMailResponsePayload.ID, SendMailResponsePayload.STREAM_CODEC)
        PayloadTypeRegistry.clientboundPlay().register(MailboxListResponsePayload.ID, MailboxListResponsePayload.STREAM_CODEC)
        PayloadTypeRegistry.clientboundPlay().register(ServerConfigResponsePayload.ID, ServerConfigResponsePayload.STREAM_CODEC)
        PayloadTypeRegistry.clientboundPlay().register(ZombieRaidResponsePayload.ID, ZombieRaidResponsePayload.STREAM_CODEC)

        // Register server receiver
        ServerPlayNetworking.registerGlobalReceiver(SendMailRequestPayload.ID, ::handleSendMailPacket)
    }
}