package joshxviii.plantz.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import kotlin.math.max

class PeaParticle private constructor(
    level: ClientLevel, x: Double, y: Double, z: Double, xa: Double, ya: Double, za: Double, sprite: TextureAtlasSprite
) : SingleQuadParticle(level, x, y, z, 0.0, 0.0, 0.0, sprite) {
    init {
        this.friction = 0.7f
        this.gravity = 0.5f
        this.xd *= 0.1
        this.yd *= 0.1
        this.zd *= 0.1
        this.xd += xa * 0.4
        this.yd += ya * 0.4
        this.zd += za * 0.4
        val col = this.random.nextFloat() * 0.3f + 0.6f
        this.rCol = col
        this.gCol = col
        this.bCol = col
        this.quadSize *= 0.75f
        this.lifetime = max((6.0 / (this.random.nextFloat() * 0.8 + 0.6)).toInt(), 1)
        this.hasPhysics = false
        this.tick()
    }

    override fun getQuadSize(a: Float): Float {
        return this.quadSize * Mth.clamp((this.age + a) / this.lifetime * 32.0f, 0.0f, 1.0f)
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
            return PeaParticle(level, x, y, z, xAux, yAux, zAux, this.sprite.get(random))
        }
    }
}