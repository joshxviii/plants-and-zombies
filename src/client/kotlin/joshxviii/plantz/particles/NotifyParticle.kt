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
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import kotlin.math.pow

class NotifyParticle private constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, xd: Double, yd: Double, zd: Double, sprite: TextureAtlasSprite
) : SingleQuadParticle(level, x, y, z, xd, yd, zd, sprite) {

    init {
        lifetime = 5
        gravity = 0f

        quadSize = 0.1f
        hasPhysics = false

        this.xd = xd * 0.02 + (random.nextFloat() - 0.5f) * 0.03
        this.yd = 0.12 + (random.nextFloat() - 0.5f) * 0.04
        this.zd = zd * 0.02 + (random.nextFloat() - 0.5f) * 0.03
    }

    override fun tick() {
        xo = x
        yo = y
        zo = z

        if (age++ >= lifetime) {
            remove()
            return
        }

        move(xd, yd, zd)

        xd *= 0.5f
        yd *= 0.5f
        zd *= 0.5f

        val progress = age*1.0 / lifetime
        //quadSize = Mth.lerp(progress, 0.4, 1.0).toFloat()  // Grow fast to full size
        //quadSize = ((age + a) / (halfLife)).pow(0.1f)
    }

    override fun getLayer(): Layer = Layer.OPAQUE  // Or TRANSLUCENT if you want additive glow

    // Optional: brighter/overlay blend if using translucent layer
    // override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

    override fun getQuadSize(a: Float): Float {
        val s = ((age + a) / lifetime).pow(0.2f)
        return this.quadSize * (s) + 0.1f
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