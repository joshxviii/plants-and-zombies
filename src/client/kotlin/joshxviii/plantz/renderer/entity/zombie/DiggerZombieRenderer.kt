package joshxviii.plantz.renderer.entity.zombie

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.zombie.DiggerZombie
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.entity.zombie.RoboZombie
import joshxviii.plantz.entity.zombie.ZombieYeti
import joshxviii.plantz.model.zombies.DiggerZombieModel
import joshxviii.plantz.model.zombies.PazZombieModel
import joshxviii.plantz.model.zombies.RoboZombieModel
import joshxviii.plantz.model.zombies.ZombieYetiModel
import joshxviii.plantz.toAngle
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.util.Mth
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.math.sqrt

class DiggerZombieRenderer(
    context: EntityRendererProvider.Context,
    private val model: PazZombieModel<PazZombieRenderState> = DiggerZombieModel(context.bakeLayer(DiggerZombieModel.LAYER_LOCATION)),
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
        return DiggerZombieRenderState()
    }

    override fun getShadowRadius(state: PazZombieRenderState): Float {
        (state as DiggerZombieRenderState)
        val r = super.getShadowRadius(state)
        return if (state.tunnelTime > 0) r * abs((state.tunnelTime.toFloat() / (DiggerZombie.TUNNELING_DURATION * 0.5f)) - 1f)
        else r
    }

    override fun extractRenderState(entity: PazZombie, state: PazZombieRenderState, partialTicks: Float) {
        (state as DiggerZombieRenderState)
        (entity as DiggerZombie)
        super.extractRenderState(entity, state, partialTicks)
        state.actionAnimationState.copyFrom(entity.mineBlockAnimation)
        state.tunnelAnimationState.copyFrom(entity.tunnelAnimation)
        state.tunnelTime = entity.tunnelTime
        state.isTunnelingUp = (entity.tunnelTime > DiggerZombie.TUNNELING_DURATION * 0.5)
    }

}

class DiggerZombieRenderState: PazZombieRenderState() {
    val tunnelAnimationState: AnimationState = AnimationState()
    var tunnelTime: Int = 0
    var isTunnelingUp: Boolean = true
}
