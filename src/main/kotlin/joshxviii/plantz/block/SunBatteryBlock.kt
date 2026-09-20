package joshxviii.plantz.block

import com.mojang.math.OctahedralGroup
import com.mojang.serialization.MapCodec
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazItems
import joshxviii.plantz.block.entity.SunBatteryBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.util.Util
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
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
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.ToIntFunction

class SunBatteryBlock(properties: Properties) : BaseEntityBlock(properties), SimpleWaterloggedBlock  {
    companion object {
        val CODEC: MapCodec<SunBatteryBlock> = simpleCodec(::SunBatteryBlock)
        val FACE: EnumProperty<AttachFace> = BlockStateProperties.ATTACH_FACE
        val FACING: EnumProperty<Direction> = HorizontalDirectionalBlock.FACING
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
        val LEVEL: IntegerProperty = BlockStateProperties.LEVEL
        val LIGHT_EMISSION: ToIntFunction<BlockState> = { it.getValue(LightBlock.LEVEL) }

        val SHAPE: VoxelShape = Shapes.rotate(column(8.0, 0.0, 11.0), OctahedralGroup.ROT_90_REF_X_NEG)
        var SHAPES: Map<AttachFace, Map<Direction, VoxelShape>> = Shapes.rotateAttachFace(SHAPE)
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.FLOOR).setValue(WATERLOGGED, false).setValue(LEVEL, 0))
    }

    override fun useItemOn(
        itemStack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (itemStack.`is`(PazItems.SUN)) {
            val blockEntity = level.getBlockEntity(pos)
            (blockEntity as? SunBatteryBlockEntity)?.let {
                if (it.isFull()) return InteractionResult.FAIL
                it.addSun(1)
                itemStack.consume(1, player)
                return InteractionResult.SUCCESS
            }
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPES[state.getValue(FACE)]!![state.getValue(FACING)]!!
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)))
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.rotate(mirror.getRotation(state.getValue(FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, FACE, WATERLOGGED, LEVEL)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val level = context.level
        val clicked = context.clickedFace
        val pos = context.clickedPos
        val fluid = level.getFluidState(pos)
        val sunLevel = context.itemInHand.get(PazComponents.STORED_SUN)?.getLevel() ?: 0

        for (dir in context.nearestLookingDirections) {
            val state = if (dir.axis === Direction.Axis.Y) {
                defaultBlockState()
                    .setValue(FACE, if (dir == Direction.UP) AttachFace.CEILING else AttachFace.FLOOR)
                    .setValue(FACING, context.horizontalDirection)
            } else {
                defaultBlockState()
                    .setValue(FACE, AttachFace.WALL)
                    .setValue(FACING, dir.opposite)
            }
                .setValue(WATERLOGGED, fluid.`is`(Fluids.WATER))
                .setValue(LEVEL, sunLevel)

            if (state.canSurvive(level, pos)) {
                return state
            }
        }
        return null
    }

    override fun newBlockEntity(worldPosition: BlockPos, blockState: BlockState): BlockEntity {
        return SunBatteryBlockEntity(worldPosition, blockState)
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        val blockEntity = level.getBlockEntity(pos)
        (blockEntity as? SunBatteryBlockEntity)?.let {
            if (player.hasInfiniteMaterials()) it.clearContent()
        }
        return super.playerWillDestroy(level, pos, state, player)
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
        return if (getConnectedDirection(state).opposite == directionToNeighbour && !state.canSurvive(level, pos)) {
            Blocks.AIR.defaultBlockState()
        } else
            super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return canAttach(level, pos, getConnectedDirection(state).opposite)
    }

    fun getConnectedDirection(state: BlockState): Direction {
        return when (state.getValue(FACE)) {
            AttachFace.CEILING -> Direction.DOWN
            AttachFace.FLOOR -> Direction.UP
            else -> state.getValue(FACING)
        }
    }

    private fun canAttach(level: LevelReader, pos: BlockPos, supportDir: Direction): Boolean {
        val supportPos = pos.relative(supportDir)
        return canSupportCenter(level, supportPos, supportDir.opposite)
    }

    override fun hasAnalogOutputSignal(state: BlockState): Boolean = state.getValue(LEVEL) > 0
    override fun getAnalogOutputSignal(state: BlockState, level: Level, pos: BlockPos, direction: Direction): Int {
        return state.getValue(LEVEL)
    }

    override fun getCloneItemStack(level: LevelReader, pos: BlockPos, state: BlockState, includeData: Boolean): ItemStack {
        return (level.getBlockEntity(pos) as? SunBatteryBlockEntity)?.theItem?:
        super.getCloneItemStack(level, pos, state, includeData)
    }

    override fun codec(): MapCodec<out SunBatteryBlock> { return CODEC }
}