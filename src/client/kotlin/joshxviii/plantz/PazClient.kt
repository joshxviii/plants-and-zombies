package joshxviii.plantz

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.renderer.item.ItemModels

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		PazConfig.load()
		PazModels.registerAll()
		PazParticles.registerAll()
		PazScreens.registerAll()
		PazClientNetwork.initialize()
		PazRenderPipelines.initialize()
	}
}