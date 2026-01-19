package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
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

    override fun createRenderState(): PlantRenderState {
        return PlantRenderState()
    }

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)

        state.isAsleep = entity.isAsleep
        state.damagedAmount = entity.damagedPercent
        state.texturePath = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type).path
        state.initAnimationState.copyFrom(entity.initAnimationState)
        state.idleAnimationState.copyFrom(entity.idleAnimationState)
        state.actionAnimationState.copyFrom(entity.actionAnimationState)
        state.coolDownAnimationState.copyFrom(entity.coolDownAnimationState)
        state.rechargeAnimationState.copyFrom(entity.rechargeAnimationState)
        state.sleepAnimationState.copyFrom(entity.sleepAnimationState)
    }

    override fun getTextureLocation(state: PlantRenderState): Identifier {
        val base = "textures/entity/plant/${state.texturePath}/${state.texturePath}"
        val rm = Minecraft.getInstance().resourceManager

        val suffixes = buildList {
            if (state.isBaby)   add("_baby")
            if (state.isAsleep) add("_sleep")

            when {
                state.damagedAmount >= 0.75f -> add("_damage_medium")
                state.damagedAmount >= 0.5f  -> add("_damage_low")
            }
        }

        for (suffix in suffixes.permutationsDescending()) {
            val candidate = pazResource("$base$suffix.png")
            if (rm.getResource(candidate).isPresent) return candidate
        }

        return pazResource("$base.png")
    }
}

class PlantRenderState : LivingEntityRenderState() {
    var damagedAmount: Float = 0.0f
    var isAsleep: Boolean = false
    var texturePath: String = "default"
    val initAnimationState: AnimationState = AnimationState()
    val idleAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()
    val coolDownAnimationState: AnimationState = AnimationState()
    val rechargeAnimationState: AnimationState = AnimationState()
    val sleepAnimationState: AnimationState = AnimationState()
}