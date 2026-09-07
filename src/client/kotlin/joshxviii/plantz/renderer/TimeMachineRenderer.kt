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
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class TimeMachineRenderer() : BlockEntityRenderer<TimeMachineBlockEntity, TimeMachineRenderSate> {

    override fun submit(
        state: TimeMachineRenderSate,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        submitSun(state, poseStack, collector, camera)
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