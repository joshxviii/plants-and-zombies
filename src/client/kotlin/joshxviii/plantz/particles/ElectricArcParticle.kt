
import joshxviii.plantz.ElectricArcParticleOptions
import joshxviii.plantz.particles.ElectricArcParticleGroup
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3

class ElectricArcParticle private constructor(
    world: ClientLevel,
    x: Double, y: Double, z: Double,
    val targetPos: Vec3,
    var thickness: Float,
    val color: Int,
) : Particle(world, x, y, z) {
    var startPos: Vec3 = Vec3(x, y, z)
    val particleAge: Int
        get() = (age)
    var alpha: Float = 1.0f

    init {
        lifetime = 6 + world.random.nextInt(4)
        hasPhysics = false
    }

    override fun tick() {
        super.tick()
        // Fade out
        thickness = (1.0f - age.toFloat() / lifetime) * 0.9f
        alpha = (1.0f - age.toFloat()+0.5f / lifetime) * 0.9f
    }

    override fun getGroup(): ParticleRenderType = ElectricArcParticleGroup.ELECTRIC_ARC_GROUP

    override fun getLightCoords(a: Float): Int = LightCoordsUtil.addSmoothBlockEmission(super.getLightCoords(a), 1.0f)

    class Provider : ParticleProvider<ElectricArcParticleOptions> {
        override fun createParticle(
            options: ElectricArcParticleOptions,
            level: ClientLevel,
            x: Double, y: Double, z: Double,
            vx: Double, vy: Double, vz: Double,
            random: RandomSource
        ): Particle {
            return ElectricArcParticle(
                level, x, y, z, options.targetPos, options.thickness, options.color
            )
        }
    }
}