package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.Plant
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.arrow.AbstractArrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

//TODO Use plant projectile class instead
class Needle(
    type: EntityType<out AbstractArrow> = PazEntities.NEEDLE,
    level: Level,
    owner: Plant? = null,
) : AbstractArrow(
    type,
    owner?.x?:0.0,
    if (owner!=null) owner.y + owner.eyeHeight else 0.0,
    owner?.z?:0.0,
    level,
    ItemStack.EMPTY,
    null
) {

    init {
        this.setOwner(owner)
    }

    override fun tick() {
        super.tick()
        if(this.inGroundTime >= 100) discard()
    }

    override fun canHitEntity(entity: Entity): Boolean {
        return if (entity is Plant) false
        else super.canHitEntity(entity)
    }

    override fun getPierceLevel(): Byte = 4

    override fun getDefaultPickupItem(): ItemStack =ItemStack.EMPTY

    override fun getDefaultHitGroundSoundEvent(): SoundEvent = SoundEvents.EMPTY

}