package joshxviii.plantz.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import joshxviii.plantz.block.SunBatteryBlock
import joshxviii.plantz.block.entity.SunBatteryBlockEntity
import joshxviii.plantz.gui.GuiUtil
import joshxviii.plantz.pazResource
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.rendertype.OutputTarget
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.resources.model.sprite.SpriteId
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.pow

class SunBatteryRenderer() : BlockEntityRenderer<SunBatteryBlockEntity, SunBatteryRenderSate> {
    companion object {
        private val TEXTURE_LOCATION = pazResource("textures/block/solar_battery_sun.png")
        public val EMISSIVE_SUN =
            RenderType.create(
                "sun",
                RenderSetup.builder(RenderPipelines.ENERGY_SWIRL)
                    .withTexture("Sampler0", TEXTURE_LOCATION)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()
            )

        fun submitSun(
            poseStack: PoseStack,
            collector: SubmitNodeCollector,
            camera: CameraRenderState,
            time: Float,
            sunPercent: Float,
            lightCoords: Int,
        ) {
            if (sunPercent <= 0f) return
            poseStack.pushPose()
            val s = sunPercent.pow(0.5f) * 0.9f
            poseStack.translate(0.5f, 0.33f, 0.5f)
            poseStack.scale(s, s, s)
            poseStack.mulPose(camera.orientation)
            poseStack.mulPose(Axis.YP.rotation(time*0.04f))

            submitSunRay(poseStack, collector, lightCoords)
            poseStack.mulPose(Axis.YP.rotation(Mth.PI*0.5f))
            submitSunRay(poseStack, collector, lightCoords)
            poseStack.popPose()
        }

        fun submitSunRay(
            poseStack: PoseStack,
            collector: SubmitNodeCollector,
            lightCoords: Int,
        ) {
            collector.submitCustomGeometry(poseStack, EMISSIVE_SUN,) { pose, buffer ->
                GuiUtil.plane(pose, buffer, lightCoords, color = 0xEEEE00)
            }
        }
    }

    override fun submit(
        state: SunBatteryRenderSate,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        submitSun(
            poseStack,
            collector,
            camera,
            state.time,
            state.sunPercent,
            state.lightCoords
        )
    }

    override fun createRenderState(): SunBatteryRenderSate = SunBatteryRenderSate()
    override fun extractRenderState(
        blockEntity: SunBatteryBlockEntity,
        state: SunBatteryRenderSate,
        partialTicks: Float,
        cameraPosition: Vec3,
        breakProgress: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress)
        state.time = blockEntity.getLevel()!!.gameTime.toFloat()
        state.sunPercent = blockEntity.blockState.getValue(SunBatteryBlock.LEVEL).toFloat() / 15.0f
    }
}

class SunBatteryRenderSate : BlockEntityRenderState() {
    var time: Float = 0f
    var sunPercent: Float = 0f
    var sprite: SpriteId? = null
}