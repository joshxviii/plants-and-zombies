package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.plant.KernelPult
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.plants.WallNut
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import net.minecraft.world.entity.AnimationState

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
        model = if (state.isBaby && babyModel != null) babyModel else defaultModel
        if (state.ageInTicks>1) super.submit(state, poseStack, collector, camera)
    }

    override fun getWhiteOverlayProgress(state: PlantRenderState): Float {
        return 0f
    }

    override fun createRenderState(): PlantRenderState {
        return PlantRenderState()
    }

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)

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
        state.texturePathExtra =
            when (entity) {
                is WallNut -> when {
                    state.damagedAmount >= 0.75f -> "damage_medium"
                    state.damagedAmount >= 0.5f  -> "damage_low"
                    else -> ""
                }
                is KernelPult -> if (entity.hasButterShot) "butter" else ""
                else -> ""
            }
    }

    override fun getTextureLocation(state: PlantRenderState): Identifier {
        val base = "textures/entity/plant/${state.texturePath}/${state.texturePath}"
        val rm = Minecraft.getInstance().resourceManager

        val suffixes = buildList {
            if (state.isBaby)   add("baby")
            if (state.isAsleep) add("sleep")
            add(state.texturePathExtra)
        }

        return resolveTextureLocation(base, suffixes, rm) ?: pazResource("$base.png")
    }

}

class PlantRenderState : LivingEntityRenderState() {
    var lastCooldownTime: Int = 0
    var cooldown: Int = 0
    var damagedAmount: Float = 0.0f
    var isAsleep: Boolean = false
    var texturePath: String = "default"
    var texturePathExtra: String = ""
    val initAnimationState: AnimationState = AnimationState()
    val idleAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()
    val coolDownAnimationState: AnimationState = AnimationState()
    val specialAnimation: AnimationState = AnimationState()
    val sleepAnimationState: AnimationState = AnimationState()
}