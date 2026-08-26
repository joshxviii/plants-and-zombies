package joshxviii.plantz

import joshxviii.plantz.PazNetwork.ZombieRaidClientCache
import joshxviii.plantz.inventory.MailboxMenu
import joshxviii.plantz.networking.MailboxListResponsePayload
import joshxviii.plantz.networking.SendMailResponsePayload
import joshxviii.plantz.networking.ServerConfigResponsePayload
import joshxviii.plantz.networking.ZombieRaidResponsePayload
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft


object PazClientNetwork {
    fun initialize() {
        ZombieRaidClientCache.clear()

        ClientTickEvents.START_CLIENT_TICK.register { client ->
            RaidMusicManager.tick()
        }

        ClientPlayNetworking.registerGlobalReceiver(ZombieRaidResponsePayload.ID) { payload, context ->
            if (ZombieRaidClientCache.get(payload.data.id) == null) RaidMusicManager.start()
            ZombieRaidClientCache.put(payload.data)
            if (payload.terminate) ZombieRaidClientCache.remove(payload.data.id)
        }

        ClientPlayNetworking.registerGlobalReceiver(SendMailResponsePayload.ID) { payload, context ->
            context.client().execute {
                val mc = context.client()
                val player = mc.player ?: return@execute
                val menu = player.containerMenu as? MailboxMenu ?: return@execute
                menu.responseMessage = payload.message
                menu.responseTimeout = 30
            }
        }

        ClientPlayNetworking.registerGlobalReceiver(MailboxListResponsePayload.ID) { payload, context ->
            context.client().execute {
                val player = context.player()
                val menu = player.containerMenu as? MailboxMenu ?: return@execute

                // Rebuild list from positions
                menu.availableMailboxes = payload.mailboxes
                menu.updateFilteredMailboxes()
            }

        }

        ClientPlayNetworking.registerGlobalReceiver(ServerConfigResponsePayload.ID) { payload, context ->
            context.client().execute {
                try {
                    PazConfig.server = PazConfig.GSON.fromJson(payload.json, ServerConfig::class.java)
                    PazMain.LOGGER.info("Server config synced!")
                } catch (e: Exception) {
                    PazMain.LOGGER.error("Failed to parse server config", e)
                }
            }
        }

    }
}