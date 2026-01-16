package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import joshxviii.plantz.entity.Sun
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.rendertype.OutputTarget
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth

class SunRenderer(context: EntityRendererProvider.Context) :
    EntityRenderer<Sun, SunRenderState>(context) {
    init {
        this.shadowRadius = 0.15f
        this.shadowStrength = 0.75f
    }

    override fun getBlockLightLevel(entity: Sun, blockPos: BlockPos): Int {
        return Mth.clamp(super.getBlockLightLevel(entity, blockPos) + 7, 0, 15)
    }

    override fun submit(
        state: SunRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        poseStack.pushPose()
        val icon = state.icon
        val u0 = (icon % 4 * 16 + 0) / 64.0f
        val u1 = (icon % 4 * 16 + 16) / 64.0f
        val v0 = (icon / 4 * 16 + 0) / 64.0f
        val v1 = (icon / 4 * 16 + 16) / 64.0f
        val r = 1.0f
        val xo = 0.5f
        val yo = 0.25f
        val br = 255.0f
        val rr = state.ageInTicks / 4.0f
        val rc = 255
        val gc = 255
        val bc = ((Mth.sin(rr + 10.0) + r) * xo * br).toInt()
        poseStack.translate(0.0f, 0.1f, 0.0f)
        poseStack.mulPose(camera.orientation)
        val s = 1.2f-(8.0f/(state.value.coerceAtLeast(1)+8f))
        poseStack.scale(s, s, s)
        submitNodeCollector.submitCustomGeometry(
            poseStack,
            EMISSIVE_SUN,
        ) { pose: PoseStack.Pose?, buffer: VertexConsumer? ->
            vertex(buffer!!, pose!!, -xo, -yo, rc, gc, bc, u0, v1, state.lightCoords)
            vertex(buffer, pose, xo, -yo, rc, gc, bc, u1, v1, state.lightCoords)
            vertex(buffer, pose, xo, 0.75f, rc, gc, bc, u1, v0, state.lightCoords)
            vertex(buffer, pose, -xo, 0.75f, rc, gc, bc, u0, v0, state.lightCoords)
        }
        poseStack.popPose()
        super.submit(state, poseStack, submitNodeCollector, camera)
    }

    override fun createRenderState(): SunRenderState {
        return SunRenderState()
    }

    override fun extractRenderState(entity: Sun, state: SunRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        state.value = entity.value
        state.icon = entity.icon
    }

    companion object {
        private val SUN_LOCATION = pazResource("textures/entity/sun.png")
        private val RENDER_TYPE = RenderTypes.itemEntityTranslucentCull(SUN_LOCATION)
        private val EMISSIVE_SUN =
            RenderType.create(
                "sun",
                RenderSetup.builder(PazRenderPipelines.EMISSIVE_SUN)
                    .withTexture("Sampler0", pazResource("textures/entity/sun.png"))
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()
            )


        private fun vertex(
            buffer: VertexConsumer,
            pose: PoseStack.Pose,
            x: Float,
            y: Float,
            r: Int,
            g: Int,
            b: Int,
            u: Float,
            v: Float,
            lightCoords: Int
        ) {
            buffer.addVertex(pose, x, y, 0.0f)
                .setColor(r, g, b, 128)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, 0.0f, 1.0f, 0.0f)
        }
    }
}