package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class PaintBall(
    level: Level,
    owner: LivingEntity? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
) : PazProjectile(PazEntities.PAINT_BALL, level, owner, spawnOffset,
    DamageTypes.GENERIC
) {
    override fun tick() {
        super.tick()
        spawnParticle(
            PazServerParticles.SPORE,
            spread = Vec3(0.01,0.01,0.01),
            speed = 0.1
        )
    }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(
            PazServerParticles.SPORE_HIT,
            amount = 18,
            speed = 0.4
        )
    }
}