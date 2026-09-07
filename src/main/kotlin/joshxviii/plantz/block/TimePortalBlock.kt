package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.entity.TimeMachineBlockEntity
import joshxviii.plantz.block.entity.TimePortalBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.NetherPortalBlock
import net.minecraft.world.level.block.Portal
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.portal.TeleportTransition
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class TimePortalBlock(properties: Properties) : BaseEntityBlock(properties), SimpleWaterloggedBlock, Portal {

    companion object {
        val CODEC: MapCodec<TimePortalBlock> = simpleCodec(::TimePortalBlock)
        val SHAPES = Shapes.rotateAll(column(16.0, 2.0, 0.0, 16.0))

        val FACING: EnumProperty<Direction> = DirectionalBlock.FACING
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (type == PazBlocks.TIME_PORTAL_ENTITY)
            BlockEntityTicker { level, pos, state, blockEntity -> TimePortalBlockEntity.tick(level, pos, state, blockEntity as TimePortalBlockEntity) }
        else
            null
    }

    override fun newBlockEntity(worldPosition: BlockPos, blockState: BlockState): BlockEntity = TimePortalBlockEntity(worldPosition, blockState)

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(FACING, WATERLOGGED)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState()
            .setValue(FACING, context.horizontalDirection)
            .setValue(WATERLOGGED, false)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPES[state.getValue(FACING)] as VoxelShape
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(TimeMachineBlock.WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.INVISIBLE

    override fun getPortalDestination(
        currentLevel: ServerLevel,
        entity: Entity,
        portalEntryPos: BlockPos
    ): TeleportTransition? {
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
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
        (level.getBlockEntity(pos.below().below()) as? TimeMachineBlockEntity)?.updatePortal()?: return Blocks.AIR.defaultBlockState()
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return super.canSurvive(state, level, pos)
    }

    override fun codec(): MapCodec<out TimePortalBlock> = CODEC

}
