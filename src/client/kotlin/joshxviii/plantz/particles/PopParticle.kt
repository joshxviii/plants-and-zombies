package joshxviii.plantz.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.RandomSource

class PopParticle private constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, xd: Double, yd: Double, zd: Double,
    private val sprites: SpriteSet
) : SingleQuadParticle(level, x, y, z, xd, yd, zd, sprites.first()) {

    init {
        lifetime = 2
        gravity = 0f
        quadSize = 0.75f
        hasPhysics = false
    }

    override fun tick() {
        xo = x
        yo = y
        zo = z

        if (age++ >= lifetime) {
            remove()
            return
        }

        setSpriteFromAge(sprites)
    }

    override fun getLayer(): Layer = Layer.OPAQUE

    override fun getQuadSize(a: Float): Float {
        return quadSize
    }

    public override fun getLightCoords(a: Float): Int {
        return LightCoordsUtil.addSmoothBlockEmission(super.getLightCoords(a), 1.0f)
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
            return PopParticle(level, x, y, z, xAux, yAux, zAux, sprites)
        }
    }
}