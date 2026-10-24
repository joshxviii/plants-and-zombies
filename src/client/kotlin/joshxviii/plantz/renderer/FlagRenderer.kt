package joshxviii.plantz.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazConfig
import joshxviii.plantz.block.FlagBlock
import joshxviii.plantz.block.entity.FlagBlockEntity
import joshxviii.plantz.model.FlagBlockModel
import joshxviii.plantz.pazResource
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.sprite.SpriteId
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.state.properties.RotationSegment
import net.minecraft.world.phys.Vec3

class FlagRenderer(
    val flagModel: FlagBlockModel
) : BlockEntityRenderer<FlagBlockEntity, FlagRenderState> {
    companion object {
        val PLANTZ_FLAG_MATERIAL = SpriteId(
            pazResource("textures/block/plantz_flag.png"),
            pazResource("base")
        )
        val BRAINZ_FLAG_MATERIAL = SpriteId(
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
        state.health = blockEntity.health
        state.maxHealth = blockEntity.maxHealth
        state.phase = (Math.floorMod((pos.x*7 + pos.y*9 + pos.z*13).toLong() + gameTime, 90L).toFloat() + partialTicks) / 90.0f
        state.angle = -RotationSegment.convertToDegrees(blockState.getValue(FlagBlock.ROTATION))
        if (blockState.`is`(PazBlocks.PLANTZ_FLAG)) state.sprite = PLANTZ_FLAG_MATERIAL
        else state.sprite = BRAINZ_FLAG_MATERIAL
    }

    override fun submit(
        state: FlagRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        if (PazConfig.SHOW_DEBUG_INFO) submitNodeCollector.submitNameTag(
            poseStack, Vec3(0.5,1.0,0.5), -20,
            Component.literal("${state.health} / ${state.maxHealth}").withColor(0xFFFFFFF),
            true, -1, camera
        )

        val material = state.sprite?: return
        val renderType = material.renderType { RenderTypes.entityCutout(it) }
        poseStack.translate(0.5f, 0.0f, 0.5f)
        poseStack.rotate(Axis.YP.rotationDegrees(state.angle+90f))
        submitNodeCollector.submitModel(
            flagModel,
            state,
            poseStack,
            renderType,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            0
        )
        state.breakProgress?.let { overlay ->
            submitNodeCollector.submitCrumblingOverlay(flagModel, state, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, overlay)
        }
    }
}

class FlagRenderState : BlockEntityRenderState() {
    var health: Float = 0f
    var maxHealth: Float = 0f
    var phase: Float = 0f
    var sprite: SpriteId? = null
    var angle: Float = 0f
}