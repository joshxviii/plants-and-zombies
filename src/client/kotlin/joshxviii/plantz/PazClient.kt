package joshxviii.plantz

import joshxviii.plantz.PazItems.DYE_BLASTER
import joshxviii.plantz.PazItems.FOOTBALL_HELMET
import joshxviii.plantz.PazItems.GARDENING_GLOVE
import joshxviii.plantz.PazItems.PLANT_POT_HELMET
import joshxviii.plantz.item.DyeBlasterItem
import joshxviii.plantz.item.FootballHelmetItem
import joshxviii.plantz.item.GardeningGloveItem
import joshxviii.plantz.item.PlantPotHelmetItem
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManagerReloadListener

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		PazConfig.load()
		PazModels.registerAll()
		PazParticles.registerAll()
		PazScreens.registerAll()
		PazClientNetwork.initialize()
		PazRenderPipelines.initialize()

		ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(
			pazResource("server_data_reload"),
			ResourceManagerReloadListener({ PazNetwork.ZombieRaidClientCache.clear() })
		)
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
			pazResource("client_reload"),
			ResourceManagerReloadListener({ if (PazNetwork.ZombieRaidClientCache.isActive()) { RaidMusicManager.restart() } })
		)

		// custom tooltips
		ItemTooltipCallback.EVENT.register { stack, context, type, lines ->
			val minecraft = Minecraft.getInstance()
			val sneakKey = keybindFormat(minecraft.options.keyShift)
			val sprintKey = keybindFormat(minecraft.options.keySprint)
			val rightClick = keybindFormat(minecraft.options.keyAttack)
			val leftClick = keybindFormat(minecraft.options.keyUse)

			if (stack.`is`(GARDENING_GLOVE)) GardeningGloveItem.addToTooltip(lines, stack, sneakKey)
			if (stack.`is`(PLANT_POT_HELMET)) PlantPotHelmetItem.addToTooltip(lines, sneakKey)
			if (stack.`is`(DYE_BLASTER)) DyeBlasterItem.addToTooltip(lines)
			if (stack.`is`(FOOTBALL_HELMET)) FootballHelmetItem.addToTooltip(lines, sprintKey)
		}
	}
	fun keybindFormat(keybind: KeyMapping): Component {
		return Component.translatable("chat.square_brackets", keybind.translatedKeyMessage).withColor(0x51ff53)
	}
}