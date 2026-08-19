package joshxviii.plantz.gui

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.ARGB

fun GuiGraphicsExtractor.outlineText(font: Font, text: MutableComponent, x: Int = 0, y: Int = 0, color: Int = 0xFFFFFF, outlineColor: Int = 0x000000) {
    text(font, text, x+1, y, ARGB.opaque(outlineColor), false)
    text(font, text, x-1, y, ARGB.opaque(outlineColor), false)
    text(font, text, x, y+1, ARGB.opaque(outlineColor), false)
    text(font, text, x, y-1, ARGB.opaque(outlineColor), false)
    text(font, text, x, y, ARGB.opaque(color), false)
}
