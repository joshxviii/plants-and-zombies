package joshxviii.plantz

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry
import net.minecraft.client.particle.CritParticle

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		PazModels.registerAll()
		PazParticles.registerAll()
	}
}