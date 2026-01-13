package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class Melon(
    type: EntityType<out PlantProjectile> = PazEntities.MELON,
    level: Level,
    owner: Plant? = null,
    spawnOffset: Vec2 = Vec2.ZERO
) : PlantProjectile(type, level, owner, spawnOffset,
    PazDamageTypes.PLANT,
) {
    override fun getDefaultGravity(): Double = 0.09

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(
            ItemParticleOption(
                ParticleTypes.ITEM,
                Items.MELON_SLICE.defaultInstance
            ),
            amount = 45,
            speed = 0.1,
            spread = Vec3(0.6, 0.2, 0.6)
        )
    }
}