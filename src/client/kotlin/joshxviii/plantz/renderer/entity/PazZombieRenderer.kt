package joshxviii.plantz.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.PazConfig
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.entity.zombie.*
import joshxviii.plantz.model.zombies.PazZombieModel
import joshxviii.plantz.renderer.getEmissiveTextureLocation
import joshxviii.plantz.renderer.getTextureLocation
import joshxviii.plantz.renderer.isMagicName
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.AbstractZombieRenderer
import net.minecraft.client.renderer.entity.ArmorModelSet
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.EyesLayer
import net.minecraft.client.renderer.entity.state.ZombieRenderState
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.phys.Vec3

open class PazZombieRenderer(
    context: EntityRendererProvider.Context,
    private val defaultModel: PazZombieModel = PazZombieModel(null, context.bakeLayer(PazZombieModel.LAYER_LOCATION)),
    private val babyModel: PazZombieModel = PazZombieModel(null, context.bakeLayer(ModelLayers.ZOMBIE_BABY)),
    armorSet: ArmorModelSet<ModelLayerLocation> = ModelLayers.ZOMBIE_ARMOR,
    babyArmorSet: ArmorModelSet<ModelLayerLocation> = ModelLayers.ZOMBIE_BABY_ARMOR
) : AbstractZombieRenderer<PazZombie, PazZombieRenderState, PazZombieModel>(
    context,
    defaultModel,
    babyModel,
    ArmorModelSet.bake<PazZombieModel>(armorSet, context.modelSet) { root: ModelPart -> PazZombieModel(null, root) },
    ArmorModelSet.bake<PazZombieModel>(babyArmorSet, context.modelSet) { root: ModelPart -> PazZombieModel(null, root) }
) {

    init {
        addLayer(EmissiveZombieLayer(this))
    }

    override fun submit(
        state: PazZombieRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        // debug info text
        if (PazConfig.SHOW_DEBUG_INFO) collector.submitNameTag(
            poseStack, Vec3(0.0,state.eyeHeight.toDouble(),0.0), -20,
            Component.literal("${state.zombieState.name}").withColor(0xFFFFFFF),
            true, -1, 20.0, camera
        )
        if (state.zombieState != ZombieState.EMERGING || state.ageInTicks>1) super.submit(state, poseStack, collector, camera)
    }

    override fun createRenderState(): PazZombieRenderState {
        return PazZombieRenderState()
    }

    override fun getShadowRadius(state: PazZombieRenderState): Float {
        return super.getShadowRadius(state)
    }

    override fun extractRenderState(entity: PazZombie, state: PazZombieRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        state.zombieState = entity.state
        state.emergeAnimationState.copyFrom(entity.emergeAnimation)
        state.floatAnimationState.copyFrom(entity.floatAnimation)
        state.movementDirection = Mth.lerp(partialTicks.toDouble() * .5, entity.moveDirO, entity.moveDir)
        if (entity is DiscoZombie) state.actionAnimationState.copyFrom(entity.summonAnimation)
        if (entity is AllStar) state.actionAnimationState.copyFrom(entity.chargeAnimation)
        if (entity is NewspaperZombie) state.isAngry = entity.isAngry()
        state.customName = entity.customName?.string ?: ""
        state.textureExtra = mutableListOf<String>().apply {
            when (entity) {
                is Gargantuar -> {
                    add(entity.variant.suffix)
                    if (entity.hasImp) add("imp")
                }
                is NewspaperZombie -> if (entity.isAngry()) add("angry")
                is BrownCoat -> add(entity.variant.suffix)
                is Imp -> {
                    add(entity.variant.suffix)
                    if (entity.hasBarrel) add("barrel")
                }
                is SuperBrainz -> add(entity.variant.suffix)
                else -> {}
            }
        }
    }

    override fun getTextureLocation(state: PazZombieRenderState): Identifier {
        val texture = state.getTextureLocation(PazZombieRenderState.TEXTURE_PATH, state.getSuffixes())
        return texture
    }
}

class EmissiveZombieLayer<M : EntityModel<PazZombieRenderState>>(
    renderer: RenderLayerParent<PazZombieRenderState, M>,
) : EyesLayer<PazZombieRenderState, M>(renderer) {

    override fun submit(
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        lightCoords: Int,
        state: PazZombieRenderState,
        yRot: Float,
        xRot: Float
    ) {
        val textureLocation = state.getEmissiveTextureLocation(PazZombieRenderState.TEXTURE_PATH, state.getSuffixes()) ?: return
        val renderType = RenderTypes.eyes(textureLocation)
        submitNodeCollector.order(1).submitModel(this.parentModel, state, poseStack, renderType, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
    }

    override fun renderType(): RenderType = RenderTypes.lines()
}

open class PazZombieRenderState : ZombieRenderState() {

    companion object {
        const val TEXTURE_PATH = "textures/entity/zombie"
    }

    var movementDirection: Vec3 = Vec3.ZERO
    var customName: String = ""
    var textureExtra: List<String> = listOf()
    var isAngry: Boolean = false
    var zombieState: ZombieState = ZombieState.IDLE
    val emergeAnimationState: AnimationState = AnimationState()
    val floatAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()

    fun getSuffixes(): MutableList<String> {
        val magicName = this.isMagicName(customName)
        val suffixes = mutableListOf<String>().apply {
            textureExtra.forEach                { add(it) }
            if (isBaby)                         add("baby")
            else if (magicName.isNotEmpty())    add(magicName)
        }.filter { it.isNotEmpty() }.toMutableList()
        return suffixes
    }
}
