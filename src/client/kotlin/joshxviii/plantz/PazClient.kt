package joshxviii.plantz

import com.mojang.authlib.minecraft.client.MinecraftClient
import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.mixin.client.rendering.GuiAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderPipelines

object PazClient : ClientModInitializer {
	override fun onInitializeClient() {
		PazModels.registerAll()
		PazParticles.registerAll()
		PazRenderPipelines.initialize()

	}
}