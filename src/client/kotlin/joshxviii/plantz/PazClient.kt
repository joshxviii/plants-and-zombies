package joshxviii.plantz

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.renderer.entity.EntityRenderers

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		PlantRenderers.registerAll()
	}
}