package joshxviii.plantz

import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.block.entity.MailboxManager
import joshxviii.plantz.block.entity.SunBatteryBlockEntity
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.phys.Vec3

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		PazConfig.load()
		PazModels.registerAll()
		PazParticles.registerAll()
		PazScreens.registerAll()
		PazClientNetwork.initialize()
		PazRenderPipelines.initialize()
	}
}