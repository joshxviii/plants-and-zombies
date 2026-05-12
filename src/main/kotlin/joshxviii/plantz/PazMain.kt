package joshxviii.plantz

import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.block.entity.MailboxManager
import joshxviii.plantz.networking.ServerConfigResponsePayload
import joshxviii.plantz.raid.getZombieRaids
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.impl.menu.Networking
import net.minecraft.util.profiling.jfr.event.NetworkSummaryEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


object PazMain : ModInitializer {
	const val MODID = "plantz"
	@JvmField
	val LOGGER: Logger = LogManager.getLogger()

	override fun onInitialize() {
		PazConfig.load()
		ServerLifecycleEvents.SERVER_STARTING.register { PazConfig.load() }
		ServerPlayerEvents.JOIN.register { player ->
			val json = PazConfig.let { it.GSON.toJson(it.server) }
			val payload = ServerConfigResponsePayload(json)
			ServerPlayNetworking.send(player, payload)

			LOGGER.info("Sent server config to ${player.name.string}")
		}

		ServerTickEvents.END_LEVEL_TICK.register { it.getZombieRaids().tick(it) }

		// mailbox managing
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register { blockEntity, level ->
			(blockEntity as? MailboxBlockEntity)?.let {
				MailboxManager.registerMailbox(level, it)
			}
		}
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register { blockEntity, level ->
			(blockEntity as? MailboxBlockEntity)?.let {
				MailboxManager.unregisterMailbox(level, blockEntity.blockPos)
			}
		}

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
		PazJukeboxSongs.initialize()
	}
}

