package joshxviii.plantz

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		PazModels.registerAll()
		PazParticles.registerAll()
		PazScreens.registerAll()
		PazClientNetwork.initialize()
	}


}