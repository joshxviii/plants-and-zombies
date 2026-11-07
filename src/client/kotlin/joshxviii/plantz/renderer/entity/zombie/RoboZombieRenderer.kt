package joshxviii.plantz.renderer.entity.zombie

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.entity.zombie.RoboZombie
import joshxviii.plantz.model.zombies.PazZombieModel
import joshxviii.plantz.model.zombies.RoboZombieModel
import joshxviii.plantz.toAngle
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.util.Mth
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.phys.Vec3
import kotlin.math.sqrt

class RoboZombieRenderer(
    context: EntityRendererProvider.Context,
    private val model: PazZombieModel<PazZombieRenderState> = RoboZombieModel(context.bakeLayer(RoboZombieModel.LAYER_LOCATION)),
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
        (state as RoboZombieRenderState)
        (entity as RoboZombie)
        super.extractRenderState(entity, state, partialTicks)
        updateTreadRotation(entity, state, partialTicks)
        state.isTankTransformation = entity.isTransformed
        state.transformAnimationState.copyFrom(entity.transformAnimation)
        state.idleAnimationState.copyFrom(entity.idleAnimation)
        state.shootAnimationState.copyFrom(entity.shootAnimation)
        state.actionAnimationState.copyFrom(entity.bashAnimation)

        state.treadYaw = entity.treadYaw
        state.treadPitch = entity.treadPitch
    }

    private fun updateTreadRotation(entity: RoboZombie, state: RoboZombieRenderState, partialTicks: Float) {
        if (!entity.isTransformed) {
            entity.treadYaw = 0.0f
            entity.treadPitch = 0.0f
            return
        }

        val yBodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot)
        val rotDelta = yBodyRot - entity.yBodyRotO

        val dx = entity.x - entity.xo
        val dy = entity.y - entity.yo
        val dz = entity.z - entity.zo

        val distance = sqrt(dx * dx + dy * dy + dz * dz)
        val isMoving = distance > 0.0

        val horizontalDir = (if (isMoving) Mth.wrapDegrees(-yBodyRot + Vec3(dx, 0.0, dz).toAngle() - 90) else rotDelta*2) * Mth.DEG_TO_RAD
        val verticalDir = if (isMoving) -Mth.sin(dy / distance) else 0f

        entity.treadYaw = Mth.rotLerpRad(partialTicks * if (isMoving) 0.095f else 0.05f, entity.treadYaw, horizontalDir)
        entity.treadPitch = Mth.rotLerpRad(partialTicks * 0.085f, entity.treadPitch, verticalDir)
    }

}

class RoboZombieRenderState: PazZombieRenderState() {
    var isTankTransformation = false
    var treadYaw: Float = 0.0f
    var treadPitch: Float = 0.0f
    val transformAnimationState: AnimationState = AnimationState()
    val idleAnimationState: AnimationState = AnimationState()
    val shootAnimationState: AnimationState = AnimationState()
}
