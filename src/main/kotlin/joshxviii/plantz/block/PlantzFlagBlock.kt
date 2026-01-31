package joshxviii.plantz.block

import com.mojang.serialization.MapCodec
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour.Properties

class PlantzFlagBlock(properties: Properties) : FlagBlock(properties) {
    companion object {
        val CODEC: MapCodec<PlantzFlagBlock> = simpleCodec(::PlantzFlagBlock)
    }
    override fun codec(): MapCodec<out PlantzFlagBlock> { return CODEC }
}