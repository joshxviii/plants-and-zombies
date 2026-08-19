package joshxviii.plantz.gui

import joshxviii.plantz.PazClientNetwork.ZombieRaidClientCache
import joshxviii.plantz.PazConfig
import joshxviii.plantz.pazResource
import joshxviii.plantz.raid.ZombieRaid
import joshxviii.plantz.tickTimeFormat
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.LerpingBossEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.Mth

object ZombieRaidOverlay {

    @JvmStatic
    fun isZombieRaid(event: LerpingBossEvent): Boolean {
        return event.name.toString().contains("zombie")
    }

    val BACKGROUND: Identifier = pazResource("textures/gui/raid/background.png")
    val BACKGROUND_CREDITS: Identifier = pazResource("textures/gui/raid/background_after_credits.png")
    val PLANT_FLAG_SPRITE: Identifier = pazResource("textures/gui/raid/plant_flag.png")
    val ZOMBIE_HEAD_SPRITE: Identifier = pazResource("textures/gui/raid/zombie_head.png")
    val ZOMBIE_HEAD_CREDITS_SPRITE: Identifier = pazResource("textures/gui/raid/zombie_head_after_credits.png")
    val HEALTH_BAR_FLAG: Identifier = pazResource("textures/gui/raid/flag_health.png")
    val HEALTH_BAR_ZOMBIES: Identifier = pazResource("textures/gui/raid/zombie_health.png")

    fun extractRenderState(
        graphics: GuiGraphicsExtractor,
        deltaTracker: DeltaTracker
    ) {
        val bgWidth = 256
        val bgHeight = 32
        val font = Minecraft.getInstance().font
        val raidEvent = ZombieRaidClientCache.active.values.firstOrNull() ?: return
        val credits = raidEvent.seenCredits

        val screenWidth = graphics.guiWidth()
        val x = screenWidth / 2 - (bgWidth / 2)
        val y = 0

        // tombstone bg
        graphics.blit(RenderPipelines.GUI_TEXTURED, if (credits) BACKGROUND_CREDITS else BACKGROUND, x, y, 0f, 0f, bgWidth, bgHeight, bgWidth, bgHeight)

        // flag health
        val barWidth = 98
        val barHeight = 5

        val flagHealthPercent = (raidEvent.flagHealth / raidEvent.flagMaxHealth).coerceIn(0f, 1f)
        val flagBarWidth = Mth.floor(barWidth * flagHealthPercent)
        val flagHealthX = x + 17 + (barWidth - flagBarWidth)
        val flagHealthY = y + 15
        val flagFrame = if (raidEvent.activeTime % 30 > 15) 1 else 0
        graphics.blit(RenderPipelines.GUI_TEXTURED, HEALTH_BAR_FLAG, flagHealthX, flagHealthY, 0f, 0f, flagBarWidth, barHeight, barWidth, barHeight)
        graphics.blit(RenderPipelines.GUI_TEXTURED, PLANT_FLAG_SPRITE, flagHealthX-7, flagHealthY-5, 7f*flagFrame, 0f, 7, 10, 14, 10)

        // zombie health
        val zombieHealthPercent = (raidEvent.zombieHealth / raidEvent.zombieHealthMax).coerceIn(0f, 1f)
        if (zombieHealthPercent > 0f) {
            val zombieBarWidth = Mth.floor(barWidth * zombieHealthPercent)
            val zombieHealthX = x + (bgWidth-barWidth) - 17
            val zombieHealthY = y + 15
            graphics.blit(RenderPipelines.GUI_TEXTURED, HEALTH_BAR_ZOMBIES, zombieHealthX, zombieHealthY, 0f, 0f, zombieBarWidth, barHeight, barWidth, barHeight)
            val headWidth = 10
            val headCount = zombieBarWidth / 8
            if (raidEvent.status != ZombieRaid.ZombieRaidStatus.NEXT_WAVE) for (i in 0..headCount) {
                val headX = zombieHealthX + (i * 8)
                val headY = zombieHealthY - if (i % 2 == 0) 3 else 0
                val headFrame = i % 4f
                graphics.blit(RenderPipelines.GUI_TEXTURED, if (credits) ZOMBIE_HEAD_CREDITS_SPRITE else  ZOMBIE_HEAD_SPRITE, headX, headY, headWidth*headFrame, 0f, headWidth, 11, headWidth*4, 11)
            }
        }

        // timer
        val time = raidEvent.waveTimer
        val timer = Component.literal(time.tickTimeFormat())

        val textX = screenWidth / 2 - font.width(timer) / 2
        val textY = y + 18
        val textColor = when (time) {
            in -1..200 -> 0xFF5555
            in 201..600 -> 0xFFFF55
            else -> 0xFFFFFF
        }
        if (raidEvent.status != ZombieRaid.ZombieRaidStatus.NEXT_WAVE) graphics.outlineText(font, timer, textX, textY,
            color = textColor,
            outlineColor = ARGB.multiply(textColor, 0x333333),
        )

        val waveText = "${raidEvent.wavesSpawned} / ${raidEvent.numWaves}"
        val waveX = screenWidth / 2 - font.width(waveText) / 2
        graphics.text(font, waveText, waveX, textY+14, -1)
        if (PazConfig.SHOW_DEBUG_INFO) {
            val textX = screenWidth / 2 - font.width(raidEvent.status.name) / 2
            graphics.text(font, raidEvent.status.name, textX, textY+30, -1)
        }

    }

}