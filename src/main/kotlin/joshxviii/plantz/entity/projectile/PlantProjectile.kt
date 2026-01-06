package joshxviii.plantz.entity.projectile

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

abstract class PlantProjectile(type: EntityType<out PlantProjectile>, level: Level) : AbstractHurtingProjectile(type, level) {

    override fun onHitEntity(hitResult: EntityHitResult) {
        super.onHitEntity(hitResult)
        val serverLevel = this.level() as? ServerLevel
        if (serverLevel != null) {
            val entity = hitResult.entity
            val owner = this.getOwner() as? LivingEntity
            owner?.setLastHurtMob(entity)

            val source = this.damageSources().mobProjectile(this, owner)
            if (entity.hurtServer(serverLevel, source, 1.0f) && entity is LivingEntity) {
                EnchantmentHelper.doPostAttackEffects(serverLevel, entity, source)
            }
        }
    }

}