package joshxviii.plantz.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
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
        lifetime = random.nextInt(14, 15)
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

        val a = 1f-(age.toFloat() / lifetime)
        //move(xd, (yd+0.0)*a, zd)

        xd *= 0.5f
        yd *= 0.5f
        zd *= 0.5f
    }

    override fun getLayer(): Layer = Layer.OPAQUE

    override fun getQuadSize(a: Float): Float {
        val halfLife = lifetime*.2f
        val s = if (age<halfLife)
            ((age + a) / (halfLife)).pow(1.0f)
        else
            1-((age + a - (halfLife)) / (lifetime-halfLife)).pow(20.0f)
        move(0.0, yd*1.5*if (age<halfLife) .2f else -.2f, 0.0)
        return this.quadSize * (s.coerceIn(0.0f, 1.0f)) * 2f
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
            return NotifyParticle(level, x, y, z, xAux, yAux, zAux, sprites.get(random))
        }
    }
}