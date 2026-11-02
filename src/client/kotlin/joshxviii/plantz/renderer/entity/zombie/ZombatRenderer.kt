package joshxviii.plantz.renderer.entity.zombie

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.zombie.Zombat
import joshxviii.plantz.model.zombies.ZombatModel
import joshxviii.plantz.renderer.getTextureLocation
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.resources.Identifier

class ZombatRenderer(
    context: EntityRendererProvider.Context,
): MobRenderer<Zombat, ZombatRenderState, ZombatModel<ZombatRenderState>>(context,
    ZombatModel(context.bakeLayer(ZombatModel.LAYER_LOCATION)), 0.25f) {

    override fun submit(
        state: ZombatRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        super.submit(state, poseStack, submitNodeCollector, camera)
    }

    override fun extractRenderState(entity: Zombat, state: ZombatRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        state.isBaronBat = entity.isBaronBat
    }

    override fun createRenderState(): ZombatRenderState {
        return ZombatRenderState()
    }

    override fun getRenderType(state: ZombatRenderState, isBodyVisible: Boolean, forceTransparent: Boolean, appearGlowing: Boolean): RenderType? {
        return if (isBodyVisible && !forceTransparent) RenderTypes.entityCutoutCull(getTextureLocation(state))
        else super.getRenderType(state, isBodyVisible, forceTransparent, appearGlowing)
    }

    override fun getTextureLocation(state: ZombatRenderState): Identifier {
        val suffixes = mutableListOf<String>()
        if (state.isBaronBat) suffixes.add("baron")
        return state.getTextureLocation(PazZombieRenderState.TEXTURE_PATH, suffixes)
    }

}

class ZombatRenderState: PazZombieRenderState() {
    var isBaronBat = false
}
