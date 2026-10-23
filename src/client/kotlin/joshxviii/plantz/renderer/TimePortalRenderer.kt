package joshxviii.plantz.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.PazRenderPipelines
import joshxviii.plantz.block.TimePortalBlock
import joshxviii.plantz.block.entity.TimePortalBlockEntity
import joshxviii.plantz.pazResource
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.pow

class TimePortalRenderer() : BlockEntityRenderer<TimePortalBlockEntity, TimePortalRenderSate> {

    companion object {
        const val PORTAL_COLOR = 0x00daf5
        // place holder textures
        private val TEXTURE_PORTAL_BACKGROUND = pazResource("textures/block/time_machine/portal0.png")
        private val TEXTURE_PORTAL_FOREGROUND = pazResource("textures/block/time_machine/portal1.png")
        public val TIME_PORTAL =
            RenderType.create(
                "time_portal",
                RenderSetup.builder(PazRenderPipelines.TIME_PORTAL)
                    .withTexture("Sampler0", TEXTURE_PORTAL_BACKGROUND)
                    .withTexture("Sampler1", TEXTURE_PORTAL_FOREGROUND)
                    .createRenderSetup()
            )
    }

    override fun createRenderState(): TimePortalRenderSate = TimePortalRenderSate()
    override fun extractRenderState(
        blockEntity: TimePortalBlockEntity,
        state: TimePortalRenderSate,
        partialTicks: Float,
        cameraPosition: Vec3,
        breakProgress: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        state.portalTime = blockEntity.tickCount
        state.facing = blockEntity.blockState.getValue(TimePortalBlock.FACING)

        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress)
    }

    override fun submit(state: TimePortalRenderSate, poseStack: PoseStack, collector: SubmitNodeCollector, camera: CameraRenderState) {
        submitPortal(state, poseStack, collector, camera)
    }

    fun submitPortal(
        state: TimePortalRenderSate,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        if (state.portalTime <= 0) return
        val open = Mth.lerp((state.portalTime / 20f.toDouble()).coerceIn(0.0, 1.0).pow(0.6), 0.0, 1.0)

        val s = 3f
        val a = (open * 255).toInt()
        val color = (a shl 24) or PORTAL_COLOR

        poseStack.pushPose()
        poseStack.translate(0.5f, 0.5f, 0.5f)
        poseStack.mulPose(Axis.YP.rotationDegrees(state.facing.toYRot()))

        poseStack.scale(s, s, s)
        poseStack.mulPose(Axis.YP.rotation(cos(Math.PI * state.portalTime/40f).toFloat() * 0.01f))
        collector.submitCustomGeometry(poseStack, TIME_PORTAL) { pose, buffer ->
            GuiUtil.plane(pose, buffer, state.lightCoords, color = color)
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f))
        collector.submitCustomGeometry(poseStack, TIME_PORTAL) { pose, buffer ->
            GuiUtil.plane(pose, buffer, state.lightCoords, color = color)
        }

        poseStack.popPose()
    }

}

class TimePortalRenderSate : BlockEntityRenderState() {
    var portalTime: Int = 0
    var facing: Direction = Direction.NORTH
}