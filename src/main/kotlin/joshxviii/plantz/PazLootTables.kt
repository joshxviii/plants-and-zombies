package joshxviii.plantz

import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.NestedLootTable
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator

object PazLootTables {
    // seed packets
    @JvmField
    val SUN = registerLootTable("sun")

    private fun registerLootTable(
        name: String
    ) : ResourceKey<LootTable> {
        return ResourceKey.create(Registries.LOOT_TABLE, pazResource(name) )
    }

    fun initialize() {
        LootTableEvents.MODIFY.register { key, builder, source, provider ->
            LootInjector.attemptInjection(key.identifier(), builder::withPool)
        }
    }

}

object LootInjector {
    private const val PREFIX = "injection/"

    private val villageHouseBuiltInLootTables = hashSetOf(
        BuiltInLootTables.VILLAGE_DESERT_HOUSE,
        BuiltInLootTables.VILLAGE_PLAINS_HOUSE,
        BuiltInLootTables.VILLAGE_SAVANNA_HOUSE,
        BuiltInLootTables.VILLAGE_SNOWY_HOUSE,
        BuiltInLootTables.VILLAGE_TAIGA_HOUSE,
    )
    private val injections = hashSetOf(
        BuiltInLootTables.FISHING_TREASURE
    ).apply { addAll(villageHouseBuiltInLootTables) }

    private val injectionIds = injections.map {it.identifier()}.toSet()
    private val villageInjectionIds = villageHouseBuiltInLootTables.map { it.identifier() }.toSet()

    fun attemptInjection(key: Identifier, provider: (LootPool.Builder) -> Unit): Boolean {
        if (!this.injectionIds.contains(key)) {
            return false
        }
        val resulting = convertToPotentialInjected(key)
        PazMain.LOGGER.debug("{}: Injected {} to {}", this::class.simpleName, resulting, key)
        provider(this.injectLootPool(resulting))
        return true
    }

    private fun convertToPotentialInjected(key: Identifier): Identifier {
        return if (this.villageInjectionIds.contains(key))
            pazResource("${PREFIX}chests/village_house")
        else
            pazResource("${PREFIX}${key.path}")
    }

    private fun injectLootPool(resulting: Identifier): LootPool.Builder {
        return LootPool.lootPool()
            .add(
                NestedLootTable
                    .lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, resulting))
                    .setWeight(1)
            )
            .setBonusRolls(UniformGenerator.between(0F, 1F))
    }
}