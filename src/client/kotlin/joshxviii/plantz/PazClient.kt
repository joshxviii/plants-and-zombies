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

		ClientTickEvents.START_LEVEL_TICK.register {// display particles on mail boxes when local player is the hero
			val player = Minecraft.getInstance().player ?: return@register
			val isHero = player.hasEffect(PazEffects.GARDEN_HERO)
			if (isHero) MailboxManager.getMailboxesInLevel(it).forEach { mailbox ->
				if (it.gameTime % 10L != 0L || it.random.nextFloat()<.25f) return@forEach
				val speed = 0.15
				val direction = Vec3(it.random.nextDouble() - 0.5, it.random.nextDouble() - 0.5, it.random.nextDouble() - 0.5).normalize().scale(speed)
				val xd = direction.x
				val yd = direction.y
				val zd = direction.z
				val x = mailbox.blockPos.x + 0.5 + xd.coerceIn(-0.2, 0.2)
				val y = mailbox.blockPos.y + 0.33 + yd.coerceIn(-0.2, 0.2)
				val z = mailbox.blockPos.z + 0.5 + zd.coerceIn(-0.2, 0.2)
				it.addParticle(ParticleTypes.FIREWORK, x, y, z, xd, yd + .1, zd)
			}
		}

	}
}