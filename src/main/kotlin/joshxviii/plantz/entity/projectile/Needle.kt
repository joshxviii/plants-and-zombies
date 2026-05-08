package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2

class Needle(
    level: Level,
    owner: LivingEntity? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
) : PazProjectile(PazEntities.NEEDLE, level, owner, spawnOffset,
    PazDamageTypes.PLANT
) {
    override fun stickInGroundTime(): Int = 100
    override fun getPierceLevel(): Byte = 4

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
    }
}