package joshxviii.plantz.renderer.entity.zombie

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.PazConfig
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.entity.zombie.*
import joshxviii.plantz.model.zombies.PazZombieModel
import joshxviii.plantz.renderer.getEmissiveTextureLocation
import joshxviii.plantz.renderer.getTextureLocation
import joshxviii.plantz.renderer.validMagicName
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelLayers
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
    val defaultModel: PazZombieModel<PazZombieRenderState> = PazZombieModel(null, context.bakeLayer(PazZombieModel.LAYER_LOCATION)),
    val babyModel: PazZombieModel<PazZombieRenderState> = PazZombieModel(null, context.bakeLayer(ModelLayers.ZOMBIE_BABY)),
    armorSet: ArmorModelSet<ModelLayerLocation> = ModelLayers.ZOMBIE_ARMOR,
    babyArmorSet: ArmorModelSet<ModelLayerLocation> = ModelLayers.ZOMBIE_BABY_ARMOR
) : AbstractZombieRenderer<PazZombie, PazZombieRenderState, PazZombieModel<PazZombieRenderState>>(
    context,
    defaultModel,
    babyModel,
    ArmorModelSet.bake(armorSet, context.modelSet) { defaultModel.forArmor(it) },
    ArmorModelSet.bake(babyArmorSet, context.modelSet) { babyModel.forArmor(it) }
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
            true, -1, camera
        )
        if (state.zombieState != ZombieState.EMERGING || state.ageInTicks>1) super.submit(state, poseStack, collector, camera)
    }

    override fun shouldRenderLayers(state: PazZombieRenderState): Boolean {
        return super.shouldRenderLayers(state)
    }
    
    override fun createRenderState(): PazZombieRenderState {
        return PazZombieRenderState()
    }

    override fun getShadowRadius(state: PazZombieRenderState): Float {
        return super.getShadowRadius(state)
    }

    override fun extractRenderState(entity: PazZombie, state: PazZombieRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        state.headOnly = false
        state.zombieState = entity.state
        state.isFlying = entity.isFlying
        state.emergeAnimationState.copyFrom(entity.emergeAnimation)
        state.retreatAnimationState.copyFrom(entity.retreatAnimation)
        state.floatAnimationState.copyFrom(entity.floatAnimation)
        state.movementDirection = Mth.lerp(partialTicks.toDouble() * .5, entity.moveDirO, entity.moveDir)
        if (entity is DiscoZombie) state.actionAnimationState.copyFrom(entity.summonAnimation)
        if (entity is EngineerZombie) state.actionAnimationState.copyFrom(entity.buildAnimation)
        if (entity is GraveDigger) state.actionAnimationState.copyFrom(entity.digAnimation)
        if (entity is AllStar) state.actionAnimationState.copyFrom(entity.chargeAnimation)
        if (entity is ZombieYeti) state.actionAnimationState.copyFrom(entity.bashAnimation)
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
                is AllStar -> add(entity.variant.suffix)
                is RoboZombie -> add(entity.variant.suffix)
                is SuperBrainz -> add(entity.variant.suffix)
                is ElectricSlide -> add(entity.variant.suffix)
                else -> {}
            }
        }
        extractCapeState(entity, state, partialTicks)
    }
    private fun extractCapeState(entity: PazZombie, state: PazZombieRenderState, partialTicks: Float) {
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

        val walkDistance = Mth.lerp(partialTicks, entity.moveDirO.horizontalDistance().toFloat(), entity.moveDir.horizontalDistance().toFloat())
        capeFlap += Mth.sin(walkDistance * 2.0) * 32f * 0.3f

        state.capeFlap = capeFlap
        state.capeLean = capeLean
        state.capeLean2 = capeLean2
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
        submitNodeCollector.order(1).submitModel(this.parentModel, state, poseStack, renderType, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }

    override fun renderType(): RenderType = RenderTypes.lines()
}

open class PazZombieRenderState : ZombieRenderState() {

    companion object {
        const val TEXTURE_PATH = "textures/entity/zombie"
    }

    var headOnly: Boolean = false
    var movementDirection: Vec3 = Vec3.ZERO
    var customName: String = ""
    var textureExtra: List<String> = listOf()
    var isAngry: Boolean = false
    var isFlying: Boolean = false
    var zombieState: ZombieState = ZombieState.IDLE
    val emergeAnimationState: AnimationState = AnimationState()
    val retreatAnimationState: AnimationState = AnimationState()
    val floatAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()

    var capeFlap: Float = 0f
    var capeLean: Float = 0f
    var capeLean2: Float = 0f

    fun getSuffixes(): MutableList<String> {
        val magicName = this.validMagicName(customName)
        val suffixes = mutableListOf<String>().apply {
            if (magicName!=null)      add(magicName)
            else {
                textureExtra.forEach  { add(it) }
                if (isBaby)           add("baby")
            }
        }.filter { it.isNotEmpty() }.toMutableList()
        return suffixes
    }
}
