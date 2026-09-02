package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import joshxviii.plantz.PlantHeadAttachment
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.util.Util
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class LawnFlamingo(properties: Properties) : HorizontalDirectionalBlock(properties), SimpleWaterloggedBlock {
    companion object {
        val CODEC: MapCodec<LawnFlamingo> = simpleCodec(::LawnFlamingo)
        val SHAPE: VoxelShape = Util.make {
            Shapes.or(
                column(6.0, 0.0, 14.0),
            )
        }
        val FACING: EnumProperty<Direction> = HorizontalDirectionalBlock.FACING
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false))
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val result = PlantHeadAttachment.potBlockInteraction(state, level, pos, player)
        return if (result == InteractionResult.PASS) super.useWithoutItem(state, level, pos, player, hitResult) else result
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
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

    override fun isPathfindable(state: BlockState, type: PathComputationType): Boolean = false

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
        return if (!state.canSurvive(level, pos)) Blocks.AIR.defaultBlockState()
        else super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val direction = Direction.DOWN
        return canSupportCenter(level, pos.relative(direction), direction.opposite)
    }

    override fun codec(): MapCodec<out LawnFlamingo> { return CODEC }
}