package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import joshxviii.plantz.block.entity.MailboxBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class MailboxBlock(properties: Properties) : BaseEntityBlock(properties) {
    companion object {
        val CODEC: MapCodec<MailboxBlock> = simpleCodec(::MailboxBlock)
        val FACING: EnumProperty<Direction> = HorizontalDirectionalBlock.FACING
        val ATTACH_FACE: EnumProperty<AttachFace> = BlockStateProperties.ATTACH_FACE
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
        val OPEN: BooleanProperty = BlockStateProperties.OPEN
        val SHAPE: VoxelShape = column(8.0, 12.0, 0.0, 8.0)
        var SHAPES: MutableMap<Direction.Axis, VoxelShape> = Shapes.rotateHorizontalAxis(SHAPE)
        var SHAPES_ON_WALL: MutableMap<Direction.Axis, VoxelShape> = Shapes.rotateHorizontalAxis(SHAPE.move(10.0,0.0,0.0))
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(OPEN, false))
    }

    override fun newBlockEntity(worldPosition: BlockPos, blockState: BlockState): BlockEntity {
        return MailboxBlockEntity(worldPosition, blockState)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val axis = (state.getValue(HorizontalDirectionalBlock.FACING) as Direction).axis
        return SHAPES[axis] as VoxelShape
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue<Direction, Direction>(FACING, rotation.rotate(state.getValue<Direction>(FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, WATERLOGGED, OPEN)
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
        return if (directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos))
            Blocks.AIR.defaultBlockState()
        else
            super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val direction = Direction.DOWN
        return canSupportCenter(level, pos.relative(direction), direction.opposite)
    }

    override fun codec(): MapCodec<out MailboxBlock> { return CODEC }
}