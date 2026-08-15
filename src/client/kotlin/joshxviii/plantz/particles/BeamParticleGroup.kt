package joshxviii.plantz.particles

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import joshxviii.plantz.pazResource
import net.minecraft.client.Camera
import net.minecraft.client.particle.*
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.rendertype.OutputTarget
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3
import kotlin.math.sin

class BeamParticleGroup(engine: ParticleEngine) : ParticleGroup<BeamParticle>(engine) {
    companion object {
        val OUTER_BEAM = RenderType.create(
            "outer_beam",
            RenderSetup.builder(RenderPipelines.BEACON_BEAM_TRANSLUCENT)
                .withTexture("Sampler0", pazResource("textures/particle/beam_outer.png"))
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .createRenderSetup()
        )
        val INNER_BEAM = RenderType.create(
            "beam",
            RenderSetup.builder(RenderPipelines.BEACON_BEAM_TRANSLUCENT)
                .withTexture("Sampler0", pazResource("textures/particle/beam.png"))
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .createRenderSetup()
        )
    }

    @JvmRecord
    data class BeamParticleRenderState(
        val startPos: Vec3,
        val targetPos: Vec3,
        val width: Float,
        val color: Int,
        val alpha: Float,
        val age: Int,
        val lifetime: Int,
        val gameTime: Float,
        val partialTicks: Float,
        val random: RandomSource
    ) {
        companion object {
            fun fromParticle(particle: BeamParticle, camera: Camera, partialTicks: Float): BeamParticleRenderState {
                val cameraPos = camera.position()

                return BeamParticleRenderState(
                    startPos = particle.startPos.subtract(cameraPos),
                    targetPos = Vec3(particle.targetPos.x, particle.targetPos.y, particle.targetPos.z).subtract(cameraPos),
                    width = particle.width,
                    color = particle.color,
                    alpha = particle.alpha,
                    age = particle.particleAge,
                    lifetime = particle.lifetime,
                    gameTime = particle.gameTime.toFloat(),
                    partialTicks = partialTicks,
                    random = RandomSource.create(particle.hashCode().toLong())
                )
            }
        }
    }

    override fun extractRenderState(
        frustum: Frustum,
        camera: Camera,
        partialTickTime: Float
    ): ParticleGroupRenderState {
        val states = particles
            .filter { it.isAlive }
            .map { BeamParticleRenderState.fromParticle(it, camera, partialTickTime) }

        return State(states)
    }

    @JvmRecord
    private data class State(val renderStates: List<BeamParticleRenderState>) : ParticleGroupRenderState {

        override fun submit(collector: SubmitNodeCollector, camera: CameraRenderState) {
            for (state in renderStates) {
                collector.submitCustomGeometry(PoseStack(), OUTER_BEAM) { _, buffer ->
                    renderBeam(buffer, state, true)
                }
                collector.submitCustomGeometry(PoseStack(), INNER_BEAM) { _, buffer ->
                    renderBeam(buffer, state)
                }
            }
        }

        private fun renderBeam(buffer: VertexConsumer, state: BeamParticleRenderState, outer: Boolean = false) {
            val waveCycle = sin((state.gameTime ) ) * 0.2f + 0.8f
            val ageFactor = 1f - (state.age.toFloat() / state.lifetime)
            val mainAlpha = waveCycle * ageFactor
            val width = state.width * waveCycle * ageFactor
            val scroll = (state.gameTime * .2f) % 2f

            if (outer) renderBeamCross(buffer, state.startPos, state.targetPos, width, state.color, mainAlpha, scroll)
            else renderBeamBox(buffer, state.startPos, state.targetPos, width, state.color, mainAlpha, scroll)
        }

