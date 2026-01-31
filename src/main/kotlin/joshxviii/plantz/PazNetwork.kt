package joshxviii.plantz

import joshxviii.plantz.networking.SendMailPayload
import joshxviii.plantz.networking.SendMailPayload.Companion.handleSendMailPacket
import joshxviii.plantz.networking.SendMailResponsePayload
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object PazNetwork {

    fun initialize() {
        // Register payloads
        PayloadTypeRegistry.clientboundPlay().register(SendMailPayload.ID, SendMailPayload.STREAM_CODEC)
        PayloadTypeRegistry.serverboundPlay().register(SendMailPayload.ID, SendMailPayload.STREAM_CODEC)

        PayloadTypeRegistry.clientboundPlay().register(SendMailResponsePayload.ID, SendMailResponsePayload.STREAM_CODEC)

        // Register server receiver
        ServerPlayNetworking.registerGlobalReceiver(SendMailPayload.ID, ::handleSendMailPacket)
    }
}