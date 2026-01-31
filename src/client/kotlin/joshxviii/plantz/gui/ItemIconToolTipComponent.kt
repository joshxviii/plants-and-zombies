package joshxviii.plantz.gui

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

class ItemIconToolTipComponent(private val stack: ItemStack, val message : Component = Component.empty()) : ClientTooltipComponent {

    override fun getHeight(font: Font): Int = 16

    override fun getWidth(font: Font): Int = 20

    override fun renderImage(font: Font, x: Int, y: Int, w: Int, h: Int, graphics: GuiGraphics) {
        graphics.renderItem(stack, x + 2, y + 1)
        graphics.renderItemDecorations(font, stack, x + 2, y + 1)
    }

    override fun renderText(graphics: GuiGraphics, font: Font, x: Int, y: Int) {
        graphics.drawString(font, message, x, y, -1)
    }
}