package joshxviii.plantz.renderer.entity.zombie

import joshxviii.plantz.entity.zombie.Gargantuar
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.model.zombies.GargantuarModel
import joshxviii.plantz.model.zombies.PazZombieModel
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.world.entity.AnimationState

class GargantuarRenderer(
    context: EntityRendererProvider.Context,
    private val model: PazZombieModel<PazZombieRenderState> = GargantuarModel(context.bakeLayer(GargantuarModel.LAYER_LOCATION)),
): PazZombieRenderer(context, model, model) {

    override fun createRenderState(): PazZombieRenderState {
        return GargantuarRenderState()
    }

    override fun extractRenderState(entity: PazZombie, state: PazZombieRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        (state as GargantuarRenderState)
        (entity as Gargantuar)
        state.punchAnimationState.copyFrom(entity.punchAttackAnimation)
        state.smashAnimationState.copyFrom(entity.smashAttackAnimation)
        state.throwImpAnimationState.copyFrom(entity.throwImpAnimation)
    }

}

class GargantuarRenderState: PazZombieRenderState() {
    val punchAnimationState: AnimationState = AnimationState()
    val smashAnimationState: AnimationState = AnimationState()
    val throwImpAnimationState: AnimationState = AnimationState()
}
