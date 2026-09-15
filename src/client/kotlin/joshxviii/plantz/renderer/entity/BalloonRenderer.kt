package joshxviii.plantz.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.entity.Balloon
import joshxviii.plantz.model.BalloonModel
import joshxviii.plantz.pazResource
import net.minecraft.client.Minecraft
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

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f))

        poseStack.mulPose(Axis.XP.rotationDegrees(-state.tiltX))
        poseStack.mulPose(Axis.ZP.rotationDegrees(-state.tiltZ))

        poseStack.scale(-1f, -1f, 1f)
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
        updateBalloonMotion(entity)

        state.tiltX = entity.clientTiltX
        state.tiltZ = entity.clientTiltZ
    }

    private fun updateBalloonMotion(entity: Balloon) {
        if (Minecraft.getInstance().isPaused) return

        val vx = entity.deltaMovement.x.toFloat()
        val vz = entity.deltaMovement.z.toFloat()
        val speed = kotlin.math.sqrt(vx * vx + vz * vz)

        val maxTilt = 86f
        val tiltStrength = (speed * 150f).coerceIn(0f, maxTilt)

        var targetTiltX: Float
        var targetTiltZ: Float
        if (speed > 1e-4f) {
            val inv = 1f / speed
            targetTiltX = -vz * inv * tiltStrength
            targetTiltZ =  vx * inv * tiltStrength
        } else {
            targetTiltX = 0f
            targetTiltZ = 0f
        }

        entity.clientTiltX = Mth.lerp(0.9f, targetTiltX, entity.clientTiltX)
        entity.clientTiltZ = Mth.lerp(0.9f, targetTiltZ, entity.clientTiltZ)
    }

    fun getTextureLocation(state: BalloonRenderState): Identifier {
        return pazResource("textures/entity/balloon/${state.color.name.lowercase()}.png")
    }
}

class BalloonRenderState : EntityRenderState() {
    var color: DyeColor = DyeColor.WHITE
    var tiltX: Float = 0f
    var tiltZ: Float = 0f
}
