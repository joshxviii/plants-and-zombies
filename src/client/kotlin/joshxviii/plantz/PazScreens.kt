package joshxviii.plantz

import joshxviii.plantz.gui.MailCollectionBoxScreen
import joshxviii.plantz.gui.MailboxScreen
import joshxviii.plantz.gui.TimeMachineScreen
import joshxviii.plantz.gui.ZombieRaidOverlay
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.gui.screens.MenuScreens

object PazScreens {

    fun registerAll() {
        MenuScreens.register(PazMenus.MAILBOX_MENU, ::MailboxScreen)
        MenuScreens.register(PazMenus.MAIL_COLLECTION_BOX_MENU, ::MailCollectionBoxScreen)
        MenuScreens.register(PazMenus.TIME_MACHINE_MENU, ::TimeMachineScreen)

        HudElementRegistry.attachElementAfter(
            VanillaHudElements.BOSS_BAR,
            pazResource("zombie_raid"),
            ZombieRaidOverlay::extractRenderState
        )
    }

}