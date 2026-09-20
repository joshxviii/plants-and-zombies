package joshxviii.plantz.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import joshxviii.plantz.block.SunBatteryBlock
import joshxviii.plantz.block.entity.SunBatteryBlockEntity
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
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.sprite.SpriteId
import net.minecraft.core.Direction
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.phys.Vec3
import kotlin.math.pow

class SunBatteryRenderer() : BlockEntityRenderer<SunBatteryBlockEntity, SunBatteryRenderSate> {
    companion object {
        private val TEXTURE_LOCATION = pazResource("textures/block/solar_battery_sun.png")
        public val EMISSIVE_SUN =
            RenderType.create(
                "sun",
                RenderSetup.builder(RenderPipelines.BEACON_BEAM_TRANSLUCENT)
                    .withTexture("Sampler0", TEXTURE_LOCATION)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .useLightmap()
                    .useOverlay()
                    .affectsCrumbling()
                    .sortOnUpload()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()
            )

        fun vertex(
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
                .setColor(r, g, b, 220)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, 0.0f, 1.0f, 0.0f)
        }

        private fun submitSunShine(
            state: BlockEntityRenderState,
            poseStack: PoseStack,
            collector: SubmitNodeCollector,
            color: Int = 0xFFFFFF
        ) {
            collector.submitCustomGeometry(
                poseStack,
                EMISSIVE_SUN,
            ) { pose: PoseStack.Pose, buffer: VertexConsumer ->
                vertex(buffer, pose, -0.5f, -0.5f, color, color, 0, 0f, 0f, state.lightCoords)
                vertex(buffer, pose, 0.5f, -0.5f, color, color, 0, 1f, 0f, state.lightCoords)
                vertex(buffer, pose, 0.5f, 0.5f, color, color, 0, 1f, 1f, state.lightCoords)
                vertex(buffer, pose, -0.5f, 0.5f, color, color, 0, 0f, 1f, state.lightCoords)

                vertex(buffer, pose, -0.5f, -0.5f, color, color, 0, 0f, 0f, state.lightCoords)
                vertex(buffer, pose, -0.5f, 0.5f, color, color, 0, 0f, 1f, state.lightCoords)
                vertex(buffer, pose, 0.5f, 0.5f, color, color, 0, 1f, 1f, state.lightCoords)
                vertex(buffer, pose, 0.5f, -0.5f, color, color, 0, 1f, 0f, state.lightCoords)
            }
        }

        fun submitSun(
            state: BlockEntityRenderState,
            poseStack: PoseStack,
            collector: SubmitNodeCollector,
            camera: CameraRenderState,
            scale: Float,
            time: Float
        ) {
            poseStack.pushPose()
            poseStack.scale(scale, scale, scale)

            poseStack.mulPose(camera.orientation)
            poseStack.mulPose(Axis.YP.rotation(time*0.04f))

            submitSunShine(state, poseStack, collector)
            poseStack.mulPose(Axis.YP.rotation(Mth.PI*0.5f))
            submitSunShine(state, poseStack, collector)
            poseStack.popPose()
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
        state.attachedFace = blockEntity.blockState.getValue(SunBatteryBlock.FACE)
        state.facing = blockEntity.blockState.getValue(SunBatteryBlock.FACING)
        state.lightCoords = LightCoordsUtil.FULL_BRIGHT
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress)
    }

    override fun submit(
        state: SunBatteryRenderSate,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        val sunPos = getSunPosition(state.attachedFace, state.facing)
        poseStack.pushPose()

        poseStack.translate(sunPos.x, sunPos.y, sunPos.z)
        val scale = (state.sunPercent).pow(0.5f) * 0.9f

        submitSun(state, poseStack, collector, camera, scale, state.time)

        poseStack.popPose()
    }

    private fun getSunPosition(face: AttachFace, facing: Direction): Vec3 {
        val h = 0.33

        return when (face) {
            AttachFace.FLOOR -> Vec3(0.5, h, 0.5)
            AttachFace.CEILING -> Vec3(0.5, 1.0 - h, 0.5)
            AttachFace.WALL -> {
                Vec3(
                    0.5 + facing.opposite.stepX * 0.5 + facing.stepX * h,
                    0.5 + facing.opposite.stepY * 0.5 + facing.stepY * h,
                    0.5 + facing.opposite.stepZ * 0.5 + facing.stepZ * h
                )
            }
        }
    }
}

class SunBatteryRenderSate : BlockEntityRenderState() {
    var time: Float = 0f
    var sunPercent: Float = 0f
    var attachedFace: AttachFace = AttachFace.FLOOR
    var facing: Direction = Direction.NORTH
    var sprite: SpriteId? = null
}