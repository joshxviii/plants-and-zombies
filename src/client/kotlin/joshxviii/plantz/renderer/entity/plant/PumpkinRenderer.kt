package joshxviii.plantz.renderer.entity.plant

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.plant.Pumpkin
import joshxviii.plantz.model.plants.PumpkinHelmetModel
import joshxviii.plantz.model.plants.PumpkinModel
import joshxviii.plantz.renderer.entity.plant.PlantRenderState.Companion.TEXTURE_PATH
import joshxviii.plantz.renderer.getTextureLocation
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.resources.Identifier

class PumpkinRenderer(
    context: EntityRendererProvider.Context,
    private var defaultModel: EntityModel<PlantRenderState> = PumpkinModel(context.bakeLayer(PumpkinModel.LAYER_LOCATION)),
): PlantRenderer(
    defaultModel,
    context,
    null
) {
    val pumpkinHelmetModel = PumpkinHelmetModel(context.bakeLayer(PumpkinHelmetModel.LAYER_LOCATION))

    override fun submit(
        state: PlantRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        (state as PumpkinRenderState)
        model = if (state.helmetMode) pumpkinHelmetModel else defaultModel
        super.submit(state, poseStack, collector, camera)
    }

    override fun createRenderState(): PlantRenderState {
        return PumpkinRenderState()
    }

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        (state as PumpkinRenderState)
        (entity as Pumpkin)
        super.extractRenderState(entity, state, partialTick)
        if (state.renderedByWearer) state.helmetMode = true

    }

    override fun getTextureLocation(state: PlantRenderState): Identifier {
        (state as PumpkinRenderState)
        val s = state.getSuffixes()
        if (state.helmetMode) s.add("helmet")
        return state.getTextureLocation(TEXTURE_PATH, s)
    }
}

class PumpkinRenderState : PlantRenderState() {
    var helmetMode: Boolean = false
    init {
        pumpkinArmor = -1f
        entityType = PazEntities.PUMPKIN
    }
}