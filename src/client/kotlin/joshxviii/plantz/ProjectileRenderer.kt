package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.projectile.PlantProjectile
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EntityType

class ProjectileRenderer(
    val model: EntityModel<ProjectileRenderState>,
    context: EntityRendererProvider.Context
) : EntityRenderer<PlantProjectile, ProjectileRenderState>(
    context
) {
    override fun submit(
        state: ProjectileRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0, -1.5, 0.0)
        submitNodeCollector.submitModel(
            this.model,
            state,
            poseStack,
            RenderType.create(
                "plant_projectile",
                RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT)
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

    override fun extractRenderState(entity: PlantProjectile, state: ProjectileRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)
        state.type = entity.type as EntityType<out PlantProjectile>?
    }

    fun getTextureLocation(state: ProjectileRenderState): Identifier {
        val texture = when (state.type) {// change texture based on the projectile type
            PazEntities.PEA -> "pea"
            PazEntities.PEA_ICE -> "pea_ice"
            else -> "pea"
        }
        return pazResource("textures/entity/projectile/${texture}.png")
    }
}