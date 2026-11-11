package joshxviii.plantz.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazModels
import joshxviii.plantz.PlantHeadAttachment
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.renderer.entity.plant.PlantRenderState
import joshxviii.plantz.renderer.entity.plant.PlantRenderer
import net.minecraft.client.model.Model
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.world.entity.LivingEntity
import org.joml.Quaternionf
import joshxviii.plantz.PazModels.HAS_BUTTER_KEY
import joshxviii.plantz.PazModels.PAINT_COLORS_KEY
import joshxviii.plantz.PazRenderPipelines.PAINT_OVERLAY
import joshxviii.plantz.model.projectiles.ButterModel
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.entity.ProjectileRenderState
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.entity.state.HumanoidRenderState
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Vector3f
import kotlin.math.pow

class SpecialEffectsLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(private val renderer: RenderLayerParent<S, M>) : RenderLayer<S, M>(
    renderer
) {
    companion object {
        private val NOISE_TEXTURE = pazResource("textures/entity/paint_overlay.png")
        fun paintOverlay(entityTexture: Identifier, amplifier: Int = 0, color: Int = 0): RenderType {
            return RenderType.create(
                "paint_overlay_${amplifier}",
                RenderSetup.builder(PAINT_OVERLAY)
                    .withTexture("Sampler0", entityTexture)
                    .withTexture("Sampler1", NOISE_TEXTURE)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .useOverlay()
                    .sortOnUpload()
                    .createRenderSetup()
            )
        }
        val BUTTER_MODEL = ButterModel(Minecraft.getInstance().modelManager.entityModels().get().bakeLayer(ButterModel.LAYER_LOCATION))
    }

    override fun submit(
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        lightCoords: Int,
        state: S,
        yRot: Float,
        xRot: Float
    ) {
        submitButterLayer(poseStack, collector, lightCoords, state)
        submitPaintLayer(poseStack, collector, lightCoords, state)
    }

    fun submitButterLayer(
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        lightCoords: Int,
        state: S
    ) {
        val hasButterEffect = state.getDataOrDefault(HAS_BUTTER_KEY, false)
        if (!hasButterEffect) return

        val humanoidModel = parentModel as? HumanoidModel<*> ?: return

        val butterState = ProjectileRenderState().apply { entityType = PazEntities.BUTTER }
        val texture = butterState.getProjectileTextureLocation() ?: return

        poseStack.pushPose()
        humanoidModel.root().translateAndRotate(poseStack)
        humanoidModel.head.translateAndRotate(poseStack)
        poseStack.rotate(Axis.YP.rotationDegrees(90f))
        poseStack.rotate(Axis.XP.rotationDegrees(25f))
        poseStack.rotate(Axis.ZP.rotationDegrees(90f))
        poseStack.translate(-0.6,-1.4,0.2)

        collector.submitModel(
            BUTTER_MODEL,
            butterState,
            poseStack,
            RenderTypes.entityCutout(texture),
            lightCoords,
            OverlayTexture.NO_OVERLAY,
            0,
        )
        poseStack.popPose()
    }

    fun submitPaintLayer(
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        lightCoords: Int,
        state: S
    ) {
        val texture = entityTexture(state) ?: return
        val colors = state.getDataOrDefault(PAINT_COLORS_KEY, mapOf())
        var colorMix = -1
        var amplifier = 0
        colors.forEach { (color, amp) ->
            amplifier += amp
            if (colorMix == -1) {
                colorMix = ARGB.opaque(color)
                return@forEach
            }
            colorMix = ARGB.average(colorMix, ARGB.opaque(color))
        }
        if (amplifier == 0) return

        if (colorMix == -1) return
        poseStack.pushPose()

        collector.order(0).submitModel(
            parentModel,
            state,
            poseStack,
            paintOverlay(texture, amplifier, colorMix),
            lightCoords,
            OverlayTexture.NO_OVERLAY,
            alphaFromAmplifier(colorMix, amplifier),
            null,
            state.outlineColor,
        )
        poseStack.popPose()
    }

    private fun entityTexture(state: S): Identifier? {
        val living = renderer as? LivingEntityRenderer<*, S, M> ?: return null
        return living.getTextureLocation(state)
    }

    fun alphaFromAmplifier(rgb: Int, amplifier: Int): Int {
        val strength = (amplifier.coerceIn(0, 10) / 10f).pow(.5f).coerceIn(0f, 1f)
        val a = (strength * 0xFF).toInt().coerceIn(50, 0xFF)
        return (a shl 24) or (rgb and 0x00FFFFFF)
    }

}

class DuckyTubeRenderLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(
    parent: RenderLayerParent<S, M>
) : BodyItemAttachmentRenderLayer<S, M>(
    parent = parent,
    stackSelector = { listOf(it.legsEquipment) },
    matches = { it.`is`(PazItems.DUCKY_TUBE) },
)

class DyeVatRenderLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(
    parent: RenderLayerParent<S, M>
) : BodyItemAttachmentRenderLayer<S, M>(
    parent = parent,
    stackSelector = { listOf(it.rightHandItemStack, it.leftHandItemStack) },
    matches = { it.`is`(PazItems.DYE_BLASTER) },
)

class BodyBlockerRenderLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(
    parent: RenderLayerParent<S, M>
) : BodyItemAttachmentRenderLayer<S, M>(
    parent = parent,
    stackSelector = { listOf(it.chestEquipment) },
    matches = { it.has(PazComponents.WORN_BODY_BLOCKER) },
)

class RollerSkateRenderLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(
    parent: RenderLayerParent<S, M>
) : FeetItemAttachmentRenderLayer<S, M>(
    parent = parent,
    stackSelector = { listOf(it.feetEquipment) },
    matches = { it.`is`(PazItems.ROLLER_SKATES) },
)

class WornPlantRenderData(val state: PlantRenderState, val renderer: PlantRenderer)
/**
 * Renders the plant worn on an entity's head [ModelPart].
 */
class PlantHeadAttachmentRenderLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(
    parent: RenderLayerParent<S, M>,
) : RenderLayer<S, M>(parent) {

    companion object {
        const val HEAD_TOP_SINK = 0.08f
        private const val HEAD_HALF_HEIGHT = 0.5f
        private const val MAX_LOOK_YAW = 75f
        private const val MAX_LOOK_PITCH = 45f

        fun findHeadPart(model: Model<*>): ModelPart? = when {
            model is HumanoidModel<*> -> model.head
            model.root().hasChild("head") -> model.root().getChild("head")
            else -> null
        }

        fun findWornPlant(entity: LivingEntity): Plant? {
            val id = (entity as PlantHeadAttachment).`plantz$getPlantId`()
            if (id < 0) return null
            val plant = entity.level().getEntity(id) as? Plant ?: return null
            return plant.takeIf { it.attachedEntity === entity }
        }

        fun wearerRendersPlant(wearer: LivingEntity): Boolean {
            val renderer = Minecraft.getInstance().entityRenderDispatcher.getRenderer(wearer) as? RenderLayerParent<*, *> ?: return false
            return findHeadPart(renderer.model) != null
        }

        @JvmStatic
        fun extractWornPlant(entity: LivingEntity, state: LivingEntityRenderState, partialTick: Float, model: Model<*>) {
            if (findHeadPart(model) == null) return
            val plant = findWornPlant(entity) ?: return
            val renderer = Minecraft.getInstance().entityRenderDispatcher.getRenderer(plant) as? PlantRenderer ?: return
            val plantState = renderer.createRenderState()
            renderer.extractRenderState(plant, plantState, partialTick)
            val humanoidState = state as? HumanoidRenderState
            val headLocked = humanoidState?.chestEquipment?.has(PazComponents.WORN_BODY_BLOCKER) == true
            val wearerYaw = if (headLocked) state.bodyRot else state.bodyRot + state.yRot
            val wearerPitch = when {
                !headLocked -> state.xRot
                humanoidState.isCrouching -> 25f
                else -> 0f
            }
            val plantYaw = plantState.bodyRot + plantState.yRot
            plantState.renderedByWearer = false
            plantState.embeddedOnWearer = true
            plantState.rotations = Quaternionf()
            plantState.bodyRot = 180f
            plantState.yRot = Mth.wrapDegrees(plantYaw - wearerYaw).coerceIn(-MAX_LOOK_YAW, MAX_LOOK_YAW)
            plantState.xRot = (plantState.xRot - wearerPitch).coerceIn(-MAX_LOOK_PITCH, MAX_LOOK_PITCH)
            plantState.walkAnimationPos = 0f
            plantState.walkAnimationSpeed = 0f
            state.setData(PazModels.HEAD_PLANT_KEY, WornPlantRenderData(plantState, renderer))
        }
    }

    override fun submit(
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        lightCoords: Int,
        state: S,
        yRot: Float,
        xRot: Float
    ) {
        val data = state.getData(PazModels.HEAD_PLANT_KEY) ?: return
        val head = findHeadPart(parentModel) ?: return
        if (!head.visible) return

        poseStack.pushPose()
        parentModel.root().translateAndRotate(poseStack)
        head.translateAndRotate(poseStack)
        poseStack.translate(0f, -(HEAD_HALF_HEIGHT - HEAD_TOP_SINK), 0f)
        poseStack.rotate(Axis.ZP.rotationDegrees(180f))

        data.state.lightCoords = lightCoords
        data.renderer.submit(data.state, poseStack, collector, CameraRenderState())
        poseStack.popPose()
    }
}


abstract class FeetItemAttachmentRenderLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(
    parent: RenderLayerParent<S, M>,
    private val stackSelector: (HumanoidRenderState) -> List<ItemStack>,
    private val matches: (ItemStack) -> Boolean,
) : RenderLayer<S, M>(parent) {
    private val itemRenderState = ItemStackRenderState()
    override fun submit(
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        lightCoords: Int,
        state: S,
        yRot: Float,
        xRot: Float
    ) {
        val humanoidModel = parentModel as? HumanoidModel<*> ?: return
        val humanState = state as? HumanoidRenderState ?: return
        val itemStack = stackSelector(humanState).firstOrNull { matches(it) } ?: return

        poseStack.pushPose()
        humanoidModel.root().translateAndRotate(poseStack)
        renderLegItem(poseStack, collector, itemStack, lightCoords, humanoidModel.leftLeg, false)
        renderLegItem(poseStack, collector, itemStack, lightCoords, humanoidModel.rightLeg, true)
        poseStack.popPose()
    }

    fun renderLegItem(
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        itemStack: ItemStack,
        lightCoords: Int,
        leg: ModelPart,
        flipped: Boolean,
    ) {
        poseStack.pushPose()

        leg.translateAndRotate(poseStack)
        poseStack.rotate(Axis.ZP.rotationDegrees(180.0f))

        val minecraft = Minecraft.getInstance()
        itemRenderState.clear()
        minecraft.itemModelResolver.updateForTopItem(
            itemRenderState,
            itemStack,
            if (flipped) ItemDisplayContext.THIRD_PERSON_RIGHT_HAND else ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
            minecraft.level,
            null,
            0
        )
        itemRenderState.submit(
            poseStack,
            collector,
            lightCoords,
            OverlayTexture.NO_OVERLAY,
            0
        )
        poseStack.popPose()
    }
}

abstract class BodyItemAttachmentRenderLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(
    parent: RenderLayerParent<S, M>,
    private val stackSelector: (HumanoidRenderState) -> List<ItemStack>,
    private val matches: (ItemStack) -> Boolean,
) : RenderLayer<S, M>(parent) {

    private val itemRenderState = ItemStackRenderState()

    override fun submit(
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        lightCoords: Int,
        state: S,
        yRot: Float,
        xRot: Float
    ) {
        val humanoidModel = parentModel as? HumanoidModel<*> ?: return
        val humanState = state as? HumanoidRenderState ?: return
        val itemStack = stackSelector(humanState).firstOrNull { matches(it) } ?: return

        poseStack.pushPose()

        humanoidModel.root().translateAndRotate(poseStack)
        humanoidModel.body.translateAndRotate(poseStack)
        poseStack.rotate(Axis.ZP.rotationDegrees(180.0f))

        if (itemStack.has(PazComponents.WORN_BODY_BLOCKER)) {
            poseStack.pushPose()
            //this moves worn headgear up a bit
            humanoidModel.head.y -= 3f
            poseStack.popPose()
        }

        if (state.isBaby) {
            poseStack.scale(0.5f, 0.5f, 0.5f)
            poseStack.translate(0.0, 0.5, 0.0)
        }

        val minecraft = Minecraft.getInstance()
        itemRenderState.clear()
        minecraft.itemModelResolver.updateForTopItem(
            itemRenderState,
            itemStack,
            ItemDisplayContext.HEAD,
            minecraft.level,
            null,
            0
        )
        itemRenderState.submit(
            poseStack,
            collector,
            lightCoords,
            OverlayTexture.NO_OVERLAY,
            0
        )

        poseStack.popPose()
    }
}