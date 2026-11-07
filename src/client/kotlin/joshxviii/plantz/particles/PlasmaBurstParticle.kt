package joshxviii.plantz.particles

import joshxviii.plantz.NukeSmokeParticleOptions
import joshxviii.plantz.PlasmaBurstOptions
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import kotlin.math.floor

class PlasmaBurstParticle(
    level: ClientLevel, x: Double, y: Double, z: Double, xa: Double, ya: Double, za: Double,
    private val scale: Float,
    private val sprites: SpriteSet
) : SingleQuadParticle(level, x, y, z, 0.0, 0.0, 0.0, sprites.first()) {
    init {
        this.quadSize = scale / 2.0f
        this.lifetime = (6 + (floor(random.nextFloat()))).toInt()
        roll = random.nextFloat() * (Mth.TWO_PI)
        oRoll = roll
        this.setParticleSpeed(xa, ya, za)
    }

    override fun tick() {
        super.tick()
        this.alpha = 1.0f - (this.age) / this.lifetime.toFloat()
        this.setSpriteFromAge(sprites);
    }

    public override fun getLayer(): Layer = Layer.TRANSLUCENT

    override fun getLightCoords(a: Float): Int = LightCoordsUtil.addSmoothBlockEmission(super.getLightCoords(a), 1.0f)

    class Provider(private val sprite: SpriteSet) : ParticleProvider<PlasmaBurstOptions> {
        override fun createParticle(
            options: PlasmaBurstOptions,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xAux: Double,
            yAux: Double,
            zAux: Double,
            random: RandomSource
        ): Particle {
            val particle = PlasmaBurstParticle(level, x, y, z, xAux, yAux, zAux, options.scale, sprite)
            val color = options.getColor()
            particle.setColor(color.x, color.y, color.z)
            return particle
        }
    }
}