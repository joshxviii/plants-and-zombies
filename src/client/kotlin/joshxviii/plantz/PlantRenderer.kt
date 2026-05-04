package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.entity.plant.KernelPult
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.plants.WallNut
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.Mth
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import kotlin.math.pow

class PlantRenderer(
    private val defaultModel: EntityModel<PlantRenderState>,
    context: EntityRendererProvider.Context,
    private val babyModel: EntityModel<PlantRenderState>? = null,
) : MobRenderer<Plant, PlantRenderState, EntityModel<PlantRenderState>>(
    context,
    defaultModel,
    0.5f
) {
    override fun submit(
        state: PlantRenderState,
        poseStack: PoseStack,
        collector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {

        if (PazConfig.SHOW_DEBUG_INFO) collector.submitNameTag(
            poseStack, Vec3(0.0,state.eyeHeight.toDouble(),0.0), -20,
            Component.literal("${state.plantState.name}, ${state.cooldown}").withColor(0xFFFFFFF),
            true, -1, 20.0, camera
        )

        model = if (state.isBaby && babyModel != null) babyModel else defaultModel
        if (state.ageInTicks>1) super.submit(state, poseStack, collector, camera)
    }

    override fun getShadowRadius(state: PlantRenderState): Float {
        return if (state.rotations == Quaternionf()) super.getShadowRadius(state) * (0.9f) else 0f
    }

    override fun scale(state: PlantRenderState, poseStack: PoseStack) {
        super.scale(state, poseStack)
        var g = state.swelling
        val wobble = 1.0f + Mth.sin((g * 100.0f).toDouble()) * g * 0.01f
        g = Mth.clamp(g, 0.0f, 1.0f)
        val s = (1.0f + g.pow(6) * 0.4f) * wobble
        val hs = (1.0f + g.pow(6) * 0.1f) / wobble
        poseStack.scale(s, hs, s)
    }

    override fun getWhiteOverlayProgress(state: PlantRenderState): Float {
        val step = state.swelling
        return if ((step * 10.0f).toInt() % 2 == 0) 0.0f else Mth.clamp(step, 0.5f, 1.0f)
    }

    override fun createRenderState(): PlantRenderState = PlantRenderState()

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)
        val attached = entity.attachedEntity
        state.rotations = if (attached != null) {
            val pitch = -Mth.lerp(partialTick, attached.xRotO, attached.xRot)
            val yaw = Mth.lerp(partialTick, attached.yRotO, attached.yRot)
            Quaternionf()
                .rotateY(Mth.DEG_TO_RAD * (180.0f - yaw))
                .rotateX(Mth.DEG_TO_RAD * pitch)
        } else {
            Quaternionf()
        }
        state.plantState = entity.state
        state.swelling = entity.getSwelling(partialTick)
        state.cooldown = entity.cooldown
        state.isAsleep = entity.isAsleep
        state.damagedAmount = entity.damagedPercent
        state.texturePath = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type).path
        state.initAnimationState.copyFrom(entity.initAnimationState)
        state.idleAnimationState.copyFrom(entity.idleAnimationState)
        state.actionAnimationState.copyFrom(entity.actionAnimationState)
        state.coolDownAnimationState.copyFrom(entity.coolDownAnimationState)
        state.specialAnimation.copyFrom(entity.specialAnimation)
        state.sleepAnimationState.copyFrom(entity.sleepAnimationState)
        state.bounceAnimationState.copyFrom(entity.bounceAnimation)
        state.texturePathExtra =
            when (entity) {
                is WallNut -> when {
                    state.damagedAmount >= 0.75f -> "damage_medium"
                    state.damagedAmount >= 0.5f  -> "damage_low"
                    else -> ""
                }
                is KernelPult -> if (entity.hasButterShot) "butter" else ""
                else -> entity.getMagicName()
            }
    }

    override fun getTextureLocation(state: PlantRenderState): Identifier {
        val base = "textures/entity/plant/${state.texturePath}/${state.texturePath}"
        val rm = Minecraft.getInstance().resourceManager

        val suffixes = buildList {
            if (!state.texturePathExtra.isEmpty()) add(state.texturePathExtra)
            if (state.isBaby)   add("baby")
            if (state.isAsleep) add("sleep")
        }

        val textureLocation = resolveTextureLocation(base, suffixes, rm)
        return textureLocation ?: pazResource("$base.png")
    }
}

class PlantRenderState : LivingEntityRenderState() {
    var rotations: Quaternionf = Quaternionf()
    var swelling: Float = 0f
    var lastCooldownTime: Int = 0
    var cooldown: Int = 0
    var damagedAmount: Float = 0.0f
    var isAsleep: Boolean = false
    var texturePath: String = "default"
    var texturePathExtra: String = ""
    var plantState: PlantState = PlantState.IDLE
    val initAnimationState: AnimationState = AnimationState()
    val idleAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()
    val coolDownAnimationState: AnimationState = AnimationState()
    val specialAnimation: AnimationState = AnimationState()
    val sleepAnimationState: AnimationState = AnimationState()
    val bounceAnimationState: AnimationState = AnimationState()
}