package joshxviii.plantz.compat.jade

import joshxviii.plantz.block.entity.SunBatteryBlockEntity
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@WailaPlugin
class PazJadePlugin : IWailaPlugin {
    override fun register(registration: IWailaCommonRegistration) {
        registration.registerProgress(SunBatteryProgress, SunBatteryBlockEntity::class.java)
    }
}
