package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class BrainzFlagBlock(properties: Properties) : HorizontalDirectionalBlock(properties) {
    companion object {
        val CODEC: MapCodec<BrainzFlagBlock> = simpleCodec(::BrainzFlagBlock)
        val SHAPE: VoxelShape = column(8.0, 0.0, 16.0)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun codec(): MapCodec<out BrainzFlagBlock> { return CODEC }
}