package joshxviii.plantz.gui

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Button.DEFAULT_NARRATION
import net.minecraft.client.gui.components.Button.OnPress
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB

object GuiUtil {
    fun plane(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        lightCoords: Int,
        color: Int = 0xFFFFFFF,
    ) {
        vertex(pose, buffer, -0.5f, -0.5f, color, 0f, 1f, lightCoords)
        vertex(pose, buffer, 0.5f, -0.5f, color, 1f, 1f, lightCoords)
        vertex(pose, buffer, 0.5f, 0.5f, color, 1f, 0f, lightCoords)
        vertex(pose, buffer, -0.5f, 0.5f, color, 0f, 0f, lightCoords)
    }

    fun vertex(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        x: Float,
        y: Float,
        color: Int,
        u: Float,
        v: Float,
        lightCoords: Int,
        overlay: Int = OverlayTexture.NO_OVERLAY
    ) {
        buffer.addVertex(pose, x, y, 0.0f)
            .setColor(ARGB.red(color), ARGB.green(color), ARGB.blue(color), ARGB.alpha(color))
            .setUv(u, v)
            .setOverlay(overlay)
            .setLight(lightCoords)
            .setNormal(pose, 0.0f, 1.0f, 0.0f)
    }
}

fun GuiGraphicsExtractor.outlineText(font: Font, text: MutableComponent, x: Int = 0, y: Int = 0, color: Int = 0xFFFFFF, outlineColor: Int = 0x000000) {
    text(font, text, x+1, y, ARGB.opaque(outlineColor), false)
    text(font, text, x-1, y, ARGB.opaque(outlineColor), false)
    text(font, text, x, y+1, ARGB.opaque(outlineColor), false)
    text(font, text, x, y-1, ARGB.opaque(outlineColor), false)
    text(font, text, x, y, ARGB.opaque(color), false)
}

open class PazButton(
    var buttonX: Int,
    var buttonY: Int,
    val buttonWidth: Int,
    val buttonHeight: Int,
    val clickAction: OnPress,
    val texture: Identifier,
    val hoverTexture: Identifier = texture,
    val disabledTexture: Identifier = texture,
    val enabledRequirement: ((button: PazButton) -> Boolean) = { true },
    val clickRequirement: ((button: PazButton) -> Boolean) = enabledRequirement,
    val text: Component = Component.empty(),
) : Button(buttonX, buttonY, buttonWidth, buttonHeight, text, clickAction, DEFAULT_NARRATION) {

    override fun extractContents(
        graphics: GuiGraphicsExtractor,
        mx: Int,
        my: Int,
        a: Float
    ) {
        val press = !enabledRequirement.invoke(this)
        val font = Minecraft.getInstance().font

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            if (press) disabledTexture else if (isButtonHovered(mx, my)) hoverTexture else texture,
            buttonX, buttonY, 0.0f, 0.0f, buttonWidth, buttonHeight, buttonWidth, buttonHeight
        )

        val line = font.split(text, buttonWidth-8).firstOrNull()
        if (line!=null) graphics.text(font, line, buttonX + if(press) 3 else 2, buttonY+3, -1, false)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (clickRequirement.invoke(this)) super.mouseClicked(event, doubleClick)
        return false
    }

    fun isButtonHovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX in (buttonX..(buttonX + (buttonWidth-1)))
                && mouseY in (buttonY..(buttonY + (buttonHeight-1)))
    }

}
