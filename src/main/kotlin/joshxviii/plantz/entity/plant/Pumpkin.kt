package joshxviii.plantz.entity.plant

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class Pumpkin(type: EntityType<out Plant>, level: Level) : Plant(type, level) {
    override fun registerGoals() {
        super.registerGoals()
    }
    override fun attackGoals() {}
    override fun canLookAround(): Boolean = false
    override fun canSurviveOn(block: BlockState): Boolean = true

    override fun isPickable(): Boolean = !isAttached()
    override fun canBeCollidedWith(other: Entity?): Boolean = !isAttached()

    override fun onAttachmentChanged() {
        refreshDimensions()
    }
}
