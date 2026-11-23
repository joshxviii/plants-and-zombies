package joshxviii.plantz.gui

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import joshxviii.plantz.PazRenderPipelines
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import joshxviii.plantz.gui.ZombieHudMarkers.ICON_SIZE
import joshxviii.plantz.raid.isBoss
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.LightCoordsUtil
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.ItemStack

class HeadHudMarker(
    val zombie: Zombie,
    val model: HumanoidModel<*>,
    val texture: Identifier,
    val itemStack: ItemStack,
    val cx: Double,
    val cy: Double,
    val scale: Float,
    val outlinePad: Float = 0f
): GuiElementRenderState {
    companion object {
        const val OUTLINE_COLOR = 0x000000
        const val BOSS_OUTLINE_COLOR = 0xFF0000
    }

    val mc = Minecraft.getInstance()

    override fun buildVertices(vertexConsumer: VertexConsumer) {
        model.resetPose()
        val pose = PoseStack()
        val tint = ARGB.opaque(if (zombie.isBoss()) (if (isOutline()) BOSS_OUTLINE_COLOR else ARGB.addRgb(BOSS_OUTLINE_COLOR, 0xBBBBBB)) else (if (isOutline()) OUTLINE_COLOR else 0xFFFFFF))
        pose.pushPose()
        pose.translate(cx, cy, if (outlinePad>0f) 149.0 else 150.0)
        pose.translate(0.0, (ICON_SIZE + outlinePad)*0.5, 0.0)
        if (zombie.isBoss()) pose.scale(1.25f, 1.25f, 1.25f)
        pose.scale(scale, scale, scale)
        if (isOutline()) pose.scale(outlinePad, outlinePad, outlinePad)
        model.head.setPos(0f, 0f, 0f)
        renderWithDepthTest(pose, vertexConsumer, tint)

        pose.popPose()
    }

    private class CubeDraw(val cube: ModelPart.Cube, val depth: Float, val pose: Matrix4f, val normal: Matrix3f)

    /**
     * makes sure the model parts are rendered in order to avoid things like hair and hats blocking the face
     */
    private fun renderWithDepthTest(pose: PoseStack, consumer: VertexConsumer, tint: Int) {
        val draws = ArrayList<CubeDraw>()
        val center = Vector3f()
        model.head.visit(pose) { cubePose, _, _, cube ->
            val matrix = cubePose.pose()
            matrix.transformPosition(
                (cube.minX + cube.maxX) * 0.5f,
                (cube.minY + cube.maxY) * 0.5f,
                (cube.minZ + cube.maxZ) * 0.5f,
                center
            )
            draws.add(CubeDraw(cube, center.z, Matrix4f(matrix), Matrix3f(cubePose.normal())))
        }

        val scratch = PoseStack()
        for (draw in draws.sortedByDescending { it.depth }) {
            scratch.last().pose().set(draw.pose)
            scratch.last().normal().set(draw.normal)
            draw.cube.compile(scratch.last(), consumer, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, tint)
        }
    }

    fun isOutline(): Boolean = outlinePad>0f

    override fun pipeline(): RenderPipeline = if (isOutline()) PazRenderPipelines.HEAD_MARKER_OUTLINE else PazRenderPipelines.HEAD_MARKER

    override fun textureSetup(): TextureSetup {
        val t = mc.textureManager.getTexture(texture)
        return TextureSetup.singleTexture(t.textureView, t.sampler)
    }

    override fun scissorArea(): ScreenRectangle? = null
    override fun bounds(): ScreenRectangle = ScreenRectangle(0, 0, mc.window.guiScaledWidth, mc.window.guiScaledHeight)
}