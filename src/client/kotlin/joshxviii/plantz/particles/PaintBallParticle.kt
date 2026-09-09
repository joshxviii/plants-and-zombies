package joshxviii.plantz.particles

import joshxviii.plantz.PaintParticleOptions
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ExplodeParticle
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.util.RandomSource

class PaintBallParticle private constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, xa: Double, ya: Double, za: Double, sprites: SpriteSet
) : ExplodeParticle(level, x, y, z, xa, ya, za, sprites) {
    init {
       gravity = 0.5f
       lifetime = (2.0 / (random.nextFloat() * 0.8 + 0.2)).toInt()
       scale(0.5f)
    }

    class Provider(private val sprites: SpriteSet) : ParticleProvider<PaintParticleOptions> {
        override fun createParticle(
            options: PaintParticleOptions,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xAux: Double,
            yAux: Double,
            zAux: Double,
            random: RandomSource
        ): Particle {
            val color = options.getColor()
            val particle = PaintBallParticle(level, x, y, z, xAux, yAux, zAux, sprites)
            particle.setColor(color.x, color.y, color.z)
            return particle
        }
    }
}