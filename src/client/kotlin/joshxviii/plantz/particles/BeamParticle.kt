package joshxviii.plantz.particles
import joshxviii.plantz.BeamParticleOptions
import joshxviii.plantz.PazParticles
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3

class BeamParticle private constructor(
    val world: ClientLevel,
    x: Double, y: Double, z: Double,
    val targetPos: Vec3,
    var width: Float,
    val color: Int,
    val lifeTime: Int = 1
) : Particle(world, x, y, z) {
    var startPos: Vec3 = Vec3(x, y, z)
    val particleAge: Int
        get() = (age)
    var alpha: Float = 1.0f
    var gameTime: Long = 0

    init {
        lifetime = lifeTime
        hasPhysics = false
    }

    override fun tick() {
        super.tick()
        gameTime = world.gameTime
    }

    override fun getGroup(): ParticleRenderType = PazParticles.BEAM

    override fun getLightCoords(a: Float): Int = LightCoordsUtil.addSmoothBlockEmission(super.getLightCoords(a), 1.0f)

    class Provider : ParticleProvider<BeamParticleOptions> {
        override fun createParticle(
            options: BeamParticleOptions,
            level: ClientLevel,
            x: Double, y: Double, z: Double,
            vx: Double, vy: Double, vz: Double,
            random: RandomSource
        ): Particle {
            return BeamParticle(
                level, x, y, z, options.targetPos, options.width, options.color, options.lifeTime
            )
        }
    }
}