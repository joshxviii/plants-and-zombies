package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.Plant
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier

class PlantRenderer(
    model: EntityModel<PlantRenderState>,
    context: EntityRendererProvider.Context
) : MobRenderer<Plant, PlantRenderState, EntityModel<PlantRenderState>>(
    context,
    model,
    0.5f
) {
    override fun submit(
        state: PlantRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        poseStack.pushPose()
        if (state.isPotted) {

            poseStack.pushPose()
            poseStack.translate(-0.5,0.0,-0.5)
            collector.submitBlock(
                poseStack,
                PazBlocks.PLANT_POT.defaultBlockState(),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
            )
            poseStack.popPose()

            poseStack.translate(0.0, 0.375, 0.0)
        }

        super.submit(state, poseStack, collector, camera)
        poseStack.popPose()
    }

    override fun createRenderState(): PlantRenderState {
        return PlantRenderState()
    }

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)

        state.damage = entity.damage
        state.isPotted = entity.isPotted
        state.texturePath = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type).path
        state.idleAnimationState.start(0)
    }

    override fun getTextureLocation(state: PlantRenderState): Identifier {

        val baseTexture = "textures/entity/${state.texturePath}/${state.texturePath}"

        val base = pazResource("${baseTexture}.png")
        val damage = when (state.damage) {// change texture based on damage
            in 0.5f..0.75f -> pazResource("${baseTexture}_damage_low.png")
            in 0.75f..1.0f -> pazResource("${baseTexture}_damage_medium.png")
            else -> base
        }
        return if (Minecraft.getInstance().resourceManager.getResource(damage).isPresent)
            damage
        else
            base
    }
}