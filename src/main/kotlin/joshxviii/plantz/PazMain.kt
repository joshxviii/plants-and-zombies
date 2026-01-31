package joshxviii.plantz

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.registry.LandPathTypeRegistry
import org.apache.logging.log4j.LogManager


object PazMain : ModInitializer {
	const val MODID = "plantz"
    private val logger = LogManager.getLogger(MODID)

	override fun onInitialize() {
		PazServerParticles.initialize()
		PazBlocks.initialize()
		PazItems.initialize()
		PazEffects.initialize()
		PazCreativeTab.initialize()
		PazEntities.initialize()
		PazDamageTypes.initialize()
		PazDataSerializers.initialize()
		PazAttributes.initialize()
		PazSounds.initialize()
		PazSpawnPlacements.initialize()
		PazMenus.initialize()
	}
}

