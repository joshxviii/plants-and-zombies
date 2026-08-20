package joshxviii.plantz.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.block.TimeMachineBlock
import joshxviii.plantz.block.TimeMachineState
import joshxviii.plantz.block.entity.TimeMachineBlockEntity
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.pow

class TimeMachineRenderer() : BlockEntityRenderer<TimeMachineBlockEntity, TimeMachineRenderSate> {
    override fun createRenderState(): TimeMachineRenderSate {
        return TimeMachineRenderSate()
    }

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
        if (blockEntity.blockState.getValue(TimeMachineBlock.STATE) == TimeMachineState.ACTIVE) state.activePortalTime++
        else state.activePortalTime = 0
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress)
    }

    override fun submit(
        state: TimeMachineRenderSate,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        val s = (state.sunPercent).pow(0.5f) * 0.9f
        val d = .25f
        poseStack.translate(state.facing.stepX.toFloat()*d+0.5f, 0.45f, state.facing.stepZ.toFloat()*d+0.5f)
        poseStack.scale(s, s, s)
        poseStack.mulPose(camera.orientation)
        poseStack.mulPose(Axis.YP.rotation(state.time*0.04f))

        SunBatteryRenderer.submitSunShine(state, poseStack, collector)
        poseStack.mulPose(Axis.YP.rotation(Mth.PI*0.5f))
        SunBatteryRenderer.submitSunShine(state, poseStack, collector)
    }
}

class TimeMachineRenderSate : BlockEntityRenderState() {
    var time: Float = 0f
    var sunPercent: Float = 0f
    var facing: Direction = Direction.NORTH
    var activePortalTime: Int = 0
}