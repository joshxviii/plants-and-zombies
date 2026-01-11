package joshxviii.plantz.particles

import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.state.QuadParticleRenderState
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import org.joml.Quaternionf
import kotlin.math.max

class PeaParticle private constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, xa: Double, ya: Double, za: Double, sprite: TextureAtlasSprite
) : SingleQuadParticle(level, x, y, z, 0.0, 0.0, 0.0, sprite) {
    init {
        friction = 0.7f
        gravity = 0.5f
        xd *= 0.1
        yd *= 0.1
        zd *= 0.1
        xd += xa * 0.4
        yd += ya * 0.4
        zd += za * 0.4
        val col = random.nextFloat() * 0.3f + 0.6f
        rCol = col
        gCol = col
        bCol = col
        quadSize *= 0.75f
        lifetime = max((6.0 / (random.nextFloat() * 0.8 + 0.9)).toInt(), 1)
        hasPhysics = true
        roll = random.nextFloat() * (Mth.TWO_PI)
        oRoll = roll
        tick()
    }

    override fun getQuadSize(a: Float): Float {
        return quadSize * Mth.clamp((age + a) / lifetime * 32.0f, 0.0f, 1.0f)
    }

    public override fun getLayer(): Layer =Layer.OPAQUE

    class Provider(private val sprite: SpriteSet) : ParticleProvider<SimpleParticleType> {
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
            return PeaParticle(level, x, y, z, xAux, yAux, zAux, sprite.get(random))
        }
    }
}