package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.Plant
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.state.CameraRenderState
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
        
        //Add any extra rendering stuff here
        if (state.ageInTicks>1) super.submit(state, poseStack, collector, camera)

        poseStack.popPose()
    }

    override fun createRenderState(): PlantRenderState {
        return PlantRenderState()
    }

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)

        state.damagedAmount = entity.damagedPercent
        state.texturePath = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type).path
        state.initAnimationState.copyFrom(entity.initAnimationState)
        state.idleAnimationState.copyFrom(entity.idleAnimationState)
        state.actionAnimationState.copyFrom(entity.actionAnimationState)
        state.coolDownAnimationState.copyFrom(entity.coolDownAnimationState)
    }

    override fun getTextureLocation(state: PlantRenderState): Identifier {

        val baseTexture = "textures/entity/${state.texturePath}/${state.texturePath}"

        val base = pazResource("${baseTexture}.png")
        val damage = when (state.damagedAmount) {// change texture based on damage
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