package joshxviii.plantz

import joshxviii.plantz.block.entity.MailboxManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		PazModels.registerAll()
		PazParticles.registerAll()
		PazScreens.registerAll()
		PazClientNetwork.initialize()

		ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register { minecraft, level ->
			MailboxManager.clearMailboxes()
		}
	}
}