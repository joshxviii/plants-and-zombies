package joshxviii.plantz.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.PaintInfoUniforms
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazModels.PAINT_COLORS_KEY
import joshxviii.plantz.PazRenderPipelines.PAINT_OVERLAY
import joshxviii.plantz.model.zombies.PazZombieModel
import joshxviii.plantz.pazResource
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
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
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class PaintLayer<S : LivingEntityRenderState, M : EntityModel<in S>>( private val renderer: RenderLayerParent<S, M>) : RenderLayer<S, M>(
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
    }

    private fun entityTexture(state: S): Identifier? {
        val living = renderer as? LivingEntityRenderer<*, S, M> ?: return null
        return living.getTextureLocation(state)
    }

    fun alphaFromAmplifier(rgb: Int, amplifier: Int): Int {
        val t = amplifier.coerceIn(0, 10) / 10f
        val strength = 0.20f + t * 0.55f
        val a = (strength * 255f).toInt().coerceIn(50, 255)
        return (a shl 24) or (rgb and 0x00FFFFFF)
    }

    override fun submit(
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        lightCoords: Int,
        state: S,
        yRot: Float,
        xRot: Float
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

//        PaintInfoUniforms.amplifierToNoise(amplifier).let { (scale, strength) ->
//            PaintInfoUniforms.write(scale, strength)
//        }
        //TODO remove dynamic uniform and make into static shader since changing the uniform is not really working with multiple instances of entities.
        // changing the alpha, mixing the colors and keeping the noise static gives a good enough effect.
        // [paint_overlay.fhs] and [paint_info]
        PaintInfoUniforms.write(64f, 2.0f)

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
            null
        )
        poseStack.popPose()
    }

}

class DuckyTubeRenderLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(
    parent: RenderLayerParent<S, M>
) : BodyItemAttachmentRenderLayer<S, M>(
    parent = parent,
    expectedItem = PazItems.DUCKY_TUBE,
    stackSelector = { it.legsEquipment },
)

class DyeVatRenderLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(
    parent: RenderLayerParent<S, M>
) : BodyItemAttachmentRenderLayer<S, M>(
    parent = parent,
    expectedItem = PazItems.DYE_BLASTER,
    stackSelector = { it.mainHandItemStack },
)

abstract class BodyItemAttachmentRenderLayer<S : LivingEntityRenderState, M : EntityModel<in S>>(
    parent: RenderLayerParent<S, M>,
    private val expectedItem: Item,
    private val stackSelector: (HumanoidRenderState) -> ItemStack,
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
        val itemStack = stackSelector(humanState)

        if (!itemStack.`is`(expectedItem)) return

        poseStack.pushPose()

        if (state.isBaby) {
            poseStack.translate(0.0, 0.8, 0.0)
            poseStack.scale(0.55f, 0.55f, 0.55f)
        }

        if (humanoidModel is PazZombieModel) humanoidModel.body
        else humanoidModel.body.translateAndRotate(poseStack)

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f))

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