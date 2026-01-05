package joshxviii.plantz

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import org.apache.logging.log4j.LogManager

object PazMain : ModInitializer {
	const val MODID = "plantz"
    private val logger = LogManager.getLogger(MODID)

	override fun onInitialize() {
		PazBlocks.initialize()
		PazItems.initialize()
		PazTabs.initialize()
		PazEntities.initialize()
		PazEntities.registerAttributes { entityType, builder -> FabricDefaultAttributeRegistry.register(entityType, builder) }
	}
}