package joshxviii.plantz.renderer.entity.plant

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.PazConfig
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.entity.plant.*
import joshxviii.plantz.model.plants.PlantModel
import joshxviii.plantz.renderer.entity.plant.PlantRenderState.Companion.TEXTURE_PATH
import joshxviii.plantz.renderer.PlantHeadAttachmentRenderLayer
import joshxviii.plantz.renderer.getEmissiveTextureLocation
import joshxviii.plantz.renderer.getTextureLocation
import joshxviii.plantz.renderer.validMagicName
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.EyesLayer
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.Mth
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import kotlin.math.pow
import kotlin.math.sqrt

open class PlantRenderer(
    private val defaultModel: EntityModel<PlantRenderState>,
    val context: EntityRendererProvider.Context,
    private val babyModel: EntityModel<PlantRenderState>? = null,
) : MobRenderer<Plant, PlantRenderState, EntityModel<PlantRenderState>>(
    context,
    defaultModel,
    0.5f
) {

    companion object {
        private val rollDelta = Quaternionf()

        private fun updateClientRoll(entity: Plant, partialTick: Float) {
            if (Minecraft.getInstance().isPaused) return
            val tickScale = entity.level().tickRateManager().tickrate() / 20f

            if (!entity.rolling) {
                entity.rollRotation.identity()
                return
            }

            val dx = (entity.x - entity.xo).toFloat()
            val dz = (entity.z - entity.zo).toFloat()
            val distance = sqrt(dx * dx + dz * dz) * tickScale
            if (distance < 1e-4f) return

            val yawRad = Mth.DEG_TO_RAD * (180.0f - entity.yRot).toDouble()
            val cos = Mth.cos(yawRad)
            val sin = Mth.sin(-yawRad)

            val localDx = dx * cos + dz * sin
            val localDz = dx * sin + -dz * cos

            val radius = sqrt(16.0f * entity.scale)
            val angle = distance / radius

            var axisX = localDz
            var axisZ = -localDx
            val axisLen = sqrt(axisX * axisX + axisZ * axisZ)
            if (axisLen < 1e-4f) return
            axisX /= axisLen
            axisZ /= axisLen

            rollDelta.fromAxisAngleRad(axisX, 0f, axisZ, angle)
            rollDelta.mul(entity.rollRotation, entity.rollRotation)
            entity.rollRotation.normalize()
        }
    }

    init {
        addLayer(EmissivePlantLayer(this))
        addLayer(SunGlowLayer(this))
        if (this !is PumpkinRenderer) addLayer(PumpkinArmorLayer(context, this))
    }

    override fun submit(
        state: PlantRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        if (state.renderedByWearer) return
        // debug info text
        if (PazConfig.SHOW_DEBUG_INFO) collector.submitNameTag(
            poseStack, Vec3(0.0,state.eyeHeight.toDouble(),0.0), -20,
            Component.literal("${state.plantState.name}, ${state.cooldown}").withColor(0xFFFFFFF),
            true, -1, camera
        )

        if (babyModel != null) model = if (state.isBaby) babyModel else defaultModel
        super.submit(state, poseStack, collector, camera)
    }

    override fun getFlipDegrees(): Float = 0f

    override fun getShadowRadius(state: PlantRenderState): Float {
        return if (!state.embeddedOnWearer && state.hasIdentityRotation()) super.getShadowRadius(state) * (0.9f) else 0f
    }

    override fun scale(state: PlantRenderState, poseStack: PoseStack) {
        super.scale(state, poseStack)
        var g = state.swelling
        val wobble = 1.0f + Mth.sin((g * 100.0f).toDouble()) * g * 0.01f
        g = Mth.clamp(g, 0.0f, 1.0f)
        val s = (1.0f + g.pow(6) * 0.4f) * wobble
        val hs = (1.0f + g.pow(6) * 0.1f) / wobble
        poseStack.scale(s, hs, s)

        val deathScale = (1f - state.deathTime / 20f).pow(0.3f)
        poseStack.scale(deathScale, deathScale, deathScale)
    }

    override fun getModelTint(state: PlantRenderState): Int =
        if (state.silhouette) 0xFF000000.toInt() else super.getModelTint(state)

    override fun getWhiteOverlayProgress(state: PlantRenderState): Float {
        val step = state.swelling
        return if ((step * 10.0f).toInt() % 2 == 0) 0.0f else Mth.clamp(step, 0.5f, 1.0f)
    }

    override fun createRenderState(): PlantRenderState = PlantRenderState()

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)
        val attached = entity.attachedEntity
        state.renderedByWearer = attached != null && PlantHeadAttachmentRenderLayer.wearerRendersPlant(attached)
        state.rotations.identity()
        if (attached != null) {
            val pitch = -Mth.lerp(partialTick, attached.xRotO, attached.xRot)
            val yaw = Mth.lerp(partialTick, attached.yRotO, attached.yRot)
            state.rotations.rotateY(Mth.DEG_TO_RAD * (180.0f - yaw)).rotateX(Mth.DEG_TO_RAD * pitch)
        }
        state.plantState = entity.state
        if (entity is ExplosivePlant) state.swelling = entity.getSwelling(partialTick)
        state.cooldown = entity.cooldown
        state.isAsleep = entity.isAsleep
        state.pumpkinArmor = entity.pumpkinArmor
        state.damagedAmount = entity.damagedPercent

        updateClientRoll(entity, partialTick)
        state.isRolling = entity.rolling
        state.rollRotation.set(entity.rollRotation)

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
                is Chomper -> entity.isChewing
                is Citron -> entity.rolling
                else -> false
            }
        state.textureExtra = when (entity) {
            is WallNut -> listOf(when {
                state.damagedAmount >= 0.75f -> "damage_medium"
                state.damagedAmount >= 0.5f -> "damage_low"
                else -> ""
            })
            is KernelPult -> if (entity.hasButterShot) listOf("butter") else emptyList()
            is Marigold -> if (entity.color != MarigoldColor.getDefault()) listOf(entity.color.name.lowercase()) else emptyList()
            else -> emptyList()
        }
        state.glowPercent = when (entity) {
            is Sunflower, is SunShroom -> {
                if (entity.state != PlantState.COOLDOWN || entity.isAsleep || entity.isGrowingSeeds) 0f
                else 1f - ((state.cooldown).coerceIn(0, 100) / 100f)
            }
            is Marigold -> if (!entity.isGrowingSeeds) 0f else entity.receivedSun / entity.sunRequiredForSeeds().toFloat()
            is ExplosivePlant -> entity.getSwelling(partialTick)
            else -> 0f
        }
        state.prepareTextures()
    }

    override fun getTextureLocation(state: PlantRenderState): Identifier =
        state.bodyTexture ?: state.getTextureLocation(TEXTURE_PATH, state.bodySuffixes)
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
        if (state.silhouette) return
        val textureLocation = state.emissiveTexture ?: return
        submitNodeCollector.order(1).submitModel(this.parentModel, state, poseStack, RenderTypes.eyes(textureLocation), lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor)
    }

    override fun renderType(): RenderType = RenderTypes.lines()
}

