package joshxviii.plantz

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.LootTable

object PazLootTables {
    // seed packets
    @JvmField
    val SEED_PACKET = registerLootTable("seed_packets")
    @JvmField
    val SUN = registerLootTable("sun")
    @JvmField
    val BRAINZ_BANNER = registerLootTable("brainz_banner")

    private fun registerLootTable(
        name: String
    ) : ResourceKey<LootTable> {
        return ResourceKey.create(Registries.LOOT_TABLE, pazResource(name) )
    }

    fun initialize() {}
}