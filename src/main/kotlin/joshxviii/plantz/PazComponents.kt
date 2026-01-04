package joshxviii.plantz

import joshxviii.plantz.item.component.SeedPacket
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey

object PazComponents {
    @JvmField
    val SEED_PACKET: DataComponentType<SeedPacket> = registerComponent("seed_packet")

    private fun registerComponent(name: String): DataComponentType<SeedPacket> {
        val key = ResourceKey.create(Registries.DATA_COMPONENT_TYPE, pazResource(name))
        val type = DataComponentType.builder<SeedPacket>()
            .persistent(SeedPacket.CODEC)
            .networkSynchronized(SeedPacket.STREAM_CODEC)
            .cacheEncoding()
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, key, type.build())
    }

    fun initialize() {}
}