class SunGlowLayer<M : EntityModel<PlantRenderState>>(
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
        if (state.silhouette) return
        val alphaPercent = state.glowPercent
        if (alphaPercent <= 0f) return
        val glowTexture = state.glowTexture ?: return
        val color = ARGB.color(Mth.floor(alphaPercent * 0xFF), 0xFFFFFF)
        submitNodeCollector.submitModel(this.parentModel, state, poseStack, RenderTypes.eyes(glowTexture), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color, null, -1)
    }
    override fun renderType(): RenderType = RenderTypes.lines()
}

class PumpkinArmorLayer<M : EntityModel<PlantRenderState>>(
    val context: EntityRendererProvider.Context,
    val renderer: RenderLayerParent<PlantRenderState, M>,
    val pumpkinArmorRenderer: PumpkinRenderer = PumpkinRenderer(context)
): RenderLayer<PlantRenderState, M>(renderer) {
    private val pumpkinState = PumpkinRenderState()
    private val cameraState = CameraRenderState()

    override fun submit(
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        lightCoords: Int,
        state: PlantRenderState,
        yRot: Float,
        xRot: Float
    ) {
        if (state.pumpkinArmor <= 0 || state.silhouette) return
        poseStack.pushPose()
        val onHat = (parentModel as? PlantModel)?.applyLocatorHat(poseStack) ?: false
        poseStack.scale(-1.0f, -1.0f, 1.0f)
        if (!onHat) poseStack.translate(0.0f, -1.503f, 0.0f)
        poseStack.rotate(Axis.YP.rotationDegrees(-180f))

        pumpkinState.helmetMode = onHat
        pumpkinState.lightCoords = state.lightCoords
        pumpkinState.ageInTicks = state.ageInTicks
        pumpkinState.isAsleep = state.isAsleep
        pumpkinState.sleepAnimationState.copyFrom(state.sleepAnimationState)
        pumpkinState.idleAnimationState.copyFrom(state.idleAnimationState)
        pumpkinState.prepareTextures()
        pumpkinArmorRenderer.submit(pumpkinState, poseStack, submitNodeCollector, cameraState)
        poseStack.popPose()
    }
}

