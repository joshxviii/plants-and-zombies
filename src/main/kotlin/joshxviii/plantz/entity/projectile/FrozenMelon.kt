package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazEntities
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class FrozenMelon(
    level: Level,
    owner: LivingEntity? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
) : PazProjectile(
    PazEntities.FROZEN_MELON, level, owner, spawnOffset,
    PazDamageTypes.PLANT_FREEZE,
    damage = 3.0f,
    knockback = 0.35
) {
    override fun getDefaultGravity(): Double = 0.03

    override fun afterHitEntityEffect(target: LivingEntity) {
        super.afterHitEntityEffect(target)
        target.addEffect(MobEffectInstance(PazEffects.CHILLED, 150, 0))
    }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        knockbackNearby(distance = 1.25)
        spawnParticle(
            ItemParticleOption(
                ParticleTypes.ITEM,
                Items.BLUE_ICE
            ),
            amount = 45,
            speed = 0.13,
            spread = Vec3(0.6, 0.2, 0.6)
        )
    }
}