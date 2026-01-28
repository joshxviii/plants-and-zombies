package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class BrainzFlagBlock(properties: Properties) : Block(properties) {
    companion object {
        val CODEC: MapCodec<BrainzFlagBlock> = simpleCodec(::BrainzFlagBlock)
        val SHAPE: VoxelShape = column(8.0, 0.0, 16.0)
        val FACING: EnumProperty<Direction> = HorizontalDirectionalBlock.FACING
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue<Direction, Direction>(FACING, rotation.rotate(state.getValue<Direction>(FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }

    override fun updateShape(
        state: BlockState,
        level: LevelReader,
        ticks: ScheduledTickAccess,
        pos: BlockPos,
        directionToNeighbour: Direction,
        neighbourPos: BlockPos,
        neighbourState: BlockState,
        random: RandomSource
    ): BlockState {
        return if (directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos))
            Blocks.AIR.defaultBlockState()
        else
            super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val direction = Direction.DOWN
        return canSupportCenter(level, pos.relative(direction), direction.opposite)
    }

    override fun codec(): MapCodec<out BrainzFlagBlock> { return CODEC }
}