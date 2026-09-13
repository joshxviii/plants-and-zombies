package joshxviii.plantz.renderer.entity.plant

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.plant.WallNut
import joshxviii.plantz.model.plants.WallNutModel
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState
import org.joml.Quaternionf
import kotlin.math.sqrt

class WallNutRenderer(
    context: EntityRendererProvider.Context,
    private val defaultModel: EntityModel<PlantRenderState> = WallNutModel(context.bakeLayer(WallNutModel.LAYER_LOCATION))
): PlantRenderer(
    defaultModel,
    context,
    null
) {

    override fun submit(
        state: PlantRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        state as WallNutRenderState
        poseStack.pushPose()
        val rollCenter = state.boundingBoxHeight * .5
        poseStack.translate(0.0, rollCenter, 0.0)
        if (state.isRolling) poseStack.mulPose(state.rollRotation)
        poseStack.translate(0.0, -rollCenter, 0.0)
        super.submit(state, poseStack, collector, camera)
        poseStack.popPose()
    }

    override fun createRenderState(): PlantRenderState {
        return WallNutRenderState()
    }

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        (state as WallNutRenderState)
        (entity as WallNut)
        super.extractRenderState(entity, state, partialTick)
        updateClientRoll(entity)
        state.isRolling = entity.isRolling
        state.rollRotation.set(entity.rollRotation)
    }

    private fun updateClientRoll(entity: WallNut) {
        if (!entity.isRolling) {
            entity.rollRotation = Quaternionf()
            return
        }

        val dx = (entity.x - entity.xo).toFloat()
        val dz = (entity.z - entity.zo).toFloat()
        val distance = kotlin.math.sqrt(dx * dx + dz * dz)
        if (distance < 1e-4f) return

        val radius = sqrt(16.0f * entity.scale)
        val angle = distance / radius
        val axisX = dz
        val axisZ = -dx
        val axisLen = kotlin.math.sqrt(axisX * axisX + axisZ * axisZ)
        if (axisLen < 1e-4f) return

        val inv = 1f / axisLen
        val delta = Quaternionf().fromAxisAngleRad(
            axisX * inv, 0f, axisZ * inv, angle
        )

        delta.mul(entity.rollRotation, entity.rollRotation)
        entity.rollRotation.normalize()
    }
}

class WallNutRenderState : PlantRenderState() {
    var isRolling: Boolean = false
    var rollRotation: Quaternionf = Quaternionf()
}