package joshxviii.plantz.particles

import ElectricArcParticle
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
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3

class ElectricArcParticleGroup(engine: ParticleEngine) : ParticleGroup<ElectricArcParticle>(engine) {
    companion object {
        @JvmField
        val ELECTRIC_ARC_GROUP = ParticleRenderType("plantz:ELECTRIC_ARC_GROUP")

        val RENDER_TYPE = RenderType.create(
            "electric_arc",
            RenderSetup.builder(RenderPipelines.ENERGY_SWIRL)
                .withTexture("Sampler0", pazResource("textures/particle/electric_arc.png"))
                .setOutputTarget(OutputTarget.MAIN_TARGET)
                .createRenderSetup()
        )
    }

    @JvmRecord
    data class ElectricArcParticleRenderState(
        val startPos: Vec3,
        val targetPos: Vec3,
        val thickness: Float,
        val color: Int,
        val alpha: Float,
        val age: Int,
        val lifetime: Int
    ) {
        companion object {
            fun fromParticle(particle: ElectricArcParticle, camera: Camera, partialTicks: Float): ElectricArcParticleRenderState {
                val cameraPos = camera.position()
                return ElectricArcParticleRenderState(
                    startPos = particle.startPos.subtract(cameraPos),
                    targetPos = Vec3(particle.targetPos.x, particle.targetPos.y, particle.targetPos.z).subtract(cameraPos),
                    thickness = particle.thickness,
                    color = particle.color,
                    alpha = particle.alpha,
                    age = particle.particleAge,
                    lifetime = particle.lifetime
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
            .map { ElectricArcParticleRenderState.fromParticle(it, camera, partialTickTime) }

        return State(states)
    }

    @JvmRecord
    private data class State(val renderStates: List<ElectricArcParticleRenderState>) : ParticleGroupRenderState {

        override fun submit(collector: SubmitNodeCollector, camera: CameraRenderState) {
            for (state in renderStates) {
                collector.submitCustomGeometry(PoseStack(), RENDER_TYPE) { pose, buffer ->
                    renderElectricArc(buffer, state)
                }
            }
        }

        private fun renderElectricArc(buffer: VertexConsumer, state: ElectricArcParticleRenderState) {
            val mainAlpha = state.alpha
            val ageFactor = state.age.toFloat() / state.lifetime
            val width = state.thickness * ageFactor

            // main arc
            renderSegmentedArc(buffer, state, width, mainAlpha, 9, jitterMultiplier = 1.1f)

            if (ageFactor < 0.6f) { // young arcs
                renderSegmentedArc(buffer, state, width * 0.5f, mainAlpha * 0.65f, 7, jitterMultiplier = 1.4f, offset = 0.12)
                renderSegmentedArc(buffer, state, width * 0.25f, mainAlpha * 0.45f, 6, jitterMultiplier = 1.6f, offset = -0.09)
            }
        }

        private fun renderSegmentedArc(
            buffer: VertexConsumer,
            state: ElectricArcParticleRenderState,
            width: Float,
            alpha: Float,
            segments: Int,
            jitterMultiplier: Float = 1.0f,
            offset: Double = 0.0
        ) {
            var current = state.startPos
            val dir = state.targetPos.subtract(state.startPos)
            val totalLength = dir.length()

            for (i in 0 until segments) {
                val t = (i + 1.0) / segments
                var next = state.startPos.add(dir.scale(t))

                // chaotic lightning jitter
                if (i in 1 until segments - 1) {
                    val progress = i.toDouble() / segments
                    val jitter = (0.45 * jitterMultiplier) * (1.0 - progress * 0.3) * (1.0 - state.age.toDouble() / state.lifetime)

                    val rnd = RandomSource.create((state.age * 37 + i * 17).toLong())

                    next = next.add(
                        rnd.nextDouble() * jitter - jitter * 0.5,
                        rnd.nextDouble() * jitter * 0.75 - jitter * 0.35 + offset,
                        rnd.nextDouble() * jitter - jitter * 0.5
                    )
                }

                drawSegment(buffer, current, next, width, state.color, alpha)
                current = next
            }
        }

        private fun drawSegment(
            buffer: VertexConsumer,
            from: Vec3,
            to: Vec3,
            width: Float,
            color: Int,
            alpha: Float
        ) {
            val diff = to.subtract(from).normalize()

            val perp = Vec3(-diff.z, 0.0, diff.x).normalize()

            val hw = width * 0.5

            val p1 = from.add(perp.scale(hw))
            val p2 = from.subtract(perp.scale(hw))
            val p3 = to.subtract(perp.scale(hw))
            val p4 = to.add(perp.scale(hw))

            val r = ((color shr 16) and 0xFF) / 255f
            val g = ((color shr 8) and 0xFF) / 255f
            val b = (color and 0xFF) / 255f

            vertex(buffer, p1, r, g, b, alpha)
            vertex(buffer, p2, r, g, b, alpha)
            vertex(buffer, p3, r, g, b, alpha)
            vertex(buffer, p4, r, g, b, alpha)
        }

        private fun vertex(buffer: VertexConsumer, pos: Vec3, r: Float, g: Float, b: Float, a: Float) {
            buffer.addVertex(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat())
                .setColor(r, g, b, a)
                .setUv(0f, 0f)
                .setUv1(0, 0)
                .setNormal(0f, 0f, 0f)
                .setLight(15728880)
        }
    }
}