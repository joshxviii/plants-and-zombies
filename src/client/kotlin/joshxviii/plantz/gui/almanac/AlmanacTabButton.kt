package joshxviii.plantz.gui.almanac

import joshxviii.plantz.gui.PazButton
import joshxviii.plantz.item.component.AlmanacTab
import joshxviii.plantz.pazResource
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component

class AlmanacTabButton(
    val tab: AlmanacTab,
    x: Int,
    y: Int,
    clickAction: OnPress,
    enabledRequirement: (PazButton) -> Boolean
): PazButton(
    x + 8 + tab.ordinal * (TAB_WIDTH + 2), y - TAB_HEIGHT + 8, TAB_WIDTH, TAB_HEIGHT, clickAction,
    texture = TAB_UNSELECTED,
    disabledTexture = TAB_SELECTED,
    enabledRequirement = enabledRequirement
) {
    companion object {
        private const val TAB_WIDTH = 16
        private const val TAB_HEIGHT = 14
        private val TAB_SELECTED = pazResource("textures/gui/almanac/tab_selected.png")
        private val TAB_UNSELECTED = pazResource("textures/gui/almanac/tab_unselected.png")
        private val TAB_ICONS = listOf(
            pazResource("textures/gui/almanac/tab_icon_plant.png"),
            pazResource("textures/gui/almanac/tab_icon_zombie.png"),
            pazResource("textures/gui/almanac/tab_icon_info.png")
        )
    }

    init {
        setTooltip(Tooltip.create(Component.translatable("gui.plantz.almanac.tab.${tab.name.lowercase()}")))
    }

    override fun extractContents(graphics: GuiGraphicsExtractor, mx: Int, my: Int, a: Float) {
        super.extractContents(graphics, mx, my, a)
        graphics.blit(RenderPipelines.GUI_TEXTURED, TAB_ICONS[tab.ordinal], x, y + if (isPressed()) 0 else 2, 0f, 0f, width, height, width, height)
    }
}