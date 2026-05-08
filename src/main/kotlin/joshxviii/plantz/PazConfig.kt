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
import kotlin.math.pow

object PazConfig {
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()

    private val path: Path = FabricLoader.getInstance().configDir.resolve(PazMain.CONFIG_PATH)

    private val defaultConfig = Data()
    private var config = defaultConfig

    data class Data(
        var coopPlanting: Boolean = true,
        var playerCreditForPlantKills: Boolean = false,
        var seedGrowTime: Int = 8100,
        var extraGrowTimePerSun: Int = 2100,
        var zenPotTimeReduction: Double = 0.25,
        var hydrationSunReduction: Double = 0.5,
        var plantPotDamageReduction: Double = 0.5,
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
            "plantz:doomshroom"         to 16,
            "plantz:seashroom"          to 0,
            "plantz:coffeebean"         to 3,
        ),
        var coffeeBuffDuration: Int = 60_000,
        var sunCostTamingThreshold: Int = 30,
        var plantCooldownEnabled: Boolean = true,
        var plantCooldownTime: Int = 10,
        var plantCooldownTimePerSun: Int = 15,
        var sunBatteryMax: Int = 320,
        var showDebugInfo: Boolean = false,
    )

    fun load() {
        config = if (path.exists()) {
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
            path.writer().use { gson.toJson(config, it) }
        } catch (exception: Exception) {
            PazMain.LOGGER.error("Failed to save plantz config.", exception)
        }
    }

    val COFFEE_BUFF_DURATION: Int
        get() = config.coffeeBuffDuration.coerceAtLeast(0)

    val SUN_BATTERY_MAX: Int
        get() = config.sunBatteryMax.coerceAtLeast(0)

    val SHOW_DEBUG_INFO: Boolean
        get() = config.showDebugInfo

    val COOP_PLANTING: Boolean
        get() = config.coopPlanting

    val PLAYER_CREDIT_FOR_PLANT_KILLS: Boolean
        get() = config.playerCreditForPlantKills

    val HYDRATION_SUN_REDUCTION: Double
        get() = 1f - config.hydrationSunReduction.coerceIn(0.0, 1.0)

    val PLANT_POT_DAMAGE_REDUCTION: Double
        get() = 1f - config.plantPotDamageReduction.coerceIn(0.0, 1.0)

    val PLANT_COOLDOWN_ENABLED: Boolean
        get() = config.plantCooldownEnabled

    fun getGrowTime(sunCost: Int, zenBuff: Boolean): Int {
        val time = config.seedGrowTime.coerceAtLeast(0) + (sunCost * config.extraGrowTimePerSun.coerceAtLeast(0))
        return if (zenBuff) (time * (1f - config.zenPotTimeReduction.coerceIn(0.0, 1.0))).toInt() else time
    }

    fun getCooldownTime(sunCost: Int): Int {
        val time = config.plantCooldownTime.coerceAtLeast(1) + (sunCost * config.plantCooldownTimePerSun.coerceAtLeast(0))
        return time
    }

    fun getSunCost(type: EntityType<*>?): Int {
        val id = type?.let { BuiltInRegistries.ENTITY_TYPE.getKey(it) }
        return getSunCost(id)
    }
    fun getSunCost(entityId: Identifier?): Int {
        if (entityId == null) return 0
        val key = entityId.toString()
        val value = config.sunCosts[key]?:// config
            defaultConfig.sunCosts[key]?.let {// default
                putDefaultSunCost(entityId, it)
                it
            }
        ?: 0 // config
        return value.coerceAtLeast(0)
    }
    fun getTameChance(type: EntityType<*>?): Double {
        val a = config.sunCostTamingThreshold
        val sunCost = getSunCost(type).coerceAtMost(a)
        val weight = 3 // higher weight = harder to tame
        val chance = ((a - sunCost).toDouble() / (a + sunCost).toDouble()).pow(weight).coerceIn(0.02, 1.0)
        return chance
    }

    fun putDefaultSunCost(entityId: Identifier, sunCost: Int) {
        config.sunCosts.putIfAbsent(entityId.toString(), sunCost.coerceAtLeast(0))
        save()
    }
}