package joshxviii.plantz.renderer.entity.zombie

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.entity.zombie.ZombieBat
import joshxviii.plantz.entity.zombie.ZombieYeti
import joshxviii.plantz.model.zombies.PazZombieModel
import joshxviii.plantz.model.zombies.PirateCaptainGhostModel
import joshxviii.plantz.model.zombies.ZombieBatModel
import joshxviii.plantz.model.zombies.ZombieYetiModel
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.getTextureLocation
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.AnimationState

class ZombieBatRenderer(
    context: EntityRendererProvider.Context,
): MobRenderer<ZombieBat, ZombieBatRenderState, ZombieBatModel<ZombieBatRenderState>>(context, ZombieBatModel(context.bakeLayer(ZombieBatModel.LAYER_LOCATION)), 0.25f) {

    override fun submit(
        state: ZombieBatRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        super.submit(state, poseStack, submitNodeCollector, camera)
    }

    override fun extractRenderState(entity: ZombieBat, state: ZombieBatRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
    }

    override fun createRenderState(): ZombieBatRenderState {
        return ZombieBatRenderState()
    }

    override fun getTextureLocation(state: ZombieBatRenderState): Identifier {
        return state.getTextureLocation(PazZombieRenderState.TEXTURE_PATH)
    }

}

class ZombieBatRenderState: PazZombieRenderState() {
}
