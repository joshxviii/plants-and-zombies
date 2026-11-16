package joshxviii.plantz.entity.plant

import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Pose
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

class Pumpkin(type: EntityType<out Plant>, level: Level) : Plant(type, level) {
    override fun registerGoals() {
        super.registerGoals()
    }
    override fun attackGoals() {}
    override fun canLookAround(): Boolean = false
    override fun canSurviveOn(block: BlockState): Boolean = true

    override fun getDefaultDimensions(pose: Pose): EntityDimensions {
        return if (isAttached()) EntityDimensions.fixed(0f, 0f) else super.getDefaultDimensions(pose)
    }

}