package joshxviii.plantz.renderer.entity.plant

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.model.plants.RoyalSunflowerModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState

class RoyalSunflowerRenderer(
    context: EntityRendererProvider.Context,
    val model: RoyalSunflowerModel = RoyalSunflowerModel(context.bakeLayer(RoyalSunflowerModel.LAYER_LOCATION))
): PlantRenderer(model, context, null) {

    override fun submit(
        state: PlantRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        poseStack.scale(1.5f, 1.5f, 1.5f)
        super.submit(state, poseStack, collector, camera)
    }

}