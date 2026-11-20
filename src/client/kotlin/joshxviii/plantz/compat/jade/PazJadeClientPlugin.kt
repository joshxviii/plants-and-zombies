package joshxviii.plantz.compat.jade

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.SunBatteryBlock
import joshxviii.plantz.gui.ZombieRaidOverlay
import joshxviii.plantz.pazResource
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.ITooltip
import snownee.jade.api.JadeIds
import snownee.jade.api.TooltipPosition
import snownee.jade.api.WailaPlugin
import snownee.jade.api.config.IPluginConfig
import snownee.jade.api.ui.Rect2f

@WailaPlugin
class PazJadeClientPlugin : IWailaPlugin {
    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerProgressClient(SunBatteryProgress)
        registration.addRayTraceCallback { result, accessor, accessor1 ->
            if ((accessor as? BlockAccessor)?.blockState?.`is`(PazBlocks.SUN_BATTERY_BLOCK) == true) accessor.serverData.remove(JadeIds.UNIVERSAL_ITEM_STORAGE.toString())
            return@addRayTraceCallback accessor
        }

        registration.addTooltipCollectedCallback { element, accessor ->
            ZombieRaidOverlay.getHudBounds()?.let {
                element.y = it.bottom()
            }
        }
    }
}