open class PlantRenderState : LivingEntityRenderState() {
    companion object {
        const val TEXTURE_PATH = "textures/entity/plant"
    }

    var pumpkinArmor: Float = 0f
    var silhouette: Boolean = false
    var renderedByWearer: Boolean = false
    var embeddedOnWearer: Boolean = false
    var rotations: Quaternionf = Quaternionf()
    var swelling: Float = 0f
    var partialTick: Float = 0f
    var cooldown: Int = 0
    var damagedAmount: Float = 0.0f
    var isAsleep: Boolean = false
    var customName: String = ""
    var textureExtra: List<String> = emptyList()
    var glowPercent: Float = 0f
    var plantState: PlantState = PlantState.IDLE
    var useSpecialAction: Boolean = false
    var isRolling: Boolean = false
    var rollRotation: Quaternionf = Quaternionf()
    val initAnimationState: AnimationState = AnimationState()
    val idleAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()
    val coolDownAnimationState: AnimationState = AnimationState()
    val specialAnimation: AnimationState = AnimationState()
    val sleepAnimationState: AnimationState = AnimationState()
    val bounceAnimationState: AnimationState = AnimationState()

    var bodySuffixes: List<String> = emptyList()
        private set
    var bodyTexture: Identifier? = null
        private set
    var emissiveTexture: Identifier? = null
        private set
    var glowTexture: Identifier? = null
        private set

    fun hasIdentityRotation(): Boolean =
        rotations.x == 0f && rotations.y == 0f && rotations.z == 0f && rotations.w == 1f

    /**
     * Resolves every texture this state needs. Call once per extraction, after the fields above are set,
     */
    fun prepareTextures() {
        val magicName = validMagicName(customName)
        bodySuffixes = buildList {
            textureExtra.forEach { if (it.isNotEmpty()) add(it) }
            if (isBaby) add("baby")
            if (isAsleep || deathTime > 0) add("sleep")
            if (magicName != null) add(magicName)
        }
        bodyTexture = getTextureLocation(TEXTURE_PATH, bodySuffixes)
        emissiveTexture = getEmissiveTextureLocation(TEXTURE_PATH, bodySuffixes)
        glowTexture = if (glowPercent > 0f) getTextureLocation(TEXTURE_PATH, bodySuffixes + "glow") else null
    }
}
