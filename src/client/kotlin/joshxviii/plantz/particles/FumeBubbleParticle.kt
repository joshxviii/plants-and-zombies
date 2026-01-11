package joshxviii.plantz.particles

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.RandomSource

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
        this.lifetime = 4
        this.gravity = 0.008f
        this.xd = xa
        this.yd = ya
        this.zd = za
        this.setSpriteFromAge(sprites)
    }

    override fun tick() {
        this.xo = this.x
        this.yo = this.y
        this.zo = this.z
        if (this.age++ >= this.lifetime) {
            this.remove()
        } else {
            this.yd = this.yd - this.gravity
            this.move(this.xd, this.yd, this.zd)
            this.setSpriteFromAge(this.sprites)
        }
    }

    public override fun getLayer(): Layer {
        return Layer.OPAQUE
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
            return FumeBubbleParticle(level, x, y, z, xAux, yAux, zAux, this.sprites)
        }
    }
}