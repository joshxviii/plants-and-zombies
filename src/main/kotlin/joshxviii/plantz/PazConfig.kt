package joshxviii.plantz

import com.google.gson.GsonBuilder
import net.fabricmc.api.EnvType
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

data class ServerConfig(
    var coopPlanting: Boolean = true,
    var playerCreditForPlantKills: Boolean = true,
    var seedGrowTime: Int = 12000,
    var extraGrowTimePerSun: Int = 2400,
    var zenPotTimeReduction: Double = 0.25,
    var hydrationSunReduction: Double = 0.5,
    var plantPotDamageReduction: Double = 0.5,
    var coffeeBuffDuration: Int = 48_000,
    var sunCostTamingThreshold: Int = 30,
    var plantCooldownEnabled: Boolean = false,
    var plantCooldownTime: Double = 4.0,
    var plantCooldownTimePerSun: Double = 2.5,
    var solarBatteryMax: Int = 512,
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
)
data class ClientConfig(
    var showDebugInfo: Boolean = false,
)

object PazConfig {

    const val SERVER_CONFIG_PATH = "plants-and-zombies/paz-server.json"
    const val CLIENT_CONFIG_PATH = "plants-and-zombies/paz-client.json"

    val GSON = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()

    private val serverConfigPath: Path = FabricLoader.getInstance().configDir.resolve(SERVER_CONFIG_PATH)
    private val clientConfigPath: Path = FabricLoader.getInstance().configDir.resolve(CLIENT_CONFIG_PATH)

    private val defaultServerConfig = ServerConfig()
    var server = defaultServerConfig

    private val defaultClientConfig = ClientConfig()
    var client = defaultClientConfig

    fun load() {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            client = loadConfig(clientConfigPath, ClientConfig())
        }
        // Server config is always loaded on both sides (will be overwritten on client by sync)
        server = loadConfig(serverConfigPath, ServerConfig())
    }

    private inline fun <reified T> loadConfig(path: Path, default: T): T {
        return if (path.exists()) {
            try {
                path.reader().use { GSON.fromJson(it, T::class.java) } ?: default
            } catch (e: Exception) {
                PazMain.LOGGER.error("Failed to load config", e)
                default
            }
        } else default.also { saveConfig(path, it) }
    }

    private inline fun <reified T> saveConfig(path: Path, config: T) {
        try {
            Files.createDirectories(path.parent)
            path.writer().use { GSON.toJson(config, it) }
        } catch (exception: Exception) {
            PazMain.LOGGER.error("Failed to save plantz config.", exception)
        }
    }

    val SHOW_DEBUG_INFO: Boolean
        get() = client.showDebugInfo

    val COFFEE_BUFF_DURATION: Int
        get() = server.coffeeBuffDuration.coerceAtLeast(0)

    val SUN_BATTERY_MAX: Int
        get() = server.solarBatteryMax.coerceAtLeast(0)

    val COOP_PLANTING: Boolean
        get() = server.coopPlanting

    val PLAYER_CREDIT_FOR_PLANT_KILLS: Boolean
        get() = server.playerCreditForPlantKills

    val HYDRATION_SUN_REDUCTION: Double
        get() = 1f - server.hydrationSunReduction.coerceIn(0.0, 1.0)

    val PLANT_POT_DAMAGE_REDUCTION: Double
        get() = 1f - server.plantPotDamageReduction.coerceIn(0.0, 1.0)

    val PLANT_COOLDOWN_ENABLED: Boolean
        get() = server.plantCooldownEnabled

    fun getGrowTime(sunCost: Int, zenBuff: Boolean): Int {
        val time = server.seedGrowTime.coerceAtLeast(0) + (sunCost * server.extraGrowTimePerSun.coerceAtLeast(0))
        return if (zenBuff) (time * (1f - server.zenPotTimeReduction.coerceIn(0.0, 1.0))).toInt() else time
    }

    fun getCooldownTime(sunCost: Int): Float {
        val time = server.plantCooldownTime + (sunCost * server.plantCooldownTimePerSun)
        return time.toFloat().coerceAtLeast(1f)
    }

    fun getSunCost(type: EntityType<*>?): Int {
        val id = type?.let { BuiltInRegistries.ENTITY_TYPE.getKey(it) }
        return getSunCost(id)
    }

    fun getSunCost(entityId: Identifier?): Int {
        if (entityId == null) return 0
        val key = entityId.toString()
        val value = server.sunCosts[key]?:// config
            defaultServerConfig.sunCosts[key]?.let {// default
                putDefaultSunCost(entityId, it)
                it
            }
        ?: -1 // not in id list
        return value.coerceAtLeast(-1)
    }

    fun getTameChance(type: EntityType<*>?): Double {
        val a = server.sunCostTamingThreshold
        val sunCost = getSunCost(type).coerceAtMost(a)
        val weight = 3 // higher weight = harder to tame
        val chance = ((a - sunCost).toDouble() / (a + sunCost).toDouble()).pow(weight).coerceIn(0.02, 1.0)
        return chance
    }

    fun putDefaultSunCost(entityId: Identifier, sunCost: Int) {
        server.sunCosts.putIfAbsent(entityId.toString(), sunCost.coerceAtLeast(0))
        saveConfig(serverConfigPath, server)
    }
}