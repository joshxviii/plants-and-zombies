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
import joshxviii.plantz.raid.ZombieRaid.WaveSpawnEntry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.minecraft.world.Difficulty
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.storage.loot.LootTable
import java.util.function.IntFunction

enum class WaveType(
    private val minWave: Int,
    private val maxWave: Int,
    private val creditsRequired: Boolean,
    private val weightFn: (ZombieRaid, Boolean) -> Float,
    private val spawnFn: (ZombieRaid, Boolean) -> List<WaveSpawnEntry>,
    val lootTableFn: (Int, Boolean) ->  ResourceKey<LootTable> = { _, _ -> PazLootTables.DIFFICULTY_EASY_MAIL_REWARD }
): StringRepresentable {
    DEFAULT(
        minWave = 0,
        maxWave = 99,
        creditsRequired = false,
        weightFn = { _, credits -> if (credits) 0.5f else 1f },
        spawnFn = { raid, credits -> ZombieRaiderType.VALUES
            .filter { it.isAvailable(credits) }
            .mapNotNull { type ->
                val count = getSpawnCountFor(raid, type, credits)
                if (count <= 0) null else WaveSpawnEntry(type, count)
            } },
        lootTableFn = { waveNum, _ -> if (waveNum > 6) PazLootTables.DIFFICULTY_HARD_MAIL_REWARD else PazLootTables.DIFFICULTY_EASY_MAIL_REWARD }
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
                WaveSpawnEntry(ZombieRaiderType.BROWN_COAT, brownCoatCount.coerceAtLeast(3), ::spawnBucketHeads),
                WaveSpawnEntry(ZombieRaiderType.NEWSPAPER_ZOMBIE, newspaperZombie.coerceAtLeast(1), ::spawnBucketHeads),
            )
        },
        lootTableFn = { _, _ -> PazLootTables.BUCKET_MAIL_REWARD }
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
                WaveSpawnEntry(ZombieRaiderType.ALL_STAR, allStarCount.coerceAtLeast(2)),
                WaveSpawnEntry(ZombieRaiderType.IMP, impCount.coerceAtLeast(1), ::spawnFootBallHelmets)
            )
        },
        lootTableFn = { _, _ -> PazLootTables.HALFTIME_MAIL_REWARD}
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
                WaveSpawnEntry(ZombieRaiderType.BROWN_COAT, browncoatCount.coerceAtLeast(5), ::spawnSnowZombies),
                WaveSpawnEntry(ZombieRaiderType.IMP, impCount.coerceAtLeast(2), ::spawnSnowZombies),
                WaveSpawnEntry(ZombieRaiderType.ZOMBIE_YETI, yetiCount.coerceAtLeast(2))
            )
        },
        lootTableFn = { _, _ -> PazLootTables.WINTER_MAIL_REWARD}
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
                WaveSpawnEntry(ZombieRaiderType.BROWN_COAT, browncoatCount.coerceAtLeast(5), ::spawnPirateZombies),
                WaveSpawnEntry(ZombieRaiderType.IMP, impCount.coerceAtLeast(2), ::spawnPirateZombies),
                WaveSpawnEntry(ZombieRaiderType.PIRATE_CAPTAIN, captainCount),
                WaveSpawnEntry(ZombieRaiderType.GARGANTUAR, gargantuarCount, ::spawnPirateZombies)
            )
        },
        lootTableFn = { _, _ -> PazLootTables.PIRATE_MAIL_REWARD}
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
                WaveSpawnEntry(ZombieRaiderType.ROBO_ZOMBIE, roboCount.coerceAtLeast(1)),
                WaveSpawnEntry(ZombieRaiderType.ENGINEER_ZOMBIE, engineerCount.coerceAtLeast(2)),
                WaveSpawnEntry(ZombieRaiderType.SOLDIER_ZOMBIE, soldierCount.coerceAtLeast(2))
            )
        },
        lootTableFn = { _, _ -> PazLootTables.ARMY_MAIL_REWARD}
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
                WaveSpawnEntry(ZombieRaiderType.BROWN_COAT, browncoatCount.coerceAtLeast(5), ::spawnLeagueZombies),
                WaveSpawnEntry(ZombieRaiderType.SUPER_BRAINZ, superCount.coerceAtLeast(1), ::spawnLeagueZombies)
            )
        },
        lootTableFn = { _, _ -> PazLootTables.LEAGUE_MAIL_REWARD}
    );


    override fun getSerializedName(): String = name.lowercase()

    companion object {
        val CODEC: Codec<WaveType> = StringRepresentable.fromEnum(WaveType::values)
        private val BY_ID: IntFunction<WaveType> = ByIdMap.continuous(WaveType::ordinal, WaveType.entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, WaveType> = ByteBufCodecs.idMapper<WaveType>(BY_ID, WaveType::ordinal)

        private fun getSpawnCountFor(raid: ZombieRaid, type: ZombieRaiderType, creditsUnlocked: Boolean): Int {
            val baseCount = type.spawnCountForWave(raid.wavesSpawned)
            if (baseCount <= 0) return 0
            val omenBonus = (raid.zombieRaidOmenLevel / 2).coerceAtLeast(0)
            val creditsBonus = if (creditsUnlocked) 1 else 0
            val lateWaveBonus = if (creditsUnlocked && raid.wavesSpawned >= 4) 1 else 0
            return baseCount + getPotentialBonusSpawns(raid, type,creditsUnlocked) + omenBonus + creditsBonus + lateWaveBonus
        }

        fun getPotentialBonusSpawns(
            raid: ZombieRaid,
            type: ZombieRaiderType,
            creditsUnlocked: Boolean,
        ): Int {
            val isEasy = raid.difficulty == Difficulty.EASY
            val isNormal = raid.difficulty == Difficulty.NORMAL
            val random = raid.random
            val wave = raid.wavesSpawned
            val bonusSpawns: Int
            when (type) {
                ZombieRaiderType.BROWN_COAT -> bonusSpawns =
                    if (isEasy) random.nextInt(2)
                    else if (isNormal) 3
                    else 4
                ZombieRaiderType.NEWSPAPER_ZOMBIE -> return 0
                ZombieRaiderType.DIGGER_ZOMBIE -> return 0
                ZombieRaiderType.DISCO_ZOMBIE -> {
                    if (isEasy || wave <= 2 || wave == 4) return 0
                    bonusSpawns = 1
                }
                ZombieRaiderType.ALL_STAR -> bonusSpawns = if (!isEasy && wave > 2) random.nextInt(2 + (raid.zombieRaidOmenLevel / 2)) else 0
                ZombieRaiderType.ZOMBIE_YETI -> bonusSpawns = if (!isEasy && wave > 3) 1 + (raid.zombieRaidOmenLevel / 5) else 0
                ZombieRaiderType.IMP -> bonusSpawns = if (wave > 2) random.nextInt(wave + (raid.zombieRaidOmenLevel / 2)) else 0
                ZombieRaiderType.GARGANTUAR -> bonusSpawns = if (creditsUnlocked && wave > 4) 1 else 0
                ZombieRaiderType.ENGINEER_ZOMBIE -> bonusSpawns = if (creditsUnlocked && wave > 1) 1 + (raid.zombieRaidOmenLevel / 8) else 0
                ZombieRaiderType.SOLDIER_ZOMBIE -> bonusSpawns = if (creditsUnlocked && wave > 2) 1 + random.nextInt(2) else 0
                ZombieRaiderType.ROBO_ZOMBIE -> bonusSpawns = if (creditsUnlocked && wave > 4) 1 else 0
                ZombieRaiderType.PIRATE_CAPTAIN -> bonusSpawns = if (creditsUnlocked && wave > 3) 1 else 0
                ZombieRaiderType.SUPER_BRAINZ -> bonusSpawns = if (creditsUnlocked && wave > 5) 1 else 0
                else -> return 0
            }

            return if (bonusSpawns > 0) random.nextInt(bonusSpawns + 1) else 0
        }

        fun spawnBucketHeads(zombie: Zombie) {
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

        fun spawnFootBallHelmets(zombie: Zombie) {
            zombie.setItemSlot(EquipmentSlot.HEAD, PazItems.FOOTBALL_HELMET.defaultInstance)
            zombie.setDropChance(EquipmentSlot.HEAD, 0.0f)
        }

        fun spawnSnowZombies(zombie: Zombie) {
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
        fun spawnPirateZombies(zombie: Zombie) {
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
        fun spawnLeagueZombies(zombie: Zombie) {
            if (zombie is SuperBrainz) zombie.variant = SuperBrainzVariant.pickRandomVariant()
            if (zombie is BrownCoat) {

            }
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

enum class ZombieRaiderType(
    val entityType: EntityType<out Zombie>,
    val spawnsPerWaveBeforeBonus: IntArray,
    private val requiresCredits: Boolean = false,
) {
    // default spawns per wave
    BROWN_COAT(PazEntities.BROWN_COAT,             intArrayOf(6,      8,      10,     12,     16,     20,     25,     25,     30,   25,     25,     25,     25,     25,     25,     25,     25,     25,     25,     25,)),
    NEWSPAPER_ZOMBIE(PazEntities.NEWSPAPER_ZOMBIE, intArrayOf(1,      1,      0,      2,      0,      1,      2,      1,      3,    2 ,     2 ,     2 ,     2 ,     2 ,     2 ,     2 ,     2 ,     2 ,     2 ,     2 ,)),
    DIGGER_ZOMBIE(PazEntities.DIGGER_ZOMBIE,       intArrayOf(0,      1,      1,      0,      2,      1,      1,      2,      3,    3 ,     3 ,     3 ,     3 ,     3 ,     3 ,     3 ,     3 ,     3 ,     3 ,     3 ,)),
    DISCO_ZOMBIE(PazEntities.DISCO_ZOMBIE,         intArrayOf(0,      0,      0,      1,      3,      3,      4,      2,      3,    5 ,     5 ,     5 ,     5 ,     5 ,     5 ,     5 ,     5 ,     5 ,     5 ,     5 ,)),
    ALL_STAR(PazEntities.ALL_STAR,                 intArrayOf(0,      0,      1,      0,      2,      4,      3,      3,      5,    5 ,     5 ,     5 ,     5 ,     5 ,     5 ,     5 ,     5 ,     5 ,     5 ,     5 ,)),
    ZOMBIE_YETI(PazEntities.ZOMBIE_YETI,           intArrayOf(0,      0,      0,      0,      1,      0,      2,      1,      2,    3 ,     3 ,     3 ,     3 ,     3 ,     3 ,     3 ,     3 ,     3 ,     3 ,     3 ,)),
    IMP(PazEntities.IMP,                           intArrayOf(0,      1,      1,      0,      2,      2,      5,      5,      4,    8 ,     8 ,     8 ,     8 ,     8 ,     8 ,     8 ,     8 ,     8 ,     8 ,     8 ,)),
    ENGINEER_ZOMBIE(PazEntities.ENGINEER_ZOMBIE,   intArrayOf(0,      0,      0,      0,      1,      0,      1,      1,      0,    1 ,     1 ,     1 ,     1 ,     1 ,     1 ,     1 ,     1 ,     1 ,     1 ,     1 ,), true),
    SOLDIER_ZOMBIE(PazEntities.SOLDIER_ZOMBIE,     intArrayOf(0,      0,      0,      0,      1,      0,      0,      1,      0,    1 ,     1 ,     1 ,     1 ,     1 ,     1 ,     1 ,     1 ,     1 ,     1 ,     1 ,), true),
    ROBO_ZOMBIE(PazEntities.ROBO_ZOMBIE,           intArrayOf(0,      0,      0,      0,      0,      0,      0,      0,      0,    0 ,     0 ,     0 ,     0 ,     0 ,     0 ,     0 ,     0 ,     0 ,     0 ,     0 ,), true),
    PIRATE_CAPTAIN(PazEntities.PIRATE_CAPTAIN,     intArrayOf(0,      0,      0,      0,      0,      0,      0,      0,      0,    0 ,     0 ,     0 ,     0 ,     0 ,     0 ,     0 ,     0 ,     0 ,     0 ,     0 ,), true),
    SUPER_BRAINZ(PazEntities.SUPER_BRAINZ,         intArrayOf(0,      0,      0,      0,      0,      0,      0,      0,      1,    0 ,     0 ,     2 ,     0 ,     0 ,     0 ,     1 ,     0 ,     0 ,     1 ,     0 ,), true),
    GARGANTUAR(PazEntities.GARGANTUAR,             intArrayOf(0,      0,      0,      0,      0,      0,      0,      0,      1,    0 ,     1 ,     1 ,     1 ,     2 ,     2 ,     2 ,     3 ,     3 ,     3 ,     4 ,));
    //                                                        1       2       3       4       5       6       7       8       9     10      11      12      13      14      15      16      17      18      19      20


    companion object {
        val VALUES = entries.toTypedArray()
    }

    fun isAvailable(creditsUnlocked: Boolean): Boolean = !requiresCredits || creditsUnlocked

    fun spawnCountForWave(wave: Int): Int {
        val index = wave.coerceIn(0, spawnsPerWaveBeforeBonus.lastIndex)
        return spawnsPerWaveBeforeBonus[index]
    }
}