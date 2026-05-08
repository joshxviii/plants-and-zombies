package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class WaterSpore(
    level: Level,
    owner: LivingEntity? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
) : PazProjectile(PazEntities.WATER_SPORE, level, owner, spawnOffset,
    PazDamageTypes.PLANT,
) {
    override fun getDefaultGravity(): Double = 0.03
    override fun ignoreWaterDrag(): Boolean = true

    override fun tick() {
        super.tick()
        spawnParticle(
            ParticleTypes.BUBBLE_POP,
            spread = Vec3(0.01,0.01,0.01),
            speed = 0.1
        )
    }

    override fun afterHitEntityEffect(target: LivingEntity) {
    }

    override fun getHitSound(): SoundEvent = SoundEvents.FISHING_BOBBER_SPLASH

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(
            ParticleTypes.SPLASH,
            amount = 4,
            speed = 0.1,
            spread = Vec3(0.1, 0.1, 0.1)
        )
    }
}