package joshxviii.plantz.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.PazRenderPipelines
import joshxviii.plantz.block.TimeMachineBlock
import joshxviii.plantz.block.entity.TimeMachineBlockEntity
import joshxviii.plantz.gui.GuiUtil
import joshxviii.plantz.pazResource
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.core.Direction
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class TimeMachineRenderer() : BlockEntityRenderer<TimeMachineBlockEntity, TimeMachineRenderSate> {

    companion object {
        const val PORTAL_COLOR = 0x27FF4B
        // place hodler textures
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

    override fun submit(
        state: TimeMachineRenderSate,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        submitSun(state, poseStack, collector, camera)
        submitPortal(state, poseStack, collector, camera)
    }

    fun submitPortal(
        state: TimeMachineRenderSate,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        if (state.activePortalTime <= 0) return
        val open = Mth.lerp((state.activePortalTime / 20f.toDouble()).coerceIn(0.0, 1.0).pow(0.6), 0.0, 1.0)

        val s = open.toFloat() * 3f
        val a = (open * 255).toInt()
        val color = (a shl 24) or PORTAL_COLOR

        poseStack.pushPose()
        poseStack.translate(0.5f, 2.5f, 0.5f)
        val d = .25f
        //poseStack.translate(state.facing.stepX.toFloat()*-d, 0.0f, state.facing.stepZ.toFloat()*-d)
        poseStack.mulPose(Axis.YP.rotationDegrees(state.facing.toYRot()))
        poseStack.scale(s, s, s)
        poseStack.mulPose(Axis.ZP.rotation(-(Math.PI * open).toFloat()))
        collector.submitCustomGeometry(poseStack, TIME_PORTAL) { pose, buffer ->
            GuiUtil.plane(pose, buffer, state.lightCoords, color = ARGB.color(0xFF, color))
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f))
        collector.submitCustomGeometry(poseStack, TIME_PORTAL) { pose, buffer ->
            GuiUtil.plane(pose, buffer, state.lightCoords, color = ARGB.color(0xFF, color))
        }

        poseStack.popPose()
    }

    fun submitSun(state: TimeMachineRenderSate, poseStack: PoseStack, collector: SubmitNodeCollector, camera: CameraRenderState) {
        val d = .25f
        poseStack.pushPose()
        poseStack.translate(state.facing.stepX.toFloat()*d, 0.12f, state.facing.stepZ.toFloat()*d)
        SunBatteryRenderer.submitSun(
            poseStack,
            collector,
            camera,
            state.time,
            state.sunPercent,
            state.lightCoords
        )
        poseStack.popPose()
    }

    override fun createRenderState(): TimeMachineRenderSate = TimeMachineRenderSate()
    override fun extractRenderState(
        blockEntity: TimeMachineBlockEntity,
        state: TimeMachineRenderSate,
        partialTicks: Float,
        cameraPosition: Vec3,
        breakProgress: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        state.time = blockEntity.getLevel()!!.gameTime.toFloat()
        state.sunPercent = blockEntity.blockState.getValue(TimeMachineBlock.LEVEL).toFloat() / 15.0f
        state.facing = blockEntity.blockState.getValue(TimeMachineBlock.FACING)
        state.activePortalTime = blockEntity.activeTime
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress)
    }
}

class TimeMachineRenderSate : BlockEntityRenderState() {
    var time: Float = 0f
    var sunPercent: Float = 0f
    var facing: Direction = Direction.NORTH
    var activePortalTime: Int = 0
}