package joshxviii.plantz

import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.fabricmc.fabric.api.loot.v3.LootTableSource
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.projectile.FishingHook
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries

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
    
}