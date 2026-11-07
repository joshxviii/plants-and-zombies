package joshxviii.plantz.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import joshxviii.plantz.entity.Sun
import joshxviii.plantz.pazResource
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.util.LightCoordsUtil

class SunRenderer(context: EntityRendererProvider.Context) :
    EntityRenderer<Sun, SunRenderState>(context) {
    init {
        this.shadowRadius = 0.15f
        this.shadowStrength = 0.75f
    }

    override fun getBlockLightLevel(entity: Sun, blockPos: BlockPos): Int {
        return 15
    }

    override fun submit(
        state: SunRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        val blinkedOut = state.isExpiring && state.ageInTicks.toInt() % 2 == 0
        if (!blinkedOut) {
            val scale = Sun.scaleForValue(state.value)
            poseStack.pushPose()
            poseStack.translate(0.0f, state.centerY, 0.0f)
            poseStack.rotate(camera.orientation)
            poseStack.scale(scale, scale, scale)
            //poseStack.mulPose(Axis.YP.rotation(state.ageInTicks * SPIN_SPEED + state.phase))
            submitPlane(state, poseStack, submitNodeCollector)
            //poseStack.mulPose(Axis.YP.rotation(Mth.HALF_PI))
            //submitPlane(state, poseStack, submitNodeCollector)
            poseStack.popPose()
        }

        super.submit(state, poseStack, submitNodeCollector, camera)
    }

    private fun submitPlane(state: SunRenderState, poseStack: PoseStack, collector: SubmitNodeCollector) {
        val light = LightCoordsUtil.FULL_BRIGHT
        collector.submitCustomGeometry(poseStack, EMISSIVE_SUN) { pose: PoseStack.Pose, buffer: VertexConsumer ->
            vertex(buffer, pose, -HALF, -HALF, 0f, 1f, light)
            vertex(buffer, pose, HALF, -HALF, 1f, 1f, light)
            vertex(buffer, pose, HALF, HALF, 1f, 0f, light)
            vertex(buffer, pose, -HALF, HALF, 0f, 0f, light)

            vertex(buffer, pose, -HALF, -HALF, 0f, 1f, light)
            vertex(buffer, pose, -HALF, HALF, 0f, 0f, light)
            vertex(buffer, pose, HALF, HALF, 1f, 0f, light)
            vertex(buffer, pose, HALF, -HALF, 1f, 1f, light)
        }
    }

    override fun createRenderState(): SunRenderState {
        return SunRenderState()
    }

    override fun extractRenderState(entity: Sun, state: SunRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        state.value = entity.value
        state.isExpiring = entity.isExpiring
        state.centerY = entity.bbHeight / 2f
        state.phase = entity.id * 0.7f
    }

    companion object {
        private val SUN_LOCATION = pazResource("textures/entity/sun.png")
        val EMISSIVE_SUN =
            RenderType.create(
                "sun",
                RenderSetup.builder(RenderPipelines.BEACON_BEAM_TRANSLUCENT)
                    .withTexture("Sampler0", SUN_LOCATION)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setLayeringTransform(LayeringTransform.NO_LAYERING)
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()
            )


        private const val HALF = 0.5f
        private const val SPIN_SPEED = 0.06f

        private fun vertex(buffer: VertexConsumer, pose: PoseStack.Pose, x: Float, y: Float, u: Float, v: Float, lightCoords: Int) {
            buffer.addVertex(pose, x, y, 0.0f)
                .setColor(255, 255, 255, 200)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, 0.0f, 1.0f, 0.0f)
        }
    }
}

class SunRenderState : EntityRenderState() {
    var value: Int = 0
    var isExpiring: Boolean = false
    var phase: Float = 0f
    var centerY: Float = 0.15f
}