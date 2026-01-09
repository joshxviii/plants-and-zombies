package joshxviii.plantz

import joshxviii.plantz.PazParticles.FIRE_PEA_HIT
import joshxviii.plantz.PazParticles.ICE_PEA_HIT
import joshxviii.plantz.PazParticles.PEA_HIT
import joshxviii.plantz.PazParticles.SPORE
import joshxviii.plantz.PazParticles.SPORE_HIT
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry
import net.minecraft.client.particle.CritParticle
import net.minecraft.client.particle.GustParticle

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		PazModels.registerAll()

		ParticleProviderRegistry.getInstance().register(PEA_HIT) { spriteSet ->
			CritParticle.Provider(spriteSet)
		}
		ParticleProviderRegistry.getInstance().register(ICE_PEA_HIT) { spriteSet ->
			CritParticle.Provider(spriteSet)
		}
		ParticleProviderRegistry.getInstance().register(FIRE_PEA_HIT) { spriteSet ->
			CritParticle.Provider(spriteSet)
		}

		ParticleProviderRegistry.getInstance().register(SPORE) { spriteSet ->
			GustParticle.Provider(spriteSet)
		}

		ParticleProviderRegistry.getInstance().register(SPORE_HIT) { spriteSet ->
			CritParticle.Provider(spriteSet)
		}
	}
}