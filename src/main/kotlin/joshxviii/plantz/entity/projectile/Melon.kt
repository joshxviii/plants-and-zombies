package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class Melon(
    level: Level,
    owner: LivingEntity? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
) : PazProjectile(
    PazEntities.MELON, level, owner, spawnOffset,
    PazDamageTypes.PLANT,
    damage = 4.5f,
    knockback = 0.4
) {
    override fun getDefaultGravity(): Double = 0.03

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        knockbackNearby(distance = 3.5)
        spawnParticle(
            ItemParticleOption(
                ParticleTypes.ITEM,
                Items.MELON_SLICE
            ),
            amount = 45,
            speed = 0.13,
            spread = Vec3(0.6, 0.2, 0.6)
        )
    }
}