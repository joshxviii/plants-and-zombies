package joshxviii.plantz.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

open class PazButton(
    var buttonX: Int,
    var buttonY: Int,
    val buttonWidth: Int,
    val buttonHeight: Int,
    val clickAction: OnPress,
    val texture: Identifier,
    val hoverTexture: Identifier = texture,
    val disabledTexture: Identifier = texture,
    private val enabledRequirement: ((button: PazButton) -> Boolean) = { true },
    private val clickRequirement: ((button: PazButton) -> Boolean) = enabledRequirement,
    val text: Component = Component.empty(),
) : Button(buttonX, buttonY, buttonWidth, buttonHeight, text, clickAction, DEFAULT_NARRATION) {

    override fun renderContents(
        graphics: GuiGraphics,
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

        val line = font.split(text, buttonWidth).firstOrNull()
        if (line!=null) graphics.drawString(font, line, buttonX + if(press) 3 else 2, buttonY+3, -1, false)
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