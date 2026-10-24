package joshxviii.plantz.block

import net.minecraft.core.BlockPos
import net.minecraft.util.Util
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ConeBlock(properties: Properties) : Block(properties) {
    companion object {

        val SHAPE: VoxelShape = Util.make {
            Shapes.or(
                column(12.0, 0.0, 2.0),
                column(6.0, 2.0, 7.0),
                column(4.0, 7.0, 12.0),
            )
        }
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

}