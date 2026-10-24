package joshxviii.plantz.block

import joshxviii.plantz.block.entity.GardenGnomeBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.ByIdMap
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.util.Util
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class GardenGnomeBlock(
    properties: Properties,
    color: GardenGnomeColor = GardenGnomeColor.BLUE
) : BaseEntityBlock(properties), SimpleWaterloggedBlock {
    companion object {

        val SHAPE: VoxelShape = Util.make {
            column(6.0, 0.0, 12.0)
        }
        val POSE: EnumProperty<GardenGnomePose> = EnumProperty.create("pose", GardenGnomePose::class.java)
        val COLOR: EnumProperty<GardenGnomeColor> = EnumProperty.create("color", GardenGnomeColor::class.java)
        val FACING: EnumProperty<Direction> = HorizontalDirectionalBlock.FACING
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(POSE, GardenGnomePose.NONE).setValue(COLOR, color))
    }

    override fun skipRendering(state: BlockState, neighborState: BlockState, direction: Direction): Boolean {
        return super.skipRendering(state, neighborState, direction)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue<Direction, Direction>(FACING, rotation.rotate(state.getValue<Direction>(FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, WATERLOGGED, POSE, COLOR)
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
        updatePose(level, state, pos, player)
        return InteractionResult.SUCCESS
    }

    fun updatePose(level: Level, state: BlockState, pos: BlockPos, player: Player) {
        level.playSound(null, pos, SoundEvents.COPPER_GOLEM_BECOME_STATUE, SoundSource.BLOCKS)
        level.setBlock(pos, state.setValue(POSE, state.getValue(POSE).getNextPose()), 3)
        level.gameEvent(player as Entity, GameEvent.BLOCK_CHANGE, pos)
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

    override fun newBlockEntity(worldPosition: BlockPos, blockState: BlockState): BlockEntity = GardenGnomeBlockEntity(worldPosition, blockState)
}

enum class GardenGnomeColor: StringRepresentable {
    RED,
    GREEN,
    BLUE,
    YELLOW;
    override fun getSerializedName(): String = this.name.lowercase()
}

enum class GardenGnomePose: StringRepresentable {
    NONE,
    WAVE,
    RELAX,
    SIT,
    THINK;
    override fun getSerializedName(): String = this.name.lowercase()
    fun getNextPose(): GardenGnomePose = BY_ID.apply(this.ordinal + 1)

    companion object {
        val BY_ID = ByIdMap.continuous(Enum<GardenGnomePose>::ordinal, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val CODEC = StringRepresentable.fromEnum(::values);
    }

}