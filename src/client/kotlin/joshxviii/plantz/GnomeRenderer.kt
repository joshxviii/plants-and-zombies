package joshxviii.plantz

import joshxviii.plantz.entity.gnome.Gnome
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.AnimationState

class GnomeRenderer (
    private val defaultModel: EntityModel<GnomeRenderState>,
    context: EntityRendererProvider.Context,
) : MobRenderer<Gnome, GnomeRenderState, EntityModel<GnomeRenderState>>(
    context,
    defaultModel,
    0.5f
) {
    override fun getTextureLocation(state: GnomeRenderState): Identifier {
        TODO("Not yet implemented")
    }

    override fun createRenderState(): GnomeRenderState {
        return GnomeRenderState()
    }

    override fun extractRenderState(entity: Gnome, state: GnomeRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
    }

}

class GnomeRenderState : LivingEntityRenderState() {
    var texturePath: String = "default"
    val idleAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()
}