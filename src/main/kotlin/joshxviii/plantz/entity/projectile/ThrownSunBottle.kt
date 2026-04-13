package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.entity.Sun
import net.minecraft.core.Position
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class ThrownSunBottle(
    level: Level,
    mob: LivingEntity? = null,
    itemStack: ItemStack? = null,
    position: Position? = null,
    entityType: EntityType<out ThrownSunBottle> = PazEntities.THROWN_SUN_BOTTLE
): ThrowableItemProjectile(entityType, level) {

    init {
        if (mob != null) {
            setOwner(mob)
            setPos(mob.x, mob.y+mob.eyeHeight*0.75, mob.z)
        }
        if (position!= null) setPos(position.x(), position.y(), position.z())
        if (itemStack != null) item = itemStack
    }

    override fun getDefaultItem(): Item = PazItems.SUN_BOTTLE

    override fun getDefaultGravity(): Double = 0.03

    protected override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        val level = this.level()
        if (level is ServerLevel) {
            level.levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, blockPosition(), -79907)
            if (hitResult is BlockHitResult) {
                val blockNormalHit = hitResult.direction.unitVec3
                Sun.awardWithDirection(level, hitResult.getLocation(), blockNormalHit, 8)
            } else {
                Sun.awardWithDirection(
                    level,
                    hitResult.getLocation(),
                    this.deltaMovement.scale(-1.0),
                    8
                )
            }
            discard()
        }
    }
}