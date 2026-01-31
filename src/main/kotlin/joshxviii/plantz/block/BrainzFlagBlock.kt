package joshxviii.plantz.block

import com.mojang.serialization.MapCodec

class BrainzFlagBlock(properties: Properties) : FlagBlock(properties) {
    companion object {
        val CODEC: MapCodec<BrainzFlagBlock> = simpleCodec(::BrainzFlagBlock)
    }
    override fun codec(): MapCodec<out BrainzFlagBlock> { return CODEC }
}