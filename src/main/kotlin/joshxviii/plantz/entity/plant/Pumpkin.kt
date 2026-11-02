package joshxviii.plantz.entity.plant

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
    override fun clampToGrid(): Boolean = !getBlockBelow().getCollisionShape(level(), blockPosition().below()).isEmpty
}