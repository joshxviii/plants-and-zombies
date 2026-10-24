package joshxviii.plantz.gui

import com.mojang.renderpearl.api.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import joshxviii.plantz.PazRenderPipelines
import joshxviii.plantz.gui.ZombieHudMarkers.ICON_SIZE
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.Identifier
import net.minecraft.util.LightCoordsUtil
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.ItemDisplayContext
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
    val mc = Minecraft.getInstance()

    override fun buildVertices(vertexConsumer: VertexConsumer) {
        model.resetPose()
        val pose = PoseStack()
        pose.pushPose()
        pose.translate(cx, cy, if (outlinePad>0f) 149.0 else 150.0)
        pose.translate(0.0, (ICON_SIZE + outlinePad)*0.5, 0.0)
        pose.rotate(Axis.YP.rotationDegrees(180.0f))
        pose.scale(scale, scale, scale)
        if (outlinePad>0f) pose.scale(outlinePad, outlinePad, outlinePad)
        model.head.setPos(0f, 0f, 0f)
        model.head.render(pose, vertexConsumer, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY)
        mc.itemModelResolver.updateForTopItem(ItemStackRenderState(), itemStack, ItemDisplayContext.HEAD, mc.level, zombie, 0)

        pose.popPose()
    }

    override fun pipeline(): RenderPipeline = if (outlinePad>0f) PazRenderPipelines.HEAD_MARKER_OUTLINE else PazRenderPipelines.HEAD_MARKER_BAKE

    override fun textureSetup(): TextureSetup {
        val t = mc.textureManager.getTexture(texture)
        return TextureSetup.singleTexture(t.textureView, t.sampler)
    }

    override fun scissorArea(): ScreenRectangle? = null
    override fun bounds(): ScreenRectangle = ScreenRectangle(0, 0, mc.window.guiScaledWidth, mc.window.guiScaledHeight)
}