package joshxviii.plantz.particles

import joshxviii.plantz.NukeWaveParticleOptions
import joshxviii.plantz.PaintParticleOptions
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.state.level.QuadParticleRenderState
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import org.joml.Quaternionf
import kotlin.math.floor


class NukeWaveParticle(
    level: ClientLevel, x: Double, y: Double, z: Double, xa: Double, ya: Double, za: Double,
    private val scale: Float,
    private val sprites: SpriteSet
) : SingleQuadParticle(level, x, y, z, 0.0, 0.0, 0.0, sprites.first()) {

    init {
        this.quadSize = scale
        this.lifetime = (10 + (floor(this.quadSize / 5))).toInt()
        this.setParticleSpeed(xa, ya, za)
    }

    override fun tick() {
        super.tick()
        this.setSpriteFromAge(sprites);
    }

    override fun extract(particleTypeRenderState: QuadParticleRenderState, camera: Camera, partialTickTime: Float) {
        val quaternion = Quaternionf()
        quaternion.rotationX(Mth.HALF_PI)
        this.extractRotatedQuad(particleTypeRenderState, camera, quaternion, partialTickTime)
        quaternion.rotateYXZ(Mth.PI, 0f, 0f)
        this.extractRotatedQuad(particleTypeRenderState, camera, quaternion, partialTickTime)
    }

    public override fun getLayer(): Layer = Layer.TRANSLUCENT

    override fun getLightCoords(tint: Float): Int = 15728880

    class Provider(private val sprite: SpriteSet) : ParticleProvider<NukeWaveParticleOptions> {
        override fun createParticle(
            options: NukeWaveParticleOptions,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xAux: Double,
            yAux: Double,
            zAux: Double,
            random: RandomSource
        ): Particle {
            val particle = NukeWaveParticle(level, x, y, z, xAux, yAux, zAux, options.scale, sprite)
            val color = options.getColor()
            particle.setColor(color.x, color.y, color.z)
            return particle
        }
    }
}