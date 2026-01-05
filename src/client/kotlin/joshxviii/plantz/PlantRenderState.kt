package joshxviii.plantz

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.world.entity.AnimationState

class PlantRenderState : LivingEntityRenderState() {
    var isPotted: Boolean = false
    var damage: Float = 0.0f
    var texturePath: String = "default"
    val idleAnimationState: AnimationState = AnimationState()
    val attackAnimationState: AnimationState = AnimationState()
}