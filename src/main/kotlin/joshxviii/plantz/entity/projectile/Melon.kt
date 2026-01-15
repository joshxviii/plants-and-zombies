package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.Plant
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class Melon(
    type: EntityType<out PlantProjectile> = PazEntities.MELON,
    level: Level,
    spawnOffset: Vec2 = Vec2.ZERO,
    owner: Plant? = null,
) : PlantProjectile(
    type, level, owner, spawnOffset,
    PazDamageTypes.PLANT,
) {
    override fun getDefaultGravity(): Double = 0.03

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        knockbackNearby()
        spawnParticle(
            ItemParticleOption(
                ParticleTypes.ITEM,
                Items.MELON_SLICE.defaultInstance
            ),
            amount = 45,
            speed = 0.13,
            spread = Vec3(0.6, 0.2, 0.6)
        )
    }

    private fun knockbackNearby() {
        val serverLevel = this.level() as? ServerLevel?: return
        serverLevel.getEntitiesOfClass(
            LivingEntity::class.java,
            this.boundingBox.inflate(3.5)
        ).forEach { nearby: LivingEntity? ->
            if (nearby == null || !canHitEntity(nearby)) return@forEach
            val direction = nearby.position().subtract(this.position())
            val knockbackVector = direction.normalize().scale(knockback)
            if (knockback > 0.0) {
                nearby.push(knockbackVector.x, 0.32, knockbackVector.z)
                val source = this.damageSources().source(damageType, this, plantOwner)
                if(nearby.hurtServer(serverLevel, source, damage/direction.length().toFloat()*10)) {
                    val knockbackDirection = calculateHorizontalHurtKnockbackDirection(nearby, source)
                    nearby.knockback(knockback, -knockbackDirection.leftDouble(), -knockbackDirection.rightDouble())
                    if (nearby is ServerPlayer) nearby.connection.send(ClientboundSetEntityMotionPacket(nearby))
                }
            }
        }
    }
}