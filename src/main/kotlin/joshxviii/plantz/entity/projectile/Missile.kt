package joshxviii.plantz.entity.projectile

import joshxviii.plantz.NukeSmokeParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class Missile(
    level: Level,
    owner: LivingEntity? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
) : PazProjectile(PazEntities.MISSILE, level, owner, spawnOffset,
    PazDamageTypes.ZOMBIE_EXPLODE
) {
    override fun tick() {
        super.tick()
        if (tickCount % 2 == 0) spawnParticle(
            NukeSmokeParticleOptions(color = 0x434343, scale = 0.2f),
            amount = 1,
            spread = Vec3(0.01,0.01,0.01),
            speed = 0.4
        )
    }

    override fun getDefaultGravity(): Double { return 0.01 }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        explode(3f)
    }
}