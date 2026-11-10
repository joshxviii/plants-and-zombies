package joshxviii.plantz.renderer.entity.zombie

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.entity.zombie.SuperBrainz
import joshxviii.plantz.model.zombies.PazZombieModel
import joshxviii.plantz.model.zombies.SuperBrainzModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.util.Mth
import net.minecraft.world.entity.AnimationState

class SuperBrainzRenderer(
    context: EntityRendererProvider.Context,
    private val model: PazZombieModel<PazZombieRenderState> = SuperBrainzModel(context.bakeLayer(SuperBrainzModel.LAYER_LOCATION)),
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
        return SuperBrainzRenderState()
    }

    override fun extractRenderState(entity: PazZombie, state: PazZombieRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        (state as SuperBrainzRenderState)
        (entity as SuperBrainz)
        state.laserAttackAnimationState.copyFrom(entity.laserAttackAnimation)
        state.rightPunchAnimationState.copyFrom(entity.rightPunchAnimation)
        state.leftPunchAnimationState.copyFrom(entity.leftPunchAnimation)
    }

}

class SuperBrainzRenderState: PazZombieRenderState() {
    val laserAttackAnimationState: AnimationState = AnimationState()
    val rightPunchAnimationState: AnimationState = AnimationState()
    val leftPunchAnimationState: AnimationState = AnimationState()
}
