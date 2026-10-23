package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazWorldGen
import joshxviii.plantz.block.entity.TimeMachineBlockEntity
import joshxviii.plantz.block.entity.getTimeMachineManager
import joshxviii.plantz.block.entity.TimePortalBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.InsideBlockEffectApplier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.portal.TeleportTransition
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import kotlin.math.max

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

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity, effectApplier: InsideBlockEffectApplier, isPrecise: Boolean) {
        if (entity.canUsePortal(false)) entity.setAsInsidePortal(this, pos)
    }

    override fun getPortalDestination(
        currentLevel: ServerLevel,
        entity: Entity,
        portalEntryPos: BlockPos
    ): TeleportTransition? {
        val source = currentLevel.getBlockEntity(portalEntryPos.below(2)) as? TimeMachineBlockEntity ?: return null
        if (source.blockState.getValue(TimeMachineBlock.STATE) != TimeMachineState.ACTIVE) return null
        val newDimension = if (PazWorldGen.isTimeDimension(currentLevel.dimension())) Level.OVERWORLD else PazWorldGen.TIME_SPACE
        val newLevel = currentLevel.server.getLevel(newDimension) ?: return null
        if (!currentLevel.isAllowedToEnterPortal(newLevel) || !entity.canTeleport(currentLevel, newLevel)) return null
        if (!currentLevel.getTimeMachineManager().createDestination(source, newLevel)) return null
        return TeleportTransition(
            newLevel, entity.position(), entity.deltaMovement, entity.yRot, entity.xRot,
            TeleportTransition.PLACE_PORTAL_TICKET
        )
    }

    override fun getPortalTransitionTime(level: ServerLevel, entity: Entity): Int {
        return if (entity is Player) max(0, level.gameRules.get( if (entity.hasInfiniteMaterials()) GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY else GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY))
        else 0
    }

    override fun getLocalTransition(): Portal.Transition = Portal.Transition.NONE

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
        val machine = level.getBlockState(pos.below(2))
        if (!machine.`is`(PazBlocks.TIME_MACHINE) || machine.getValue(TimeMachineBlock.STATE) != TimeMachineState.ACTIVE) {
            return Blocks.AIR.defaultBlockState()
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return super.canSurvive(state, level, pos)
    }

    override fun codec(): MapCodec<out TimePortalBlock> = CODEC

}
