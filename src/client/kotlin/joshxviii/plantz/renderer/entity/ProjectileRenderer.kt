package joshxviii.plantz.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.entity.projectile.LaserBullet
import joshxviii.plantz.entity.projectile.PaintBall
import joshxviii.plantz.renderer.getProjectileTextureLocation
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.EyesLayer
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.util.ARGB
import net.minecraft.world.entity.projectile.Projectile

class ProjectileRenderer<M: EntityModel<ProjectileRenderState>>(
    val projectileModel: M,
    context: EntityRendererProvider.Context,
) : EntityRenderer<Projectile, ProjectileRenderState>(
    context
), RenderLayerParent<ProjectileRenderState, M> {
    private val renderLayers: MutableList<RenderLayer<ProjectileRenderState, M>> = mutableListOf()

    override fun getModel(): M {
        return projectileModel
    }

    init {
        renderLayers.add(EmissiveProjectileLayer(this))
    }

    override fun submit(
        state: ProjectileRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        poseStack.pushPose()
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0f))
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot))
        poseStack.translate(0.0, -1.5, 0.0)
        val tint = state.color?: -1
        val texture = state.getProjectileTextureLocation(ProjectileRenderState.TEXTURE_PATH)
        if (texture!=null) collector.submitModel(
            this.projectileModel,
            state,
            poseStack,
            this.projectileModel.renderType(texture),
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            tint,
            null,
            state.outlineColor,
            null
        )
        model.setupAnim(state)
        for (layer in renderLayers) {
            layer.submit(poseStack, collector, state.lightCoords, state, state.yRot, state.xRot)
        }
        poseStack.popPose()

        super.submit(state, poseStack, collector, camera)
    }

    override fun createRenderState(): ProjectileRenderState {
        return ProjectileRenderState()
    }

    override fun extractRenderState(entity: Projectile, state: ProjectileRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)
        if (entity is PaintBall) state.color = entity.dyeColor.fireworkColor
        if (entity is LaserBullet) state.color = entity.laserColor
        state.xRot = entity.getXRot(partialTick)
        state.yRot = entity.getYRot(partialTick)
        state.texturePath = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type).path
    }
}

class EmissiveProjectileLayer<M : EntityModel<ProjectileRenderState>>(
    renderer: RenderLayerParent<ProjectileRenderState, M>,
) : EyesLayer<ProjectileRenderState, M>(renderer) {

    override fun submit(
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        lightCoords: Int,
        state: ProjectileRenderState,
        yRot: Float,
        xRot: Float
    ) {
        val textureLocation = state.getProjectileTextureLocation(ProjectileRenderState.TEXTURE_PATH, true) ?: return
        val renderType = RenderTypes.eyes(textureLocation)
        val tint = state.color?.let {ARGB.opaque(it) }?: -1
        submitNodeCollector.order(1).submitModel(
            this.parentModel,
            state,
            poseStack,
            renderType,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            tint,
            null,
            state.outlineColor,
            null
        );
    }

    override fun renderType(): RenderType = RenderTypes.lines()
}

class ProjectileRenderState : EntityRenderState() {
    companion object {
        const val TEXTURE_PATH = "textures/entity/projectile"
    }
    var xRot: Float = 0f
    var yRot: Float = 0f
    var texturePath: String = "default"
    var color: Int? = null
}