package joshxviii.plantz.renderer.entity.plant

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.plant.Pumpkin
import joshxviii.plantz.model.plants.PumpkinModel
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState

class PumpkinRenderer(
    context: EntityRendererProvider.Context,
    private val defaultModel: EntityModel<PlantRenderState> = PumpkinModel(context.bakeLayer(PumpkinModel.LAYER_LOCATION)),
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
        super.submit(state, poseStack, collector, camera)
    }

    override fun createRenderState(): PlantRenderState {
        return PumpkinRenderState()
    }

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        (state as PumpkinRenderState)
        (entity as Pumpkin)
        super.extractRenderState(entity, state, partialTick)
    }
}

class PumpkinRenderState : PlantRenderState() {
    init {
        pumpkinArmor = -1f
        entityType = PazEntities.PUMPKIN
    }
}