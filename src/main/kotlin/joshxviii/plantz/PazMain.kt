package joshxviii.plantz

import joshxviii.plantz.raid.getZombieRaids
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.registry.LandPathTypeRegistry
import net.minecraft.server.level.ServerLevel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


object PazMain : ModInitializer {
	const val MODID = "plantz"
	@JvmField
	val LOGGER: Logger = LogManager.getLogger()

	override fun onInitialize() {

		ServerTickEvents.END_LEVEL_TICK.register { it.getZombieRaids().tick(it) }

		PazServerParticles.initialize()
		PazBlocks.initialize()
		PazItems.initialize()
		PazLootTables.initialize()
		PazEffects.initialize()
		PazCreativeTab.initialize()
		PazEntities.initialize()
		PazDamageTypes.initialize()
		PazCriteria.initialize()
		PazDataSerializers.initialize()
		PazAttributes.initialize()
		PazSounds.initialize()
		PazSpawnPlacements.initialize()
		PazMenus.initialize()
		PazNetwork.initialize()
	}
}

