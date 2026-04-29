package joshxviii.plantz.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.RisingParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.RandomSource

class EnergizedParticle private constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, xd: Double, yd: Double, zd: Double,
    private val sprites: SpriteSet
) : RisingParticle(level, x, y, z, xd, yd, zd, sprites.first()) {

    init {
        lifetime = 8
        setSpriteFromAge(sprites)
    }

    override fun tick() {
        xo = x
        yo = y
        zo = z
        if (age++ >= lifetime) remove()
        else {
            move(xd, yd*0.1+0.04, zd)

            setSpriteFromAge(sprites)
        }
    }

    public override fun getLayer(): Layer {
        return Layer.OPAQUE
    }

    override fun move(xa: Double, ya: Double, za: Double) {
        this.boundingBox = this.boundingBox.move(xa, ya, za)
        this.setLocationFromBoundingbox()
    }

    override fun getQuadSize(a: Float): Float {
        val s = 1.0f - (this.age + a) / this.lifetime
        return this.quadSize * (0.5f + s * s * 0.1f)
    }

    public override fun getLightCoords(a: Float): Int {
        return LightCoordsUtil.addSmoothBlockEmission(super.getLightCoords(a), 1.0f - (this.age + a) / this.lifetime)
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
            return EnergizedParticle(level, x, y, z, xAux, yAux, zAux, sprites)
        }
    }
}