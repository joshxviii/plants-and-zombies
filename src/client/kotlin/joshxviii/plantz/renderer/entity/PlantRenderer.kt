package joshxviii.plantz.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.PazConfig
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.entity.plant.*
import joshxviii.plantz.renderer.getEmissiveTextureLocation
import joshxviii.plantz.renderer.getTextureLocation
import joshxviii.plantz.renderer.isMagicName
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.EyesLayer
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import kotlin.math.pow

class PlantRenderer(
    private val defaultModel: EntityModel<PlantRenderState>,
    context: EntityRendererProvider.Context,
    private val babyModel: EntityModel<PlantRenderState>? = null,
) : MobRenderer<Plant, PlantRenderState, EntityModel<PlantRenderState>>(
    context,
    defaultModel,
    0.5f
) {
    init {
        addLayer(EmissivePlantLayer(this))
    }

    override fun submit(
        state: PlantRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {

        // debug info text
        if (PazConfig.SHOW_DEBUG_INFO) collector.submitNameTag(
            poseStack, Vec3(0.0,state.eyeHeight.toDouble(),0.0), -20,
            Component.literal("${state.plantState.name}, ${state.cooldown}").withColor(0xFFFFFFF),
            true, -1, 20.0, camera
        )

        model = if (state.isBaby && babyModel != null) babyModel else defaultModel
        if (state.plantState != PlantState.INIT || state.ageInTicks>1) super.submit(state, poseStack, collector, camera)
    }

    override fun getShadowRadius(state: PlantRenderState): Float {
        return if (state.rotations == Quaternionf()) super.getShadowRadius(state) * (0.9f) else 0f
    }

    override fun scale(state: PlantRenderState, poseStack: PoseStack) {
        super.scale(state, poseStack)
        var g = state.swelling
        val wobble = 1.0f + Mth.sin((g * 100.0f).toDouble()) * g * 0.01f
        g = Mth.clamp(g, 0.0f, 1.0f)
        val s = (1.0f + g.pow(6) * 0.4f) * wobble
        val hs = (1.0f + g.pow(6) * 0.1f) / wobble
        poseStack.scale(s, hs, s)
    }

    override fun getWhiteOverlayProgress(state: PlantRenderState): Float {
        val step = state.swelling
        return if ((step * 10.0f).toInt() % 2 == 0) 0.0f else Mth.clamp(step, 0.5f, 1.0f)
    }

    override fun createRenderState(): PlantRenderState = PlantRenderState()

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)
        val attached = entity.attachedEntity
        state.rotations = if (attached != null) {
            val pitch = -Mth.lerp(partialTick, attached.xRotO, attached.xRot)
            val yaw = Mth.lerp(partialTick, attached.yRotO, attached.yRot)
            Quaternionf()
                .rotateY(Mth.DEG_TO_RAD * (180.0f - yaw))
                .rotateX(Mth.DEG_TO_RAD * pitch)
        } else {
            Quaternionf()
        }
        state.plantState = entity.state
        if (entity is ExplosivePlant) state.swelling = entity.getSwelling(partialTick)
        state.cooldown = entity.cooldown
        state.isAsleep = entity.isAsleep
        state.damagedAmount = entity.damagedPercent
        state.initAnimationState.copyFrom(entity.initAnimationState)
        state.idleAnimationState.copyFrom(entity.idleAnimationState)
        state.actionAnimationState.copyFrom(entity.actionAnimationState)
        state.coolDownAnimationState.copyFrom(entity.coolDownAnimationState)
        state.specialAnimation.copyFrom(entity.specialAnimation)
        state.sleepAnimationState.copyFrom(entity.sleepAnimationState)
        state.bounceAnimationState.copyFrom(entity.bounceAnimation)
        state.customName = entity.customName?.string ?: ""
        state.useSpecialAction =
            when (entity) {
                is BonkChoy -> entity.useUppercut
                else -> false
            }
        state.textureExtra = mutableListOf<String>().apply {
            when (entity) {
                is WallNut, is ExplodeONut -> add(when {
                    state.damagedAmount >= 0.75f -> "damage_medium"
                    state.damagedAmount >= 0.5f -> "damage_low"
                    else -> ""
                })

                is KernelPult -> if (entity.hasButterShot) add("butter")
                else -> {}
            }
        }
    }

    override fun getTextureLocation(state: PlantRenderState): Identifier {
        val texture = state.getTextureLocation(PlantRenderState.TEXTURE_PATH, state.getSuffixes())
        return texture
    }
}

class EmissivePlantLayer<M : EntityModel<PlantRenderState>>(
    renderer: RenderLayerParent<PlantRenderState, M>,
) : EyesLayer<PlantRenderState, M>(renderer) {

    override fun submit(
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        lightCoords: Int,
        state: PlantRenderState,
        yRot: Float,
        xRot: Float
    ) {
        val textureLocation = state.getEmissiveTextureLocation(PlantRenderState.TEXTURE_PATH, state.getSuffixes()) ?: return
        val renderType = RenderTypes.eyes(textureLocation)
        submitNodeCollector.order(1).submitModel(this.parentModel, state, poseStack, renderType, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
    }

    override fun renderType(): RenderType = RenderTypes.lines()
}


class PlantRenderState : LivingEntityRenderState() {
    companion object {
        const val TEXTURE_PATH = "textures/entity/plant"
    }
    var rotations: Quaternionf = Quaternionf()
    var swelling: Float = 0f
    var partialTick: Float = 0f
    var cooldown: Int = 0
    var damagedAmount: Float = 0.0f
    var isAsleep: Boolean = false
    var customName: String = ""
    var textureExtra: List<String> = listOf()
    var plantState: PlantState = PlantState.IDLE
    var useSpecialAction: Boolean = false
    val initAnimationState: AnimationState = AnimationState()
    val idleAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()
    val coolDownAnimationState: AnimationState = AnimationState()
    val specialAnimation: AnimationState = AnimationState()
    val sleepAnimationState: AnimationState = AnimationState()
    val bounceAnimationState: AnimationState = AnimationState()

    fun getSuffixes(): MutableList<String> {
        val magicName = this.isMagicName(customName)
        val suffixes = mutableListOf<String>().apply {
            textureExtra.forEach                { add(it) }
            if (magicName.isNotEmpty())         add(magicName)
            else {
                if (isBaby)                     add("baby")
                if (isAsleep)                   add("sleep")
            }
        }.filter { it.isNotEmpty() }.toMutableList()
        return suffixes
    }
}
