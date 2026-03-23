package joshxviii.plantz.gui

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

class ItemIconToolTipComponent(private val stack: ItemStack, val message : Component = Component.empty()) : ClientTooltipComponent {

    override fun getHeight(font: Font): Int = 16

    override fun getWidth(font: Font): Int = 20

    override fun extractImage(font: Font, x: Int, y: Int, w: Int, h: Int, graphics: GuiGraphicsExtractor) {
        graphics.item(stack, x + 2, y + 1)
        graphics.itemDecorations(font, stack, x + 2, y + 1)    }

    override fun extractText(graphics: GuiGraphicsExtractor, font: Font, x: Int, y: Int) {
        graphics.text(font, message, x, y, -1)
    }
}