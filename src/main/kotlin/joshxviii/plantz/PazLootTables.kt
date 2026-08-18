package joshxviii.plantz

import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.trading.VillagerTrade
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.NestedLootTable
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator

object PazLootTables {
    // seed packets
    @JvmField
    val SUN = registerLootTable("sun")

    @JvmField
    val GRAVESTONE_TREASURE = registerLootTable("chests/gravestone_treasure")

    @JvmField val DIFFICULTY_EASY_MAIL_REWARD = registerLootTable("chests/raid/difficulty_easy_mail_reward")
    @JvmField val DIFFICULTY_HARD_MAIL_REWARD = registerLootTable("chests/raid/difficulty_hard_mail_reward")
    @JvmField val ZOMBOSS_MAIL_REWARD = registerLootTable("chests/raid/zomboss_mail_reward")
    @JvmField val BUCKET_MAIL_REWARD = registerLootTable("chests/raid/bucket_mail_reward")
    @JvmField val HALFTIME_MAIL_REWARD = registerLootTable("chests/raid/halftime_mail_reward")
    @JvmField val WINTER_MAIL_REWARD = registerLootTable("chests/raid/winter_mail_reward")
    @JvmField val PIRATE_MAIL_REWARD = registerLootTable("chests/raid/pirate_mail_reward")
    @JvmField val ARMY_MAIL_REWARD = registerLootTable("chests/raid/army_mail_reward")
    @JvmField val LEAGUE_MAIL_REWARD = registerLootTable("chests/raid/league_mail_reward")

    private fun registerLootTable(
        name: String
    ) : ResourceKey<LootTable> {
        return ResourceKey.create(Registries.LOOT_TABLE, pazResource(name) )
    }

    private fun registerVillagerTrade(
        name: String
    ) : ResourceKey<VillagerTrade> {
        return ResourceKey.create(Registries.VILLAGER_TRADE, pazResource(name) )
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
        BuiltInLootTables.FISHING_FISH,
        BuiltInLootTables.FISHING_TREASURE,
        BuiltInLootTables.BASTION_TREASURE,
        BuiltInLootTables.BASTION_OTHER,
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