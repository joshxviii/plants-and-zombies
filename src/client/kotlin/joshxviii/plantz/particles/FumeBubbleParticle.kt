package joshxviii.plantz.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource
import kotlin.math.pow

class FumeBubbleParticle private constructor(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xa: Double,
    ya: Double,
    za: Double,
    private val sprites: SpriteSet
) : SingleQuadParticle(level, x, y, z, sprites.first()) {
    init {
        lifetime = 8
        gravity = 0.008f
        xd = xa
        yd = ya
        zd = za
        setSpriteFromAge(sprites)
    }

    override fun tick() {
        xo = x
        yo = y
        zo = z
        if (age++ >= lifetime) remove()
        else {
            yd = yd - gravity
            move(xd, yd, zd)

            updateEasedSprite()
        }
    }

    public override fun getLayer(): Layer {
        return Layer.OPAQUE
    }

    private fun updateEasedSprite() {
        if (removed) return

        val progress = age.toFloat() / lifetime.toFloat()
        val eased = progress.pow(3.7f)// set easeIn here

        val frameIndex = (eased * (lifetime - 1)).toInt().coerceIn(0, lifetime - 1)
        setSprite(sprites.get(frameIndex, lifetime))
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
            return FumeBubbleParticle(level, x, y, z, xAux, yAux, zAux, sprites)
        }
    }
}