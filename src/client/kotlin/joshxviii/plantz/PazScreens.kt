package joshxviii.plantz

import joshxviii.plantz.gui.MailboxScreen
import net.minecraft.client.gui.screens.MenuScreens

object PazScreens {

    fun registerAll() {
        MenuScreens.register(PazMenus.MAILBOX_MENU, ::MailboxScreen)
    }

}