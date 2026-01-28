package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.entity.MailboxBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.*
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

class MailboxBlock(
    properties: Properties,
    color: DyeColor = DyeColor.WHITE
) : BaseEntityBlock(properties) {
    companion object {
        val CODEC: MapCodec<MailboxBlock> = RecordCodecBuilder.mapCodec {
            it.group(
                DyeColor.CODEC.fieldOf("color").forGetter { b -> b.color },
                propertiesCodec()
            ).apply(it) { color, properties -> MailboxBlock(properties, color) }
        }

        val FACING: EnumProperty<Direction> = HorizontalDirectionalBlock.FACING
        val FACE: EnumProperty<AttachFace> = BlockStateProperties.ATTACH_FACE
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
        val OPEN: BooleanProperty = BlockStateProperties.OPEN
        val SHAPE: VoxelShape = column(8.0, 12.0, 0.0, 8.0)
        var SHAPES: MutableMap<Direction.Axis, VoxelShape> = Shapes.rotateHorizontalAxis(SHAPE)
        var SHAPES_WALL: MutableMap<Direction, VoxelShape> = Shapes.rotateHorizontal(SHAPE.move(0.0,0.25,0.125))
        val mailboxByColor = mapOf<DyeColor, Block>(
            DyeColor.WHITE to      PazBlocks.MAILBOX,
            DyeColor.LIGHT_GRAY to PazBlocks.LIGHT_GRAY_MAILBOX,
            DyeColor.GRAY to       PazBlocks.GRAY_MAILBOX,
            DyeColor.BLACK to      PazBlocks.BLACK_MAILBOX,
            DyeColor.BROWN to      PazBlocks.BROWN_MAILBOX,
            DyeColor.RED to        PazBlocks.RED_MAILBOX,
            DyeColor.ORANGE to     PazBlocks.ORANGE_MAILBOX,
            DyeColor.YELLOW to     PazBlocks.YELLOW_MAILBOX,
            DyeColor.LIME to       PazBlocks.LIME_MAILBOX,
            DyeColor.GREEN to      PazBlocks.GREEN_MAILBOX,
            DyeColor.CYAN to       PazBlocks.CYAN_MAILBOX,
            DyeColor.LIGHT_BLUE to PazBlocks.LIGHT_BLUE_MAILBOX,
            DyeColor.BLUE to       PazBlocks.BLUE_MAILBOX,
            DyeColor.PURPLE to     PazBlocks.PURPLE_MAILBOX,
            DyeColor.MAGENTA to    PazBlocks.MAGENTA_MAILBOX,
            DyeColor.PINK to       PazBlocks.PINK_MAILBOX,
        )
    }
    private val color: DyeColor = DyeColor.WHITE

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.FLOOR).setValue(WATERLOGGED, false).setValue(OPEN, false))
    }

    override fun newBlockEntity(worldPosition: BlockPos, blockState: BlockState): BlockEntity {
        return MailboxBlockEntity(worldPosition, blockState, color)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val facing = state.getValue(FACING)
        return if (state.getValue(FACE) == AttachFace.WALL)
            SHAPES_WALL[facing] as VoxelShape
        else
            SHAPES[facing.axis] as VoxelShape
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, FACE, WATERLOGGED, OPEN)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {

//        return defaultBlockState()
//            .setValue(FACING, context.horizontalDirection.opposite)
//            .setValue(WATERLOGGED, replacedFluidState.`is`(Fluids.WATER))
        val facing = context.horizontalDirection.opposite
        for (direction in context.getNearestLookingDirections()) {
            var state: BlockState
            if (direction.axis === Direction.Axis.Y) {
                state = defaultBlockState().setValue(FACE, AttachFace.FLOOR)
                    .setValue(FACING, facing)
            } else {
                state = defaultBlockState()
                    .setValue(FaceAttachedHorizontalDirectionalBlock.FACE, AttachFace.WALL)
                    .setValue(HorizontalDirectionalBlock.FACING, direction.opposite)
            }

            if (state.canSurvive(context.level, context.clickedPos)) {
                val replacedFluidState = context.level.getFluidState(context.clickedPos)
                return state.setValue(WATERLOGGED, replacedFluidState.`is`(Fluids.WATER))
            }
        }

        return null
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
        return if (getConnectedDirection(state).opposite == directionToNeighbour && !state.canSurvive(level, pos))
            Blocks.AIR.defaultBlockState()
        else
            super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return canAttach(level, pos, getConnectedDirection(state).opposite)
    }

    fun canAttach(level: LevelReader, pos: BlockPos, direction: Direction): Boolean {
        val relative = pos.relative(direction)
        return if (direction == Direction.DOWN) canSupportCenter(level, relative, direction.opposite)
        else level.getBlockState(relative).isFaceSturdy(level, relative, direction.opposite)
    }

    fun getConnectedDirection(state: BlockState): Direction {
        return when (state.getValue(FACE)) {
            AttachFace.CEILING -> Direction.UP
            AttachFace.FLOOR -> Direction.UP
            else -> state.getValue(FACING)
        }
    }

    override fun codec(): MapCodec<out MailboxBlock> { return CODEC }
}