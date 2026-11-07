package joshxviii.plantz

import joshxviii.plantz.command.PazCommands
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.block.entity.MailboxManager
import joshxviii.plantz.block.entity.getMailboxMailQueue
import joshxviii.plantz.block.entity.getTimeMachineManager
import joshxviii.plantz.block.entity.TimeMachineBlockEntity
import joshxviii.plantz.networking.ServerConfigResponsePayload
import joshxviii.plantz.recipe.DoorShieldRecipe
import joshxviii.plantz.raid.ZombieRaids
import joshxviii.plantz.raid.getZombieRaids
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


object PazMain : ModInitializer {
	const val MODID = "plantz"
	@JvmField
	val LOGGER: Logger = LogManager.getLogger("Plants & Zombies")

	override fun onInitialize() {
		PazRegistries.initialize()
		DoorShieldRecipe.register()
		PazEnchantments.initialize()
		PazConfig.load()
		// clear client raid cache + load config
		ServerLifecycleEvents.SERVER_STARTING.register {
			ZombieRaids.clearActiveRaidCenters()
			PazConfig.load()
		}
		// config sync
		ServerPlayerEvents.JOIN.register { player ->
			val json = PazConfig.let { it.GSON.toJson(it.server) }
			val payload = ServerConfigResponsePayload(json)
			ServerPlayNetworking.send(player, payload)

			LOGGER.info("Sent server config to ${player.name.string}")
		}
		// raid ticking
		ServerTickEvents.END_LEVEL_TICK.register { it.getZombieRaids().tick(it) }
		ServerTickEvents.END_SERVER_TICK.register { it.overworld().getTimeMachineManager().tick(it) }

		// mailbox managing
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register { blockEntity, level ->
			(blockEntity as? TimeMachineBlockEntity)?.let { level.getTimeMachineManager().register(it) }
			(blockEntity as? MailboxBlockEntity)?.let {
				MailboxManager.registerMailbox(level, it)
				level.getMailboxMailQueue().deliverTo(it)
			}
		}
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register { blockEntity, level -> }

		PazCommands.initialize()
		PazServerParticles.initialize()
		PazBlocks.initialize()
		PazItems.initialize()
		PazLootTables.initialize()
		PazEffects.initialize()
		PazCreativeTabs.initialize()
		PazEntities.initialize()
		PazDamageTypes.initialize()
		PazCriteria.initialize()
		PazDataSerializers.initialize()
		PazAttributes.initialize()
		PazSounds.initialize()
		PazSpawnPlacements.initialize()
		PazMenus.initialize()
		PazNetwork.initialize()
		PazJukeboxSongs.initialize()
		PazPoiTypes.initialize()
	}
}
