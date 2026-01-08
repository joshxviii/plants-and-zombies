package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult

class PeaFire(
    type: EntityType<out PlantProjectile> = PazEntities.PEA_FIRE,
    level: Level,
    owner: Plant? = null,
) : PlantProjectile(type, level, owner,
    DamageTypes.IN_FIRE,
    PazParticles.FIRE_PEA_HIT
) {
    override fun onHitEntity(hitResult: EntityHitResult) {
        val target = hitResult.entity
        target.igniteForSeconds(2.0f);

        super.onHitEntity(hitResult)
    }

    override fun onHitBlock(hitResult: BlockHitResult) {
        super.onHitBlock(hitResult)

        val world = this.level()
        if (world is ServerLevel) {
            if (random.nextFloat() < 0.3f) { // 30% chance
                val pos = hitResult.blockPos
                val sidePos = pos.offset(hitResult.blockPos.above())

                if (world.getBlockState(sidePos).isAir) {
                    world.setBlock(sidePos, Blocks.FIRE.defaultBlockState(), 1)
                }
            }
        }
    }
}