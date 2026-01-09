package joshxviii.plantz.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ExplodeParticle
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource

class SporeParticle private constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, xa: Double, ya: Double, za: Double, sprites: SpriteSet
) : ExplodeParticle(level, x, y, z, xa, ya, za, sprites) {
    init {
        this.gravity = 0.5f
        this.lifetime = (2.0 / (this.random.nextFloat() * 0.8 + 0.2)).toInt()
        this.scale(0.5f)
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
            return SporeParticle(level, x, y, z, xAux, yAux, zAux, this.sprites)
        }
    }
}