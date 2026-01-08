package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.PlantPotMinecart
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.MinecartRenderState
import net.minecraft.world.level.block.state.BlockState

class PlantPotMinecartRenderer(
    context: EntityRendererProvider.Context,
    model: ModelLayerLocation,
): AbstractMinecartRenderer<PlantPotMinecart, MinecartRenderState>(
    context,
    model
) {
    override fun submitMinecartContents(
        state: MinecartRenderState,
        blockState: BlockState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        lightCoords: Int
    ) {
        poseStack.translate(-0.5 * 1/3, 0.15, -0.5 * 1/3)
        poseStack.scale(4/3f, 4/3f, 4/3f)
        super.submitMinecartContents(state, blockState, poseStack, submitNodeCollector, lightCoords)
    }

    override fun createRenderState(): MinecartRenderState = MinecartRenderState()
}