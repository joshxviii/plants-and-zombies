package joshxviii.plantz

import net.minecraft.client.renderer.entity.state.EntityRenderState

class ProjectileRenderState : EntityRenderState() {
    var xRot: Float = 0f
    var yRot: Float = 0f
    var texturePath: String = "default"
    var emissive: Boolean = false
}