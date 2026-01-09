package joshxviii.plantz.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource

class SporeParticle (
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    private val sprites: SpriteSet
) : SingleQuadParticle(level, x, y, z, sprites.first()) {
    init {
        this.setSpriteFromAge(sprites)
        this.lifetime = 12 + this.random.nextInt(4)
        this.quadSize = 1.0f
        this.setSize(1.0f, 1.0f)
    }

    public override fun getLayer(): Layer {
        return Layer.OPAQUE
    }

    public override fun getLightCoords(a: Float): Int {
        return 15728880
    }

    override fun tick() {
        if (this.age++ >= this.lifetime) {
            this.remove()
        } else {
            this.setSpriteFromAge(this.sprites)
        }
    }

    class Provider(private val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            options: SimpleParticleType,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xAux: Double,
            yAux: Double,
            zAux: Double,
            random: RandomSource
        ): Particle {
            val particle: Particle = SporeParticle(level, x, y, z, this.sprites)
            particle.scale(0.15f)
            return particle
        }
    }
}