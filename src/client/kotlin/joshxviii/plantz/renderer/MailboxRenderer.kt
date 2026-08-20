package joshxviii.plantz.renderer

import com.mojang.authlib.minecraft.client.MinecraftClient
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import joshxviii.plantz.PazEffects
import joshxviii.plantz.block.TimeMachineBlock
import joshxviii.plantz.block.TimeMachineState
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.block.entity.TimeMachineBlockEntity
import joshxviii.plantz.effect.GardenHeroEffect
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SpellParticleOption
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.pow

class MailboxRenderer() : BlockEntityRenderer<MailboxBlockEntity, MailboxRenderState> {
    override fun createRenderState(): MailboxRenderState {
        return MailboxRenderState()
    }

    var doParticles = true

    override fun extractRenderState(
        mailbox: MailboxBlockEntity,
        state: MailboxRenderState,
        partialTicks: Float,
        cameraPosition: Vec3,
        breakProgress: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        super.extractRenderState(mailbox, state, partialTicks, cameraPosition, breakProgress)
        // display particles on mail boxes when local player is the hero
        val player = Minecraft.getInstance().player ?: return
        state.level = player.level()
        state.tickCount = player.tickCount
        state.isClientPlayerHero = player.hasEffect(PazEffects.GARDEN_HERO)
    }

    override fun submit(
        state: MailboxRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        val level = state.level ?: return
        if (state.tickCount % 5 == 0 && level.random.nextFloat()<.1f && !doParticles) doParticles = true
        if (doParticles && state.isClientPlayerHero) state.level?.let {
            val speed = 0.1
            val distance = 0.4
            val direction = Vec3(it.random.nextDouble() - 0.5, it.random.nextDouble() - 0.5, it.random.nextDouble() - 0.5).normalize()
            val xd = direction.scale(speed).x
            val yd = direction.scale(speed).y
            val zd = direction.scale(speed).z
            val x = state.blockPos.x + 0.5 + direction.scale(distance).x
            val y = state.blockPos.y + 0.33 + direction.scale(distance).y
            val z = state.blockPos.z + 0.5 + direction.scale(distance).z
            it.addParticle(SpellParticleOption.create(ParticleTypes.EFFECT, GardenHeroEffect.EFFECT_COLOR, 0f), x, y, z, xd, yd + .1, zd)
            doParticles = false
        }
    }
}

class MailboxRenderState : BlockEntityRenderState() {
    var isClientPlayerHero: Boolean = false
    var level: Level? = null
    var tickCount: Int = 0
}