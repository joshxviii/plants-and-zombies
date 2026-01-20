package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.gnome.Gnome
import joshxviii.plantz.entity.gnome.GnomeVariant
import joshxviii.plantz.model.GnomeModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.AnimationState

class GnomeRenderer(
    private val defaultModel: GnomeModel<GnomeRenderState>,
    context: EntityRendererProvider.Context,
) : MobRenderer<Gnome, GnomeRenderState, GnomeModel<GnomeRenderState>>(
    context,
    defaultModel,
    0.2f
) {
    init {
        this.addLayer(ItemInHandLayer(this))
    }

    override fun submit(
        state: GnomeRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        super.submit(state, poseStack, submitNodeCollector, camera)
    }

    override fun getTextureLocation(state: GnomeRenderState): Identifier = state.variant.getTexture()

    override fun createRenderState(): GnomeRenderState {
        return GnomeRenderState()
    }

    override fun extractRenderState(entity: Gnome, state: GnomeRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver, partialTicks)
        state.variant = entity.variant
    }
}

class GnomeRenderState : ArmedEntityRenderState() {
    var variant: GnomeVariant = GnomeVariant.BLUE
    val idleAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()
}