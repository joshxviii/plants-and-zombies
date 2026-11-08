package joshxviii.plantz.gui

import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazItems
import joshxviii.plantz.getTotalSun
import joshxviii.plantz.item.component.SunCost
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.outlineText
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameType

object SunInfoHud {
    val SUN_ICON: Identifier = pazResource("textures/gui/hud/sun.png")
    val SUN_ICON_SMALL: Identifier = pazResource("textures/gui/hud/sun_small.png")
    private val mc = Minecraft.getInstance()
    private val font = mc.font
    fun extractRenderState(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, itemStack: ItemStack, highlightTimer: Int) {
        if (!PazConfig.SHOW_SUN_INFO || mc.gui.hud.isHidden || mc.gameMode?.playerMode == GameType.SPECTATOR) return
        val hotbarWidth = 182
        val x = graphics.guiWidth() / 2
        val y = graphics.guiHeight()

        displayHeldSun((x - hotbarWidth / 2) - 2, y - 14, graphics)
        displaySeedCost(x, y - 34, graphics, itemStack, highlightTimer)
    }

    fun displaySeedCost(x: Int , y: Int, graphics: GuiGraphicsExtractor, seedPacket: ItemStack, highlightTimer: Int) {
        if (!seedPacket.`is`(PazItems.SEED_PACKET)) return
        val alpha = (highlightTimer.toFloat() * 0xFF / 10).toInt().coerceAtMost(0xFF)
        if (alpha <= 0) return
        val sunCost = SunCost.getSunCost(seedPacket)
        val text = "${sunCost}"
        val iconWidth = 10
        val textWidth = font.width(text)
        val offsetX = (textWidth + iconWidth + 1) / 2
        val offsetY = mc.gameMode?.canHurtPlayer()?.let { y - if (it) 14 else 0 }?: y
        graphics.text(font, text, x - offsetX, offsetY, ARGB.color(alpha, 0xFFE330))
        sunIcon(x - offsetX + textWidth + 1, offsetY-1, graphics, iconWidth, true, alpha)
    }

    fun displayHeldSun(x: Int, y: Int, graphics: GuiGraphicsExtractor) {
        val sunTotal = mc.player?.getTotalSun()?: return
        if (sunTotal <= 0) return
        val text = "${sunTotal}"
        val offsetX = font.width(text) + if (mc.player?.offhandItem?.isEmpty == true) 0 else 29

        graphics.outlineText(font, text, x - offsetX, y, ARGB.opaque(0xFFF000))
        graphics.item(PazItems.SUN.defaultInstance, x - offsetX - 19, y - 4)
    }

    fun sunIcon(x: Int, y: Int, graphics: GuiGraphicsExtractor, size: Int = 14, small: Boolean = false, alpha: Int = 0xFF) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, if (small) SUN_ICON_SMALL else SUN_ICON, x, y, 0f, 0f, size, size, size, size, ARGB.white(alpha))
    }
}