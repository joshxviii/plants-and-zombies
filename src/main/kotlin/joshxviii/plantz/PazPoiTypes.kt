package joshxviii.plantz

import com.google.common.collect.ImmutableSet
import joshxviii.plantz.PazBlocks.PLANTZ_FLAG_BLOCK
import joshxviii.plantz.PazBlocks.SUN_BATTERY_BLOCK
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.ai.village.poi.PoiTypes
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

object PazPoiTypes {

    @JvmField val SUN_BATTERY = createKey("sun_battery")
    @JvmField val PLANTZ_FLAG = createKey("plantz_flag")

    private fun createKey(name: String): ResourceKey<PoiType> {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, pazResource(name))
    }

    private fun getBlockStates(block: Block): MutableSet<BlockState> {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates())
    }

    fun initialize() {
        val r = BuiltInRegistries.POINT_OF_INTEREST_TYPE
        PoiTypes.register(r, SUN_BATTERY, getBlockStates(SUN_BATTERY_BLOCK), 1, 1)
        PoiTypes.register(r, PLANTZ_FLAG, getBlockStates(PLANTZ_FLAG_BLOCK), 1, 1)
    }
}