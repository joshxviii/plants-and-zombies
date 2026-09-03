package joshxviii.plantz.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.entity.zombie.RoboZombie
import joshxviii.plantz.model.zombies.RoboZombieModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.world.entity.AnimationState

class RoboZombieRenderer(
    context: EntityRendererProvider.Context,
    private val model: RoboZombieModel = RoboZombieModel(context.bakeLayer(RoboZombieModel.LAYER_LOCATION)),
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
        return RoboZombieRenderState()
    }

    override fun extractRenderState(entity: PazZombie, state: PazZombieRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        (state as RoboZombieRenderState)
        (entity as RoboZombie)
        state.isTankTransformation = entity.isTransformed
        state.idleAnimationState.copyFrom(entity.idleAnimation)
        state.shootAnimationState.copyFrom(entity.shootAnimation)
        state.bashAnimationState.copyFrom(entity.bashAnimation)
    }

}

class RoboZombieRenderState: PazZombieRenderState() {
    var isTankTransformation = false
    val idleAnimationState: AnimationState = AnimationState()
    val shootAnimationState: AnimationState = AnimationState()
    val bashAnimationState: AnimationState = AnimationState()
}
