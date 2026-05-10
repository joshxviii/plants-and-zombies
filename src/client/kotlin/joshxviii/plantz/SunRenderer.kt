package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import joshxviii.plantz.entity.Sun
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.rendertype.OutputTarget
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import kotlin.math.floor

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
        val r = 1.0f
        val xo = 0.5f
        val yo = 0.25f
        val rr = state.ageInTicks / 4.0f
        val br = 0xFFFF77
        val rc = 0xFFFF77
        val gc = 0xFFFF77
        val bc = ((Mth.sin(rr + 10.0) + r) * xo * br).toInt()
        poseStack.translate(0.0f, 0.1f, 0.0f)
        poseStack.mulPose(camera.orientation)
        val s = 1.2f-(8.0f/(state.value.coerceAtLeast(1)+8f))
        poseStack.scale(s, s, s)
        val timeLeft = floor(state.lifeTime - state.ageInTicks).toInt()
        val isFlashing = (timeLeft <= 40) && timeLeft % 2 == 0
        if (!isFlashing) submitNodeCollector.submitCustomGeometry(
            poseStack,
            EMISSIVE_SUN,
        ) { pose: PoseStack.Pose, buffer: VertexConsumer ->
            vertex(buffer, pose, -xo, -yo, rc, gc, bc, 0f, 1f, state.lightCoords)
            vertex(buffer, pose, xo, -yo, rc, gc, bc, 1f, 1f, state.lightCoords)
            vertex(buffer, pose, xo, 0.75f, rc, gc, bc, 1f, 0f, state.lightCoords)
            vertex(buffer, pose, -xo, 0.75f, rc, gc, bc, 0f, 0f, state.lightCoords)
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
        state.lifeTime = entity.getLifeTime()
        state.icon = 0
    }

    companion object {
        private val SUN_LOCATION = pazResource("textures/entity/sun.png")
        public val RENDER_TYPE = RenderTypes.itemTranslucent(SUN_LOCATION)
        public val EMISSIVE_SUN =
            RenderType.create(
                "sun",
                RenderSetup.builder(RenderPipelines.ENERGY_SWIRL)
                    .withTexture("Sampler0", SUN_LOCATION)
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

class SunRenderState : EntityRenderState() {
    var value: Int = 0
    var lifeTime: Int = 0
    var icon: Int = 0
}