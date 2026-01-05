package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Util
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class PlantPotBlock(properties: Properties) : HorizontalDirectionalBlock(properties) {
    companion object {
        val CODEC: MapCodec<PlantPotBlock> = simpleCodec(::PlantPotBlock)
        val SHAPE: VoxelShape = Util.make {
            Shapes.join(
                column(14.0, 0.0, 8.0),
                column(10.0, 6.0, 8.0),
                BooleanOp.ONLY_FIRST
            )
        }
        val FACING: EnumProperty<Direction> = HorizontalDirectionalBlock.FACING
    }

    init {
        this.registerDefaultState(
            this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        )
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun codec(): MapCodec<out PlantPotBlock> { return CODEC }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue<Direction, Direction>(FACING, rotation.rotate(state.getValue<Direction>(FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return this.defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }
}