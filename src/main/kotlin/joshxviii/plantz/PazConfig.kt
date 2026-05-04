package joshxviii.plantz

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EntityType
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writer

object PazConfig {
    const val CONFIG_PATH = "plants-and-zombies.json"

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()

    private val path: Path = FabricLoader.getInstance().configDir.resolve(CONFIG_PATH)

    private var data = Data()

    data class Data(
        var seedGrowTime: Int = 7800,
        var extraGrowTimePerSun: Int = 2100,
        var zenPotTimeReduction: Double = 0.75,
        var sunCosts: MutableMap<String, Int> = mutableMapOf(
            "plantz:sunflower"          to 5,
            "plantz:peashooter"         to 5,
            "plantz:wallnut"            to 5,
            "plantz:chomper"            to 7,
            "plantz:cherrybomb"         to 10,
            "plantz:potatomine"         to 3,
            "plantz:ice_peashooter"     to 7,
            "plantz:repeater"           to 7,
            "plantz:fire_peashooter"    to 7,
            "plantz:cactus"             to 6,
            "plantz:cabbagepult"        to 5,
            "plantz:kernelpult"         to 6,
            "plantz:melonpult"          to 10,
            "plantz:puffshroom"         to 0,
            "plantz:scaredyshroom"      to 3,
            "plantz:fumeshroom"         to 6,
            "plantz:sunshroom"          to 4,
            "plantz:hypnoshroom"        to 7,
            "plantz:doomshroom"         to 12,
            "plantz:coffeebean"         to 3,
        ),
        var coffeeBuffDuration: Int = 72_000,
        var sunBatteryMax: Int = 320,
        var showDebugInfo: Boolean = false,
    )

    fun load() {
        data = if (path.exists()) {
            try {
                path.reader().use { gson.fromJson(it, Data::class.java) ?: Data() }
            } catch (exception: Exception) {
                PazMain.LOGGER.error("Failed to load plantz config. Using defaults.", exception)
                Data()
            }
        } else Data()
        save()
    }

    fun save() {
        try {
            Files.createDirectories(path.parent)
            path.writer().use { gson.toJson(data, it) }
        } catch (exception: Exception) {
            PazMain.LOGGER.error("Failed to save plantz config.", exception)
        }
    }

    val COFFEE_BUFF_DURATION: Int
        get() = data.coffeeBuffDuration.coerceAtLeast(0)

    val SUN_BATTERY_MAX: Int
        get() = data.sunBatteryMax.coerceAtLeast(0)

    val SHOW_DEBUG_INFO: Boolean
        get() = data.showDebugInfo

    fun getGrowTime(sunCost: Int, zenBuff: Boolean): Int {
        val time = data.seedGrowTime.coerceAtLeast(0) + (sunCost * data.extraGrowTimePerSun.coerceAtLeast(0))
        return if (zenBuff) (time * data.zenPotTimeReduction.coerceIn(0.0, 1.0)).toInt() else time
    }

    fun getSunCost(entityId: Identifier?): Int {
        if (entityId == null) return 0
        return data.sunCosts[entityId.toString()]?.coerceAtLeast(0) ?: 0
    }
    fun getSunCost(type: EntityType<*>?): Int {
        val id = type?.let { BuiltInRegistries.ENTITY_TYPE.getKey(it) }
        return getSunCost(id)
    }

    fun putDefaultSunCost(entityId: Identifier, sunCost: Int) {
        data.sunCosts.putIfAbsent(entityId.toString(), sunCost.coerceAtLeast(0))
    }
}