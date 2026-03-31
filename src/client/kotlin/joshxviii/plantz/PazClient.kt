package joshxviii.plantz

import net.fabricmc.api.ClientModInitializer

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		PazModels.registerAll()
		PazParticles.registerAll()
		PazScreens.registerAll()
		PazRenderPipelines.initialize()
		PazClientNetwork.initialize()
	}
}