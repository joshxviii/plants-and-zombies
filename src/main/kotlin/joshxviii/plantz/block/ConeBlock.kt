package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Util
import net.minecraft.world.item.component.FireworkExplosion
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
import net.minecraft.world.phys.shapes.CubeVoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class ConeBlock(properties: Properties) : HorizontalDirectionalBlock(properties) {
    companion object {
        val CODEC: MapCodec<ConeBlock> = simpleCodec(::ConeBlock)
        val SHAPE: VoxelShape = Util.make {
            Shapes.or(
                column(12.0, 0.0, 2.0),
                column(6.0, 2.0, 7.0),
                column(4.0, 7.0, 12.0),
            )
        }
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun codec(): MapCodec<out ConeBlock> { return CODEC }
}