package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import joshxviii.plantz.SunRenderer.Companion.EMISSIVE_SUN
import joshxviii.plantz.SunRenderer.Companion.RENDER_TYPE
import joshxviii.plantz.block.SunBatteryBlock
import joshxviii.plantz.block.entity.SunBatteryBlockEntity
import joshxviii.plantz.model.zombies.PazZombieModel
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Overlay
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.rendertype.OutputTarget
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.sprite.SpriteId
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

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

        private fun vertex(
            buffer: VertexConsumer,
            pose: PoseStack.Pose,
            x: Float,
            y: Float,
            r: Int,
            g: Int,
            b: Int,
            u: Float,
            v: Float,
            lightCoords: Int
        ) {
            buffer.addVertex(pose, x, y, 0.0f)
                .setColor(r, g, b, 128)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, 0.0f, 1.0f, 0.0f)
        }

    }
    override fun createRenderState(): SunBatteryRenderSate {
        return SunBatteryRenderSate()
    }

    override fun extractRenderState(
        blockEntity: SunBatteryBlockEntity,
        state: SunBatteryRenderSate,
        partialTicks: Float,
        cameraPosition: Vec3,
        breakProgress: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        state.time = blockEntity.getLevel()!!.gameTime.toFloat()
        state.sunPercent = blockEntity.blockState.getValue(SunBatteryBlock.LEVEL).toFloat() / 15.0f
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress)
    }

    override fun submit(
        state: SunBatteryRenderSate,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        val s = (state.sunPercent).pow(0.5f) * 0.9f
        poseStack.translate(0.5f, 0.33f, 0.5f)
        poseStack.scale(s, s, s)
        poseStack.mulPose(camera.orientation)
        poseStack.mulPose(Axis.YP.rotation(state.time*0.04f))

        submitSunShine(state, poseStack, collector)
        poseStack.mulPose(Axis.YP.rotation(Mth.PI*0.5f))
        submitSunShine(state, poseStack, collector)
    }

    fun submitSunShine(
        state: SunBatteryRenderSate,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        color: Int = 0xFFFFFF
    ) {
        collector.submitCustomGeometry(
            poseStack,
            EMISSIVE_SUN,
        ) { pose: PoseStack.Pose, buffer: VertexConsumer ->
            vertex(buffer, pose, -0.5f, -0.5f, color, color, 0, 0f, 1f, state.lightCoords)
            vertex(buffer, pose, 0.5f, -0.5f, color, color, 0, 1f, 1f, state.lightCoords)
            vertex(buffer, pose, 0.5f, 0.5f, color, color, 0, 1f, 0f, state.lightCoords)
            vertex(buffer, pose, -0.5f, 0.5f, color, color, 0, 0f, 0f, state.lightCoords)
        }
    }
}

class SunBatteryRenderSate : BlockEntityRenderState() {
    var time: Float = 0f
    var sunPercent: Float = 0f
    var sprite: SpriteId? = null
}