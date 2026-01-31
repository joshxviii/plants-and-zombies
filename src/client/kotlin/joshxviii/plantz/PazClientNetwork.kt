package joshxviii.plantz

import joshxviii.plantz.inventory.MailboxMenu
import joshxviii.plantz.networking.SendMailResponsePayload
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.sounds.SoundEvents

object PazClientNetwork {

    fun initialize() {
        ClientPlayNetworking.registerGlobalReceiver(SendMailResponsePayload.ID) { payload, context ->
            context.client().execute {
                val mc = context.client()
                val player = mc.player ?: return@execute
                val menu = player.containerMenu as? MailboxMenu ?: return@execute

                player.playSound(SoundEvents.BARREL_CLOSE, 0.4f, 1.2f)
            }
        }

    }
}