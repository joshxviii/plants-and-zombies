package joshxviii.plantz

import joshxviii.plantz.entity.projectile.PlantProjectile
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.world.entity.EntityType

class ProjectileRenderState : EntityRenderState() {
    var type: EntityType<out PlantProjectile>? = null
}