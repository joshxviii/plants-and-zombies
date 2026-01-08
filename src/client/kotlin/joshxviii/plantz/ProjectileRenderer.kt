package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.projectile.Projectile

class ProjectileRenderer(
    val model: EntityModel<ProjectileRenderState>,
    context: EntityRendererProvider.Context
) : EntityRenderer<Projectile, ProjectileRenderState>(
    context
) {
    override fun submit(
        state: ProjectileRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        poseStack.pushPose()
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0f))
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot))
        poseStack.translate(0.0, -1.5, 0.0)
        submitNodeCollector.submitModel(
            this.model,
            state,
            poseStack,
            RenderType.create(
                "plant_projectile",
                RenderSetup.builder(RenderPipelines.BREEZE_WIND)// TODO make a custom render pipeline
                    .withTexture("Sampler0", getTextureLocation(state))
                    .useLightmap()
                    .sortOnUpload()
                    .createRenderSetup()
            ),
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            state.outlineColor,
            null
        )
        poseStack.popPose()
        super.submit(state, poseStack, submitNodeCollector, camera)
    }

    override fun createRenderState(): ProjectileRenderState {
        return ProjectileRenderState()
    }

    override fun extractRenderState(entity: Projectile, state: ProjectileRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)
        state.xRot = entity.getXRot(partialTick)
        state.yRot = entity.getYRot(partialTick)
        state.texturePath = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type).path
    }

    fun getTextureLocation(state: ProjectileRenderState): Identifier {
        val baseTexture = "textures/entity/projectile/${state.texturePath}.png"
        return pazResource(baseTexture)
    }
}