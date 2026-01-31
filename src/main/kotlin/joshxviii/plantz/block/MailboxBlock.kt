package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.entity.MailboxBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
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
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class MailboxBlock(
    properties: Properties,
    val color: DyeColor = DyeColor.WHITE
) : BaseEntityBlock(properties) {
    companion object {
        val CODEC: MapCodec<MailboxBlock> = RecordCodecBuilder.mapCodec { it.group(DyeColor.CODEC.fieldOf("color").forGetter { b -> b.color }, propertiesCodec()).apply(it) { color, properties -> MailboxBlock(properties, color) } }

        val STATE: EnumProperty<MailboxState> = EnumProperty.create<MailboxState>("mailbox_state", MailboxState::class.java)
        val FACING: EnumProperty<Direction> = HorizontalDirectionalBlock.FACING
        val FACE: EnumProperty<AttachFace> = BlockStateProperties.ATTACH_FACE
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED

        val SHAPE: VoxelShape = column(8.0, 12.0, 0.0, 8.0)
        var SHAPES: MutableMap<Direction.Axis, VoxelShape> = Shapes.rotateHorizontalAxis(SHAPE)
        var SHAPES_WALL: MutableMap<Direction, VoxelShape> = Shapes.rotateHorizontal(SHAPE.move(0.0,0.25,0.125))
    }
    override fun codec(): MapCodec<out MailboxBlock> { return CODEC }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.FLOOR).setValue(WATERLOGGED, false).setValue(STATE, MailboxState.INACTIVE))
    }

    override fun newBlockEntity(worldPosition: BlockPos, blockState: BlockState): BlockEntity {
        return MailboxBlockEntity(worldPosition, blockState, color)
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
        val mailbox = getMailboxEntity(level, pos)?: return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult)
        return if (mailbox.tryToGetMail()) InteractionResult.SUCCESS
        else InteractionResult.TRY_WITH_EMPTY_HAND
    }

    fun getMailboxEntity(level : Level, pos: BlockPos): MailboxBlockEntity? {
        val mailbox = (level as? ServerLevel)?.getBlockEntity(pos) as? MailboxBlockEntity
        return mailbox
    }

    override fun <T : BlockEntity> getTicker(level: Level, blockState: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return if (type == PazBlocks.MAILBOX_ENTITY) {
            BlockEntityTicker { lvl, pos, st, be ->
                (be as MailboxBlockEntity).tick(lvl, pos, st)
            }
        } else null
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide) {
            (level.getBlockEntity(pos) as? MailboxBlockEntity).let {
                player.openMenu(it)
            }
        }
        return InteractionResult.SUCCESS
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState = state.setValue(FACING, rotation.rotate(state.getValue(FACING)))

    public override fun mirror(state: BlockState, mirror: Mirror): BlockState = state.rotate(mirror.getRotation(state.getValue(FACING)))

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, FACE, STATE, WATERLOGGED)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val facing = context.horizontalDirection.opposite
        for (direction in context.getNearestLookingDirections()) {
            var state: BlockState
            if (direction.axis === Direction.Axis.Y) {
                state = defaultBlockState().setValue(FACE, AttachFace.FLOOR).setValue(FACING, facing)
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

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val facing = state.getValue(FACING)
        return if (state.getValue(FACE) == AttachFace.WALL)
            SHAPES_WALL[facing] as VoxelShape
        else
            SHAPES[facing.axis] as VoxelShape
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


    override fun hasAnalogOutputSignal(state: BlockState): Boolean = state.getValue(STATE) == MailboxState.HAS_MAIL
    override fun getAnalogOutputSignal(state: BlockState, level: Level, pos: BlockPos, direction: Direction): Int {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos))
    }

    override fun getCloneItemStack(level: LevelReader, pos: BlockPos, state: BlockState, includeData: Boolean): ItemStack {
        return PazBlocks.mailboxByColor[color]?.asItem()?.defaultInstance?:
        super.getCloneItemStack(level, pos, state, includeData)
    }
}