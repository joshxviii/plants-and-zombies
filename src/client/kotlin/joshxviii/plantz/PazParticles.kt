package joshxviii.plantz

import joshxviii.plantz.particles.EmberParticle
import joshxviii.plantz.particles.FumeBubbleParticle
import joshxviii.plantz.particles.PeaParticle
import joshxviii.plantz.particles.SporeParticle
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry
import net.minecraft.client.particle.CritParticle
import net.minecraft.client.particle.FlameParticle
import net.minecraft.client.particle.SingleQuadParticle

object PazParticles {

    fun registerAll() {
        val it = ParticleProviderRegistry.getInstance()
        it.register(PazServerParticles.PEA_HIT) { spriteSet ->
            PeaParticle.Provider(spriteSet)
        }

        it.register(PazServerParticles.ICE_PEA_HIT) { spriteSet ->
            PeaParticle.Provider(spriteSet)
        }

        it.register(PazServerParticles.FIRE_PEA_HIT) { spriteSet ->
            PeaParticle.Provider(spriteSet)
        }

        it.register(PazServerParticles.SPORE) { spriteSet ->
            SporeParticle.Provider(spriteSet)
        }

        it.register(PazServerParticles.SPORE_HIT) { spriteSet ->
            CritParticle.Provider(spriteSet)
        }

        it.register(PazServerParticles.FUME_BUBBLE) { spriteSet ->
            FumeBubbleParticle.Provider(spriteSet)
        }

        it.register(PazServerParticles.EMBER) { spriteSet ->
            EmberParticle.Provider(spriteSet)
        }
    }

}