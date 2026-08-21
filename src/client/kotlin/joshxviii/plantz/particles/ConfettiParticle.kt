package joshxviii.plantz.particles

import joshxviii.plantz.PaintParticleOptions
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ExplodeParticle
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.state.level.QuadParticleRenderState
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import org.joml.Quaternionf
import kotlin.math.pow

class ConfettiParticle(
    level: ClientLevel, x: Double, y: Double, z: Double, xa: Double, ya: Double, za: Double, val sprites: SpriteSet
) : ExplodeParticle(level, x, y, z, xa, ya, za, sprites) {
    companion object {
        fun getColor(): Int {
            return listOf(
                0xFF254A,
                0xFFE804,
                0x0053FF,
                0x00FF00,
                0xE522FF,
                0xFF8120,
            ).random()
        }
    }

    var phaseOffset = 0f
    var frequency = 1f

    init {
        gravity = random.nextFloat() * 0.3f + 0.15f
        lifetime = 64 + random.nextInt(16)
        phaseOffset = random.nextFloat() * Mth.TWO_PI
        frequency = 1.5f
    }

    override fun tick() {
        super.tick()
        val progress = age.toFloat() / lifetime.toFloat()
        val eased = progress.pow(3.7f)
        alpha = 1f - eased
        this.yd *= 0.5f
        setSpriteFromCycle()
    }

    fun setSpriteFromCycle() {
        val progress = age.toDouble() / lifetime.toDouble()
        val cycle = Mth.cos(frequency * progress * Mth.TWO_PI + phaseOffset) * lifetime*.5 + lifetime*.5
        val frame = Mth.floor(cycle).coerceIn(0, lifetime-1)
        setSprite(sprites.get(frame, lifetime))
    }

    override fun getLayer(): Layer {
        return Layer.TRANSLUCENT
    }

    override fun getQuadSize(a: Float): Float {
        return 0.15f
    }

    override fun extractRotatedQuad(particleTypeRenderState: QuadParticleRenderState, rotation: Quaternionf, x: Float, y: Float, z: Float, partialTickTime: Float) {
        super.extractRotatedQuad(particleTypeRenderState, rotation, x, y+.05f, z, partialTickTime)
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
            val color = ARGB.vector3fFromRGB24(getColor())
            val particle = ConfettiParticle(level, x, y, z, xAux, yAux, zAux, sprites)
            particle.setColor(color.x, color.y, color.z)
            return particle
        }
    }
}