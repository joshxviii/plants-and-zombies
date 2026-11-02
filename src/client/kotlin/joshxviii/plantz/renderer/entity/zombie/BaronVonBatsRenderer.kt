package joshxviii.plantz.renderer.entity.zombie

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.zombie.BaronVonBats
import joshxviii.plantz.entity.zombie.DiggerZombie
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.model.zombies.BaronVonBatsModel
import joshxviii.plantz.model.zombies.PazZombieModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.world.entity.AnimationState
import kotlin.math.abs

class BaronVonBatsRenderer(
    context: EntityRendererProvider.Context,
    private val model: PazZombieModel<PazZombieRenderState> = BaronVonBatsModel(context.bakeLayer(BaronVonBatsModel.LAYER_LOCATION)),
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
        return BaronVonBatsRenderState()
    }

    override fun getShadowRadius(state: PazZombieRenderState): Float {
        (state as BaronVonBatsRenderState)
        val r = super.getShadowRadius(state)
        return if (state.teleportTime > 0) r * abs((state.teleportTime.toFloat() / (BaronVonBats.TELEPORT_DURATION * 0.5f)) - 1f)
        else r
    }

    override fun extractRenderState(entity: PazZombie, state: PazZombieRenderState, partialTicks: Float) {
        (state as BaronVonBatsRenderState)
        (entity as BaronVonBats)
        super.extractRenderState(entity, state, partialTicks)
        state.actionAnimationState.copyFrom(entity.summonAnimation)
        state.teleportAnimationState.copyFrom(entity.teleportAnimation)
        state.teleportTime = entity.teleportTime
        state.isAppearing = (entity.teleportTime > BaronVonBats.TELEPORT_DURATION * 0.5)
    }

}

class BaronVonBatsRenderState: PazZombieRenderState() {
    val teleportAnimationState: AnimationState = AnimationState()
    var teleportTime: Int = 0
    var isAppearing: Boolean = false
}
