package joshxviii.plantz.renderer.entity.zombie

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.entity.zombie.ZombieYeti
import joshxviii.plantz.model.zombies.PazZombieModel
import joshxviii.plantz.model.zombies.ZombieYetiModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.world.entity.AnimationState

class ZombieYetiRenderer(
    context: EntityRendererProvider.Context,
    private val model: PazZombieModel<PazZombieRenderState> = ZombieYetiModel(context.bakeLayer(ZombieYetiModel.LAYER_LOCATION)),
): PazZombieRenderer(context, model, model) {

    override fun submit(
        state: PazZombieRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        super.submit(state, poseStack, collector, camera)
    }

    override fun createRenderState(): PazZombieRenderState {
        return ZombieYetiRenderState()
    }

    override fun extractRenderState(entity: PazZombie, state: PazZombieRenderState, partialTicks: Float) {
        (state as ZombieYetiRenderState)
        (entity as ZombieYeti)
        super.extractRenderState(entity, state, partialTicks)
        state.bashAnimationState.copyFrom(entity.bashAnimation)
    }

}

class ZombieYetiRenderState: PazZombieRenderState() {
    val bashAnimationState: AnimationState = AnimationState()
}
