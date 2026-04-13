package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.util.Util
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ZenPlantPotBlock(properties: Properties) : HorizontalDirectionalBlock(properties), SimpleWaterloggedBlock {
    companion object {
        val CODEC: MapCodec<ZenPlantPotBlock> = simpleCodec(::ZenPlantPotBlock)
        val SHAPE_INSIDE = column(12.0, 7.0, 9.0)
        val SHAPE: VoxelShape = Util.make {
            Shapes.or(
                column(16.0, 0.0, 3.0),
                column(12.0, 3.0, 8.0),
                Shapes.join(
                    column(14.0, 7.0, 9.0),
                    column(10.0, 7.0, 9.0),
                    BooleanOp.ONLY_FIRST
                )
            )
        }
        val FACING: EnumProperty<Direction> = HorizontalDirectionalBlock.FACING
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false))
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun getInteractionShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape {
        return SHAPE_INSIDE
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue<Direction, Direction>(FACING, rotation.rotate(state.getValue<Direction>(FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, WATERLOGGED)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val replacedFluidState = context.level.getFluidState(context.clickedPos)
        return defaultBlockState()
            .setValue(FACING, context.horizontalDirection.opposite)
            .setValue(WATERLOGGED, replacedFluidState.`is`(Fluids.WATER))
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
        if (state.getValue(WATERLOGGED)) ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }

    override fun codec(): MapCodec<out ZenPlantPotBlock> { return CODEC }
}