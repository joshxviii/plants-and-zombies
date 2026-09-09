package joshxviii.plantz.particles

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import joshxviii.plantz.pazResource
import net.minecraft.client.Camera
import net.minecraft.client.particle.ParticleEngine
import net.minecraft.client.particle.ParticleGroup
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

class ElectricArcParticleGroup(engine: ParticleEngine) : ParticleGroup<ElectricArcParticle>(engine) {
    companion object {
        val RENDER_TYPE = RenderType.create(
            "electric_arc",
            RenderSetup.builder(RenderPipelines.BEACON_BEAM_TRANSLUCENT)
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
        val lifetime: Int,
        val random: RandomSource
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
                    lifetime = particle.lifetime,
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
            .map { ElectricArcParticleRenderState.fromParticle(it, camera, partialTickTime) }

        return State(states)
    }

    @JvmRecord
    private data class State(val renderStates: List<ElectricArcParticleRenderState>) : ParticleGroupRenderState {

        override fun submit(collector: SubmitNodeCollector, camera: CameraRenderState) {
            for (state in renderStates) {
                collector.submitCustomGeometry(PoseStack(), RENDER_TYPE) { _, buffer ->
                    renderElectricArc(buffer, state)
                }
            }
        }

        private fun renderElectricArc(buffer: VertexConsumer, state: ElectricArcParticleRenderState) {
            val ageFactor = 1-(state.age.toFloat() / state.lifetime)
            val mainAlpha = state.alpha * ageFactor
            val width = state.thickness * ageFactor

            renderSegmentedArc(buffer, state, width, mainAlpha, 10, 1.05f, randomInt = state.random.nextFloat())

            if (ageFactor < 0.75f) renderSegmentedArc(buffer, state, width * 0.55f, mainAlpha, 8, 1.35f, 0.08, state.random.nextFloat())
            if (ageFactor < 0.74f) renderSegmentedArc(buffer, state, width * 0.35f, mainAlpha, 7, 1.6f, -0.10, state.random.nextFloat())
        }

        private fun renderSegmentedArc(
            buffer: VertexConsumer,
            state: ElectricArcParticleRenderState,
            width: Float,
            alpha: Float,
            segments: Int,
            jitterMultiplier: Float = 1.0f,
            yOffset: Double = 0.0,
            randomInt: Float = 0f
        ) {
            var current = state.startPos
            val dir = state.targetPos.subtract(state.startPos)
            val twist = Mth.TWO_PI * randomInt

            for (i in 0 until segments) {
                val t = (i + 1.0) / segments
                var next = state.startPos.add(dir.scale(t))

                if (i in 1 until segments - 1) {
                    val progress = i.toDouble() / segments
                    val jitter = 0.42 * jitterMultiplier * (1.0 - progress * 0.4) * (1.0 - state.age.toDouble() / state.lifetime)

                    val rnd = RandomSource.create((state.age * 31 + i * 19).toLong())

                    next = next.add(
                        rnd.nextDouble() * jitter - jitter * 0.5,
                        rnd.nextDouble() * jitter * 0.8 - jitter * 0.35 + yOffset,
                        rnd.nextDouble() * jitter - jitter * 0.5
                    )
                }

                drawSegment(buffer, current, next, width, state.color, alpha, twist)
                current = next
            }
        }

        private fun drawSegment(
            buffer: VertexConsumer,
            from: Vec3,
            to: Vec3,
            width: Float,
            color: Int,
            alpha: Float,
            twist: Float
        ) {
            val r = ((color shr 16) and 0xFF) / 255f
            val g = ((color shr 8) and 0xFF) / 255f
            val b = (color and 0xFF) / 255f

            val hw = width * 0.5

            repeat(2) {
                val diff = to.subtract(from).normalize()
                val neg = if (it == 0) 1 else -1
                var perp = Vec3(-diff.z * neg, 0.0, diff.x * neg).normalize()
                perp = perp.zRot(twist)

                val p1 = from.add(perp.scale(hw))
                val p2 = from.subtract(perp.scale(hw))
                val p3 = to.subtract(perp.scale(hw))
                val p4 = to.add(perp.scale(hw))

                vertex(buffer, p1, r, g, b, alpha)
                vertex(buffer, p2, r, g, b, alpha)
                vertex(buffer, p3, r, g, b, alpha)
                vertex(buffer, p4, r, g, b, alpha)
            }
        }

        private fun vertex(buffer: VertexConsumer, pos: Vec3, r: Float, g: Float, b: Float, a: Float) {
            buffer.addVertex(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat())
                .setColor(r, g, b, a)
                .setUv(0f, 0f)
                .setUv1(0, 0)
                .setNormal(0f, 1f, 0f)
                .setLight(15728880)
        }
    }
}