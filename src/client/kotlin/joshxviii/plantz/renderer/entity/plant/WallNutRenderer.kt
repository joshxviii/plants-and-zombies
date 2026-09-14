package joshxviii.plantz.renderer.entity.plant

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.plant.WallNut
import joshxviii.plantz.model.plants.WallNutModel
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.util.Mth
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
        super.submit(state, poseStack, collector, camera)
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
        if (Minecraft.getInstance().isPaused) return
        val tickScale = entity.level().tickRateManager().tickrate() / 20f

        if (!entity.isRolling) {
            entity.rollRotation.identity()
            return
        }

        val dx = (entity.x - entity.xo).toFloat()
        val dz = (entity.z - entity.zo).toFloat()
        val distance = sqrt(dx * dx + dz * dz) * tickScale
        if (distance < 1e-4f) return

        val yawRad = entity.yRot.toDouble() * Mth.DEG_TO_RAD
        val cos = Mth.cos(yawRad)
        val sin = Mth.sin(yawRad)

        val localDx = dx * cos + -dz * sin
        val localDz = -dx * sin + dz * cos

        val radius = sqrt(16.0f * entity.scale)
        val angle = distance / radius

        var axisX = localDz
        var axisZ = -localDx
        val axisLen = sqrt(axisX * axisX + axisZ * axisZ)
        if (axisLen < 1e-4f) return
        axisX /= axisLen
        axisZ /= axisLen

        val delta = Quaternionf().fromAxisAngleRad(axisX, 0f, axisZ, angle)
        delta.mul(entity.rollRotation, entity.rollRotation)
        entity.rollRotation.normalize()
    }
}

class WallNutRenderState : PlantRenderState() {
    var isRolling: Boolean = false
    var rollRotation: Quaternionf = Quaternionf()
}