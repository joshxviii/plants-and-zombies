package joshxviii.plantz

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.renderer.entity.EntityRenderers

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		PlantRenderers.registerAll()
	}
}