package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult

class PeaFire(
    type: EntityType<out PeaProjectile> = PazEntities.PEA_FIRE,
    level: Level,
    owner: Plant? = null,
) : PeaProjectile(type, level, owner,
    DamageTypes.IN_FIRE,
) {
    override fun onHitEntity(hitResult: EntityHitResult) {
        super.onHitEntity(hitResult)
        val target = hitResult.entity
        target.igniteForSeconds(3.0f);
    }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        PazServerParticles.FIRE_PEA_HIT
    }
}