package joshxviii.plantz.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.RisingParticle
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.RandomSource
import kotlin.math.pow

class NotifyParticle private constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, xd: Double, yd: Double, zd: Double, sprite: TextureAtlasSprite
) : SingleQuadParticle(level, x, y, z, xd, yd, zd, sprite) {

    init {
        lifetime = 32
        gravity = -0.08f
    }

    override fun tick() {
        xo = x
        yo = y
        zo = z
        if (age++ >= lifetime) remove()
        else {
            move(xd, yd*30, zd)
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
        val halfLife = lifetime/2
        val s = if (age<halfLife)
                ((age + a) / (halfLife)).pow(0.1f)
        else
                1-((age + a - (halfLife)) / (lifetime-halfLife)).pow(1.0f)
        return this.quadSize * (s)
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
            return NotifyParticle(level, x, y, z, xAux, yAux, zAux, sprites.get(random))
        }
    }
}