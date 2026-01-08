package joshxviii.plantz

import joshxviii.plantz.ai.PlantState
import net.fabricmc.api.ModInitializer
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.network.syncher.EntityDataSerializers
import org.apache.logging.log4j.LogManager

object PazMain : ModInitializer {
	const val MODID = "plantz"
    private val logger = LogManager.getLogger(MODID)

	override fun onInitialize() {
		PazParticles.initialize()
		PazBlocks.initialize()
		PazItems.initialize()
		PazCreativeTab.initialize()
		PazEntities.initialize()
	}
}