package joshxviii.plantz

import joshxviii.plantz.item.component.BlocksHeadDamage
import joshxviii.plantz.item.component.SeedPacket
import joshxviii.plantz.item.component.SunCost
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import java.util.function.UnaryOperator

object PazComponents {
    @JvmField
    val SEED_PACKET: DataComponentType<SeedPacket> = register(
        "seed_packet"
    ) { b: DataComponentType.Builder<SeedPacket> ->
        DataComponentType.builder<SeedPacket>()
        .persistent(SeedPacket.CODEC)
        .networkSynchronized(SeedPacket.STREAM_CODEC)
        .cacheEncoding()
    }

    @JvmField
    val SUN_COST: DataComponentType<SunCost> = register(
        "sun_cost"
    ) { b: DataComponentType.Builder<SunCost> ->
        DataComponentType.builder<SunCost>()
            .persistent(SunCost.CODEC)
            .networkSynchronized(SunCost.STREAM_CODEC)
            .cacheEncoding()
    }


    @JvmField
    val BLOCKS_HEAD_DAMAGE: DataComponentType<BlocksHeadDamage> = register(
        "blocks_head_damage"
    ) { b: DataComponentType.Builder<BlocksHeadDamage> ->
        DataComponentType.builder<BlocksHeadDamage>()
            .persistent(BlocksHeadDamage.CODEC)
            .networkSynchronized(BlocksHeadDamage.STREAM_CODEC)
            .cacheEncoding()
    }

    private fun <T : Any> register(name: String, builder: UnaryOperator<DataComponentType.Builder<T>>): DataComponentType<T> {
        val key = ResourceKey.create(Registries.DATA_COMPONENT_TYPE, pazResource(name))
        return Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            key,
            builder.apply(DataComponentType.builder()).build()
        )
    }

    fun initialize() {}
}