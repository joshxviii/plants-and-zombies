package joshxviii.plantz

import joshxviii.plantz.block.entity.MailboxManager
import joshxviii.plantz.gui.MailboxScreen
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents
import net.minecraft.client.gui.screens.MenuScreens

object PazScreens {

    fun registerAll() {
        MenuScreens.register(PazMenus.MAILBOX_MENU, ::MailboxScreen)
    }

}