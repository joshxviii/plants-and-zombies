package joshxviii.plantz.gui

import joshxviii.plantz.pazResource
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.FormattedCharSequence

fun GuiGraphicsExtractor.outlineText(font: Font, text: String, x: Int = 0, y: Int = 0, color: Int = 0xFFFFFF, outlineColor: Int = ARGB.multiply(color, 0x545454)) {
    val c = Component.literal(text)
    outlineText(font, c, x, y, color, outlineColor)
}

fun GuiGraphicsExtractor.outlineText(font: Font, text: Component, x: Int = 0, y: Int = 0, color: Int = 0xFFFFFF, outlineColor: Int = ARGB.multiply(color, 0x545454)) {
    outlineText(font, text.visualOrderText, x, y, color, outlineColor)
}

fun GuiGraphicsExtractor.outlineText(font: Font, text: FormattedCharSequence, x: Int = 0, y: Int = 0, color: Int = 0xFFFFFF, outlineColor: Int = ARGB.multiply(color, 0x545454)) {
    text(font, text, x+1, y, ARGB.opaque(outlineColor), false)
    text(font, text, x-1, y, ARGB.opaque(outlineColor), false)
    text(font, text, x, y+1, ARGB.opaque(outlineColor), false)
    text(font, text, x, y-1, ARGB.opaque(outlineColor), false)
    text(font, text, x, y, ARGB.opaque(color), false)
}

object Fonts {
    val DOT_DISPLAY: Identifier = pazResource("dot_display")
    fun Component.withFont(font: Identifier): Component = copy().withStyle { it.withFont(FontDescription.Resource(font)) }
}

open class PazButton(
    var buttonX: Int,
    var buttonY: Int,
    var buttonWidth: Int,
    var buttonHeight: Int,
    val clickAction: OnPress,
    val texture: Identifier,
    val hoverTexture: Identifier = texture,
    val disabledTexture: Identifier = texture,
    val enabledRequirement: ((button: PazButton) -> Boolean) = { true },
    val clickRequirement: ((button: PazButton) -> Boolean) = enabledRequirement,
    val text: Component = Component.empty(),
    val color: Int = -1
) : Button(buttonX, buttonY, buttonWidth, buttonHeight, text, clickAction, DEFAULT_NARRATION) {

    override fun extractContents(
        graphics: GuiGraphicsExtractor,
        mx: Int,
        my: Int,
        a: Float
    ) {
        val pressed = isPressed()
        val font = Minecraft.getInstance().font

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            if (pressed) disabledTexture else if (isButtonHovered(mx, my)) hoverTexture else texture,
            buttonX, buttonY, 0.0f, 0.0f, buttonWidth, buttonHeight, buttonWidth, buttonHeight, color
        )

        val line = font.split(text, buttonWidth-8).firstOrNull()
        if (line!=null) graphics.text(font, line, buttonX + if(pressed) 3 else 2, buttonY+3, -1, false)
    }

    fun isPressed(): Boolean {
        return !enabledRequirement.invoke(this)
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