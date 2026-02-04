package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.block.FlagBlock
import joshxviii.plantz.block.entity.FlagBlockEntity
import joshxviii.plantz.model.FlagBlockModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.Material
import net.minecraft.world.level.block.state.properties.RotationSegment
import net.minecraft.world.phys.Vec3

class FlagRenderer(
    val flagModel: FlagBlockModel
) : BlockEntityRenderer<FlagBlockEntity, FlagRenderState> {
    companion object {
        val PLANTZ_FLAG_MATERIAL = Material(
            pazResource("textures/block/plantz_flag.png"),
            pazResource("base")
        )
        val BRAINZ_FLAG_MATERIAL = Material(
            pazResource("textures/block/brainz_flag.png"),
            pazResource("base")
        )
    }
    override fun createRenderState(): FlagRenderState {
        return FlagRenderState()
    }

    override fun extractRenderState(
        blockEntity: FlagBlockEntity,
        state: FlagRenderState,
        partialTicks: Float,
        cameraPosition: Vec3,
        breakProgress: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress)
        val pos = blockEntity.blockPos
        val blockState = blockEntity.blockState
        val gameTime = if (blockEntity.getLevel() != null) blockEntity.getLevel()!!.gameTime else 0L
        state.phase = (Math.floorMod((pos.x*7 + pos.y*9 + pos.z*13).toLong() + gameTime, 90L).toFloat() + partialTicks) / 90.0f
        state.angle = -RotationSegment.convertToDegrees(blockState.getValue(FlagBlock.ROTATION))
        if (blockState.`is`(PazBlocks.PLANTZ_FLAG)) state.material = PLANTZ_FLAG_MATERIAL
        else state.material = BRAINZ_FLAG_MATERIAL
    }

    override fun submit(
        state: FlagRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        val material = state.material?: return
        val renderType = material.renderType { RenderTypes.entityCutout(it) }
        poseStack.translate(0.5f, 0.0f, 0.5f)
        poseStack.mulPose(Axis.YP.rotationDegrees(state.angle+90f))
        submitNodeCollector.submitModel(
            flagModel,
            state,
            poseStack,
            renderType,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            0,
            state.breakProgress
        )
    }
}

class FlagRenderState : BlockEntityRenderState() {
    var phase: Float = 0f
    var material: Material? = null
    var angle: Float = 0f
}