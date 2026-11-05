package joshxviii.plantz

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import joshxviii.plantz.gui.PazOptionsScreen

class PazModMenu : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> =
        ConfigScreenFactory { parent -> PazOptionsScreen(parent) }
}
