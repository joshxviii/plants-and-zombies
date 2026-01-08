package joshxviii.plantz

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.world.entity.AnimationState

class PlantRenderState : LivingEntityRenderState() {
    var damagedAmount: Float = 0.0f
    var texturePath: String = "default"
    val idleAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()
    val coolDownAnimationState: AnimationState = AnimationState()
}