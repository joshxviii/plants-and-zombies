package joshxviii.plantz.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.entity.Balloon
import joshxviii.plantz.model.BalloonModel
import joshxviii.plantz.pazResource
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import net.minecraft.world.item.DyeColor

class BalloonRenderer(
    context: EntityRendererProvider.Context,
) : EntityRenderer<Balloon, BalloonRenderState>(context) {
    val model: BalloonModel<BalloonRenderState> = BalloonModel(context.bakeLayer(BalloonModel.LAYER_LOCATION))

    override fun submit(
        state: BalloonRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        poseStack.pushPose()
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - state.yRot))
        poseStack.mulPose(Axis.XP.rotationDegrees(state.xRot))
        poseStack.scale(-1f,-1f,1f)
        poseStack.translate(0.0f, -1.501f, 0.0f)
        collector.submitModel(
            model,
            state,
            poseStack,
            getTextureLocation(state),
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            state.outlineColor,
            null
        )
        poseStack.popPose()
        super.submit(state, poseStack, collector, camera)
    }

    override fun createRenderState(): BalloonRenderState = BalloonRenderState()

    override fun extractRenderState(entity: Balloon, state: BalloonRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        state.color = entity.dyeColor
        state.yRot = Mth.rotLerp(partialTicks, entity.yRotO, entity.yRot);
        state.xRot = Mth.rotLerp(partialTicks, entity.xRotO, entity.xRot);
    }

    fun getTextureLocation(state: BalloonRenderState): Identifier {
        return pazResource("textures/entity/balloon/${state.color.name.lowercase()}.png")
    }
}

class BalloonRenderState : EntityRenderState() {
    var color: DyeColor = DyeColor.WHITE
    @JvmField
    var yRot: Float = 0f
    var xRot: Float = 0f
}
