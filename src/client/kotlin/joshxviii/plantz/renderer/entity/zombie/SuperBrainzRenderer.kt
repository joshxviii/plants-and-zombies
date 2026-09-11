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
        extractCapeState(entity, state, partialTicks)
    }

    private fun extractCapeState(entity: PazZombie, state: SuperBrainzRenderState, partialTicks: Float) {
        if (entity !is SuperBrainz) return
        val deltaX = Mth.lerp(partialTicks, entity.xCapeO, entity.xCape) -
                Mth.lerp(partialTicks, entity.xo.toFloat(), entity.x.toFloat())
        val deltaY = Mth.lerp(partialTicks, entity.yCapeO, entity.yCape) -
                Mth.lerp(partialTicks, entity.yo.toFloat(), entity.y.toFloat())
        val deltaZ = Mth.lerp(partialTicks, entity.zCapeO, entity.zCape) -
                Mth.lerp(partialTicks, entity.zo.toFloat(), entity.z.toFloat())

        val yBodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot)

        val forwardX = Mth.sin(yBodyRot * (Math.PI / 180f))
        val forwardZ = -Mth.cos(yBodyRot * (Math.PI / 180f))

        var capeFlap = (deltaY * 20.0f)
        capeFlap = Mth.clamp(capeFlap, -6f, 32f)

        var capeLean = (deltaX * forwardX + deltaZ * forwardZ) * 200.0f
        capeLean = Mth.clamp(capeLean, 0f, 150f)

        var capeLean2 = (deltaX * forwardZ - deltaZ * forwardX) * 200.0f
        capeLean2 = Mth.clamp(capeLean2, -20f, 20f)

        val walkDistance = Mth.lerp(partialTicks, entity.walkDistO, entity.walkDist)
        capeFlap += Mth.sin(walkDistance * 2.0) * 32f * 0.3f

        state.capeFlap = capeFlap
        state.capeLean = capeLean
        state.capeLean2 = capeLean2
    }
}

class SuperBrainzRenderState: PazZombieRenderState() {
    var capeFlap: Float = 0f
    var capeLean: Float = 0f
    var capeLean2: Float = 0f
    val laserAttackAnimationState: AnimationState = AnimationState()
    val rightPunchAnimationState: AnimationState = AnimationState()
    val leftPunchAnimationState: AnimationState = AnimationState()
}
