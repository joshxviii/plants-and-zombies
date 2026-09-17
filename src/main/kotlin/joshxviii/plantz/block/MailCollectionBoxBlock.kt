package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.entity.MailCollectionBoxEntity
import joshxviii.plantz.block.entity.MailboxManager
import joshxviii.plantz.inventory.MailCollectionBoxMenu
import joshxviii.plantz.networking.MailboxListResponsePayload
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.*
import net.minecraft.world.level.block.*
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
import net.minecraft.world.level.redstone.Orientation
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class MailCollectionBoxBlock(
    properties: Properties,
) : BaseEntityBlock(properties) {
    companion object {
        val CODEC: MapCodec<MailCollectionBoxBlock> = RecordCodecBuilder.mapCodec { it.group(propertiesCodec()).apply(it) { properties -> MailCollectionBoxBlock(properties) } }

        val STATE: EnumProperty<CollectionBoxState> = EnumProperty.create("state", CollectionBoxState::class.java)
        val FACING: EnumProperty<Direction> = HorizontalDirectionalBlock.FACING
        val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED

        val SHAPE: VoxelShape = column(14.0, 14.0, 0.0, 16.0)
    }
    override fun codec(): MapCodec<out MailCollectionBoxBlock> { return CODEC }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(STATE, CollectionBoxState.CLOSED))
    }

    override fun newBlockEntity(worldPosition: BlockPos, blockState: BlockState): BlockEntity {
        return MailCollectionBoxEntity(worldPosition, blockState)
    }

    override fun <T : BlockEntity> getTicker(level: Level, blockState: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return if (type == PazBlocks.MAIL_COLLECTION_BOX_ENTITY) {
            BlockEntityTicker { level, pos, state, blockEntity ->
                MailCollectionBoxEntity.tick(level, pos, state, blockEntity as MailCollectionBoxEntity)
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
            (level.getBlockEntity(pos) as? MailCollectionBoxEntity).let { currentMailbox ->
                val mailboxes = MailboxManager.getMailboxesInLevel(level).sortedBy { it.blockPos.distSqr(pos) }
                player.openMenu(currentMailbox)
                (player.containerMenu as? MailCollectionBoxMenu)?.availableMailboxes = mailboxes
                ServerPlayNetworking.send(player as ServerPlayer, MailboxListResponsePayload(level.dimension(), mailboxes))
            }
        }
        return InteractionResult.SUCCESS
    }

    override fun destroy(level: LevelAccessor, pos: BlockPos, state: BlockState) {
        super.destroy(level, pos, state)
        MailboxManager.unregisterMailbox(level as Level, pos)
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState = state.setValue(FACING, rotation.rotate(state.getValue(FACING)))

    public override fun mirror(state: BlockState, mirror: Mirror): BlockState = state.rotate(mirror.getRotation(state.getValue(FACING)))

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, STATE, WATERLOGGED)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val replacedFluidState = context.level.getFluidState(context.clickedPos)
        return defaultBlockState()
            .setValue(FACING, context.horizontalDirection.opposite)
            .setValue(WATERLOGGED, replacedFluidState.`is`(Fluids.WATER))
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
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
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        orientation: Orientation?,
        movedByPiston: Boolean
    ) {
        if (level.isClientSide) return
        val powered = level.hasNeighborSignal(pos)
        (level.getBlockEntity(pos) as? MailCollectionBoxEntity)?.let {
            if (powered && !it.wasPowered) it.trySendMail()
            it.wasPowered = powered
        }
    }

    override fun hasAnalogOutputSignal(state: BlockState): Boolean = true
    override fun getAnalogOutputSignal(state: BlockState, level: Level, pos: BlockPos, direction: Direction): Int {
        val blockEntity = level.getBlockEntity(pos) as? MailCollectionBoxEntity ?: return 0
        val hasSelected = blockEntity.selectedMailbox != null
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(blockEntity)
    }
}
