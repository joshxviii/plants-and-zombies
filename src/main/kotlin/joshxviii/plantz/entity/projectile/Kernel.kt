package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class Kernel(
    level: Level,
    owner: Plant? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
) : PlantProjectile(
    PazEntities.KERNEL, level, owner, spawnOffset,
    PazDamageTypes.PLANT,
    damage = 1.0f
) {
    override fun getDefaultGravity(): Double = 0.03

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(
            BlockParticleOption(
                ParticleTypes.BLOCK,
                Blocks.HONEYCOMB_BLOCK.defaultBlockState()
            ),
            amount = 16,
            speed = 0.1,
            spread = Vec3(0.2, 0.2, 0.2)
        )
    }
}