        private fun renderBeamCross(buffer: VertexConsumer, from: Vec3, to: Vec3, width: Float, color: Int, alpha: Float, scroll: Float) {
            val r = ((color shr 16) and 0xFF) / 255f
            val g = ((color shr 8) and 0xFF) / 255f
            val b = (color and 0xFF) / 255f

            val dir = to.subtract(from)
            val length = dir.length().toFloat()
            if (length < 1.0E-4f) return

            val direction = dir.scale(1.0 / length)

            var right = direction.cross(Vec3(0.0, 1.0, 0.0))
            if (right.lengthSqr() < 1.0E-6) {
                right = direction.cross(Vec3(1.0, 0.0, 0.0))
            }
            right = right.normalize()
            val up = right.cross(direction).normalize()

            val hw = width * 1.0
            val v0 = scroll
            val v1 = scroll + length / width

            drawCrossPlane(buffer, from, to, right, hw, r, g, b, alpha, v0, v1)
            drawCrossPlane(buffer, from, to, up, hw, r, g, b, alpha, v0, v1)
        }

        private fun drawCrossPlane(buffer: VertexConsumer, from: Vec3, to: Vec3, axis: Vec3, hw: Double, r: Float, g: Float, b: Float, alpha: Float, v0: Float, v1: Float) {
            val p1 = from.add(axis.scale(hw))
            val p2 = from.subtract(axis.scale(hw))
            val p3 = to.subtract(axis.scale(hw))
            val p4 = to.add(axis.scale(hw))

            vertex(buffer, p1, r, g, b, alpha, 0f, v0)
            vertex(buffer, p2, r, g, b, alpha, 1f, v0)
            vertex(buffer, p3, r, g, b, alpha, 1f, v1)
            vertex(buffer, p4, r, g, b, alpha, 0f, v1)

            vertex(buffer, p1, r, g, b, alpha, 0f, v0)
            vertex(buffer, p4, r, g, b, alpha, 0f, v1)
            vertex(buffer, p3, r, g, b, alpha, 1f, v1)
            vertex(buffer, p2, r, g, b, alpha, 1f, v0)
        }

        private fun renderBeamBox(buffer: VertexConsumer, from: Vec3, to: Vec3, width: Float, color: Int, alpha: Float, scroll: Float) {
            val r = ((color shr 16) and 0xFF) / 255f
            val g = ((color shr 8) and 0xFF) / 255f
            val b = (color and 0xFF) / 255f

            val dir = to.subtract(from)
            val length = dir.length().toFloat()
            if (length < 1.0E-4) return

            val direction = dir.scale(1.0 / length)

            var right = direction.cross(Vec3(0.0, 1.0, 0.0))
            if (right.lengthSqr() < 1.0E-6) {
                right = direction.cross(Vec3(1.0, 0.0, 0.0))
            }
            right = right.normalize()
            val up = right.cross(direction).normalize()

            val hw = width * 0.5

            val c0 = right.scale( hw).add(up.scale( hw))
            val c1 = right.scale( hw).add(up.scale(-hw))
            val c2 = right.scale(-hw).add(up.scale(-hw))
            val c3 = right.scale(-hw).add(up.scale( hw))

            val corners = arrayOf(c0, c1, c2, c3)

            val v0 = scroll
            val v1 = scroll + (length) / width

            for (i in 0..3) {
                val x = corners[i]
                val y = corners[(i + 1) % 4]

                val p1 = from.add(x)
                val p2 = from.add(y)
                val p3 = to.add(y)
                val p4 = to.add(x)

                vertex(buffer, p1, r, g, b, alpha, 0f, v0)
                vertex(buffer, p2, r, g, b, alpha, 1f, v0)
                vertex(buffer, p3, r, g, b, alpha, 1f, v1)
                vertex(buffer, p4, r, g, b, alpha, 0f, v1)
            }
        }

        private fun vertex(
            buffer: VertexConsumer,
            pos: Vec3,
            r: Float, g: Float, b: Float, a: Float,
            u: Float, v: Float
        ) {
            buffer.addVertex(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat())
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setUv1(0, 0)
                .setNormal(0f, 1f, 0f)
                .setLight(15728880)
        }
    }
}