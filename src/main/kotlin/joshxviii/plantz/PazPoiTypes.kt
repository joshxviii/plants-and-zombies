package joshxviii.plantz

import net.fabricmc.fabric.api.`object`.builder.v1.world.poi.PoiHelper
import joshxviii.plantz.PazBlocks.PLANTZ_FLAG_BLOCK
import joshxviii.plantz.PazBlocks.SUN_BATTERY_BLOCK
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.ai.village.poi.PoiType

object PazPoiTypes {

    @JvmField val SUN_BATTERY = createKey("sun_battery")
    @JvmField val PLANTZ_FLAG = createKey("plantz_flag")

    private fun createKey(name: String): ResourceKey<PoiType> {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, pazResource(name))
    }

    fun initialize() {
        PoiHelper.register(SUN_BATTERY.identifier(), 1, 1, SUN_BATTERY_BLOCK)
        PoiHelper.register(PLANTZ_FLAG.identifier(), 1, 1, PLANTZ_FLAG_BLOCK)
    }
}
