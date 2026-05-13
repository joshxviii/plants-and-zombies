package joshxviii.plantz.particles

import com.mojang.math.Axis
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SingleQuadParticle
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.state.level.QuadParticleRenderState
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.Vector


class ElectrifiedParticle private constructor(
    world: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    private val sprites: SpriteSet
) : SingleQuadParticle(world, x, y, z, sprites.first()) {
    init {
        this.lifetime = 6 + this.random.nextInt(3)
        this.setParticleSpeed(0.0, 0.0, 0.0)
        this.scale(1.25f)
        oRoll = random.nextFloat() * Mth.TWO_PI
    }

    override fun extract(particleTypeRenderState: QuadParticleRenderState, camera: Camera, partialTickTime: Float) {
        val quaternion = Quaternionf()
        facingCameraMode.setRotation(quaternion, camera, partialTickTime)
        quaternion.rotateZ(oRoll)
        this.extractRotatedQuad(particleTypeRenderState, camera, quaternion, partialTickTime)
    }

    override fun tick() {
        if (this.age++ >= this.lifetime) {
            this.remove()
        } else {
            sprite = sprites.get(random)
        }
    }

    override fun getLayer(): Layer {
        return Layer.TRANSLUCENT
    }

    override fun getLightCoords(a: Float): Int {
        return LightCoordsUtil.addSmoothBlockEmission(super.getLightCoords(a), 1.0f)
    }

    class Provider(val sprites: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            parameters: SimpleParticleType,
            world: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            velocityX: Double,
            velocityY: Double,
            velocityZ: Double,
            random: RandomSource
        ): Particle {
            return ElectrifiedParticle(world, x, y, z, sprites)
        }
    }
}