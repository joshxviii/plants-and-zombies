package joshxviii.plantz.raid

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazLootTables
import joshxviii.plantz.entity.zombie.BrownCoat
import joshxviii.plantz.entity.zombie.BrownCoatVariant
import joshxviii.plantz.entity.zombie.Gargantuar
import joshxviii.plantz.entity.zombie.GargantuarVariant
import joshxviii.plantz.entity.zombie.Imp
import joshxviii.plantz.entity.zombie.ImpVariant
import joshxviii.plantz.entity.zombie.SuperBrainz
import joshxviii.plantz.entity.zombie.SuperBrainzVariant
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.storage.loot.LootTable
import java.util.function.IntFunction
import kotlin.collections.listOf

enum class WaveType(
    private val minWave: Int,
    private val maxWave: Int,
    private val creditsRequired: Boolean,
    private val weightFn: (ZombieRaid, Boolean) -> Float,
    private val spawnFn: (ZombieRaid, Boolean) -> List<WaveSpawnEntry>,
    val lootTableFn: (Int, Boolean) ->  ResourceKey<LootTable> = { _, _ -> PazLootTables.MAIL_REWARD_DEFAULT_EASY }
): StringRepresentable {
    DEFAULT(
        minWave = 0,
        maxWave = 99,
        creditsRequired = false,
        weightFn = { _, credits -> if (credits) 0.5f else 1f },
        spawnFn = { raid, credits ->
            val wave = raid.wavesSpawned
            val omenLevel = raid.zombieRaidOmenLevel
            val brownCoatCount = 4 + wave * if (credits) 6 else 4 + (omenLevel * 3)
            val newspaperCount = 1 + wave + (omenLevel / 2)
            val diggerCount = if (wave > 2) 1 + wave / 2 + (omenLevel / 2) else 0
            val impCount = if (wave > 4) 1 + (wave - 5) + (omenLevel / 2) else 0
            val allStarCount = if (wave > 4) 1 + (wave - 5) * if (credits) 2 else 1 + (omenLevel / 2) else 0
            val discoCount = if (wave > 5) 1 + (wave - 6) / 2 + (omenLevel / 2) else 0
            val gargantuarCount = if (wave > 8) 1 + (wave - 9) else 0
            val engineerCount = if (credits && wave > 4) 1 + (wave - 5) + raid.wavesSpawned / 8 else 0
            val soldierCount = if (credits && wave > 7) 1 + (wave - 8) + raid.wavesSpawned / 6 else 0
            listOf(
                WaveSpawnEntry(PazEntities.BROWN_COAT, brownCoatCount)
                , WaveSpawnEntry(PazEntities.NEWSPAPER_ZOMBIE, newspaperCount)
                , WaveSpawnEntry(PazEntities.DIGGER_ZOMBIE, diggerCount)
                , WaveSpawnEntry(PazEntities.IMP, impCount)
                , WaveSpawnEntry(PazEntities.ALL_STAR, allStarCount)
                , WaveSpawnEntry(PazEntities.DISCO_ZOMBIE, discoCount)
                , WaveSpawnEntry(PazEntities.GARGANTUAR, gargantuarCount)
                , WaveSpawnEntry(PazEntities.ENGINEER_ZOMBIE, engineerCount)
                , WaveSpawnEntry(PazEntities.SOLDIER_ZOMBIE, soldierCount)
            )
        },
        lootTableFn = { waveNum, _ -> if (waveNum > 7) PazLootTables.MAIL_REWARD_DEFAULT_HARD else PazLootTables.MAIL_REWARD_DEFAULT_EASY }
    ),
    BUCKET_BRIGADE(
        minWave = 1,
        maxWave = 3,
        creditsRequired = false,
        weightFn = { raid, _ ->
            0.11f + (raid.zombieRaidOmenLevel * 0.02f)
        },
        spawnFn = { raid, credits ->
            val brownCoatCount = 5 + raid.wavesSpawned * if (credits) 4 else 2 + (raid.zombieRaidOmenLevel * 2)
            val newspaperZombie = 1 + raid.wavesSpawned + (raid.zombieRaidOmenLevel / 2)
            listOf(
                WaveSpawnEntry(PazEntities.BROWN_COAT, brownCoatCount.coerceAtLeast(3), ::spawnBucketBrigade)
                , WaveSpawnEntry(PazEntities.NEWSPAPER_ZOMBIE, newspaperZombie.coerceAtLeast(1), ::spawnBucketBrigade)
            )
        },
        lootTableFn = { _, _ -> PazLootTables.MAIL_REWARD_BUCKET }
    ),
    HALFTIME_SHOWDOWN(
        minWave = 2,
        maxWave = 5,
        creditsRequired = false,
        weightFn = { raid, credits ->
            0.11f + (raid.zombieRaidOmenLevel * 0.04f) + if (credits) 0.04f else 0f
        },
        spawnFn = { raid, credits ->
            val allStarCount = 3 + raid.wavesSpawned * if (credits) 2 else 1 + (raid.zombieRaidOmenLevel / 2)
            val impCount = 4 + raid.wavesSpawned / if (credits) 1 else 2 + (raid.zombieRaidOmenLevel / 2)
            listOf(
                WaveSpawnEntry(PazEntities.ALL_STAR, allStarCount.coerceAtLeast(2))
                , WaveSpawnEntry(PazEntities.IMP, impCount.coerceAtLeast(1), ::spawnHalftimeShowdown)
            )
        },
        lootTableFn = { _, _ -> PazLootTables.MAIL_REWARD_HALFTIME}
    ),
    WINTER_WONDERLAND(
        minWave = 4,
        maxWave = 9,
        creditsRequired = false,
        weightFn = { raid, credits ->
            0.12f + (raid.zombieRaidOmenLevel * 0.04f) + if (credits) 0.05f else 0f
        },
        spawnFn = { raid, credits ->
            val browncoatCount = 5 + raid.wavesSpawned * if (credits) 4 else 2 + (raid.zombieRaidOmenLevel / 2)
            val impCount = 2 + raid.wavesSpawned * if (credits) 2 else 1 + (raid.zombieRaidOmenLevel / 2)
            val yetiCount = 1 + raid.wavesSpawned / 3 + (raid.zombieRaidOmenLevel / 3)
            listOf(
                WaveSpawnEntry(PazEntities.BROWN_COAT, browncoatCount.coerceAtLeast(5), ::spawnWinterWonderland)
                , WaveSpawnEntry(PazEntities.IMP, impCount.coerceAtLeast(2), ::spawnWinterWonderland)
                , WaveSpawnEntry(PazEntities.ZOMBIE_YETI, yetiCount.coerceAtLeast(2))
            )
        },
        lootTableFn = { _, _ -> PazLootTables.MAIL_REWARD_WINTER}
    ),
    PIRATE_INVASION(
        minWave = 5,
        maxWave = 14,
        creditsRequired = false,
        weightFn = { raid, credits ->
            0.19f + (raid.zombieRaidOmenLevel * 0.09f) + if (credits) 0.15f else 0f
        },
        spawnFn = { raid, credits ->
            val browncoatCount = 6 + raid.wavesSpawned * if (credits) 4 else 2 + (raid.zombieRaidOmenLevel / 2)
            val impCount = if (credits) 7 else 3 + raid.wavesSpawned / 2 + (raid.zombieRaidOmenLevel / 2)
            val gargantuarCount = if (credits) 1 else 0 + raid.wavesSpawned / 4 + (raid.zombieRaidOmenLevel / 4)
            val captainCount = if (credits) 3 + raid.wavesSpawned / 3 + (raid.zombieRaidOmenLevel / 3) else 0
            listOf(
                WaveSpawnEntry(PazEntities.BROWN_COAT, browncoatCount.coerceAtLeast(5), ::spawnPirateInvasion)
                , WaveSpawnEntry(PazEntities.IMP, impCount.coerceAtLeast(2), ::spawnPirateInvasion)
                , WaveSpawnEntry(PazEntities.PIRATE_CAPTAIN, captainCount)
                , WaveSpawnEntry(PazEntities.GARGANTUAR, gargantuarCount, ::spawnPirateInvasion)
            )
        },
        lootTableFn = { _, _ -> PazLootTables.MAIL_REWARD_PIRATE}
    ),
    ROBO_ARMY(
        minWave = 6,
        maxWave = 15,
        creditsRequired = true,
        weightFn = { raid, credits ->
            if (!credits) 0f else 0.2f + (raid.zombieRaidOmenLevel * 0.05f)
        },
        spawnFn = { raid, _ ->
            val roboCount = 3 + raid.wavesSpawned / 4
            val engineerCount = 3 + raid.wavesSpawned / 2
            val soldierCount = 8 + raid.wavesSpawned / 3
            listOf(
                WaveSpawnEntry(PazEntities.ROBO_ZOMBIE, roboCount.coerceAtLeast(1))
                , WaveSpawnEntry(PazEntities.ENGINEER_ZOMBIE, engineerCount.coerceAtLeast(2))
                , WaveSpawnEntry(PazEntities.SOLDIER_ZOMBIE, soldierCount.coerceAtLeast(2))
            )
        },
        lootTableFn = { _, _ -> PazLootTables.MAIL_REWARD_ARMY}
    ),
    LEAGUE_OF_AWESOMENESS(
        minWave = 8,
        maxWave = 20,
        creditsRequired = true,
        weightFn = { raid, credits ->
            if (!credits) 0f else 0.2f + (raid.zombieRaidOmenLevel * 0.05f)
        },
        spawnFn = { raid, _ ->
            val browncoatCount = 4 + raid.wavesSpawned * 3 + (raid.zombieRaidOmenLevel / 2)
            val superCount = 1 + raid.wavesSpawned / 4 + (raid.zombieRaidOmenLevel / 3)
            listOf(
                WaveSpawnEntry(PazEntities.BROWN_COAT, browncoatCount.coerceAtLeast(5), ::spawnLeagueOfAwesomeness)
                , WaveSpawnEntry(PazEntities.SUPER_BRAINZ, superCount.coerceAtLeast(1), ::spawnLeagueOfAwesomeness)
            )
        },
        lootTableFn = { _, _ -> PazLootTables.MAIL_REWARD_LEAGUE}
    );


    override fun getSerializedName(): String = name.lowercase()

    companion object {
        val CODEC: Codec<WaveType> = StringRepresentable.fromEnum(WaveType::values)
        private val BY_ID: IntFunction<WaveType> = ByIdMap.continuous(WaveType::ordinal, WaveType.entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, WaveType> = ByteBufCodecs.idMapper<WaveType>(BY_ID, WaveType::ordinal)

        fun spawnBucketBrigade(zombie: Zombie) {
            zombie.setItemSlot(EquipmentSlot.HEAD, Items.BUCKET.defaultInstance)
            zombie.setDropChance(EquipmentSlot.HEAD, 0.0f)
            if (zombie.random.nextFloat() < 0.7f) {
                zombie.setItemSlot(EquipmentSlot.CHEST, Items.IRON_CHESTPLATE.defaultInstance)
                zombie.setDropChance(EquipmentSlot.CHEST, 0.0f)
            }
            if (zombie.random.nextFloat() < 0.7f) {
                zombie.setItemSlot(EquipmentSlot.LEGS, Items.IRON_LEGGINGS.defaultInstance)
                zombie.setDropChance(EquipmentSlot.LEGS, 0.0f)
            }
            if (zombie.random.nextFloat() < 0.7f) {
                zombie.setItemSlot(EquipmentSlot.FEET, Items.IRON_BOOTS.defaultInstance)
                zombie.setDropChance(EquipmentSlot.FEET, 0.0f)
            }
        }

        fun spawnHalftimeShowdown(zombie: Zombie) {
            zombie.setItemSlot(EquipmentSlot.HEAD, PazItems.FOOTBALL_HELMET.defaultInstance)
            zombie.setDropChance(EquipmentSlot.HEAD, 0.0f)
        }

        fun spawnWinterWonderland(zombie: Zombie) {
            if (zombie is Imp) zombie.variant = ImpVariant.YETI
            if (zombie is BrownCoat) {
                zombie.variant = BrownCoatVariant.SNOW
                val boots = Items.LEATHER_BOOTS.defaultInstance
                boots.set(DataComponents.DYED_COLOR, DyedItemColor(0xFFFFFF))
                if (zombie.random.nextFloat() < 0.7f) {
                    val frostWalker = zombie.level()
                        .registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.FROST_WALKER)
                    boots.enchant(frostWalker, 2)
                    zombie.setItemSlot(EquipmentSlot.FEET, boots)
                    zombie.setDropChance(EquipmentSlot.FEET, 0.0f)
                }
                if (zombie.random.nextFloat() < 0.4f) {
                    zombie.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_SHOVEL.defaultInstance)
                    zombie.setDropChance(EquipmentSlot.MAINHAND, 0.0f)
                }
            }
        }

        fun spawnPirateInvasion(zombie: Zombie) {
            if (zombie is Gargantuar) zombie.variant = GargantuarVariant.PIRATE
            if (zombie is Imp) zombie.variant = ImpVariant.PIRATE
            if (zombie is BrownCoat) {
                zombie.variant = BrownCoatVariant.BUCCANEER
                if (zombie.random.nextFloat() < 0.4f) {
                    zombie.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_SWORD.defaultInstance)
                    zombie.setDropChance(EquipmentSlot.MAINHAND, 0.0f)
                }
            }
        }

        fun spawnLeagueOfAwesomeness(zombie: Zombie) {
            if (zombie is SuperBrainz) zombie.variant = SuperBrainzVariant.pickRandomVariant()
            if (zombie is BrownCoat) { }
        }
    }

    fun isAvailable(raid: ZombieRaid, creditsUnlocked: Boolean): Boolean {
        return raid.wavesSpawned in minWave..maxWave && (!creditsRequired || creditsUnlocked)
    }

    fun weight(raid: ZombieRaid, creditsUnlocked: Boolean): Float = weightFn(raid, creditsUnlocked)

    fun spawnEntries(raid: ZombieRaid, creditsUnlocked: Boolean): List<WaveSpawnEntry> = spawnFn(raid, creditsUnlocked)

    fun popupMessage(): Component {
        return Component.translatable("event.plantz.zombie_raid.special_wave.${name.lowercase()}")
    }
}

data class WaveSpawnEntry(
    val entityType: EntityType<out Zombie>,
    val count: Int = 1,
    val configure: (Zombie) -> Unit = {},
)