package joshxviii.plantz.raid

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazLootTables
import joshxviii.plantz.entity.zombie.*
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders
import net.minecraft.world.level.storage.loot.LootTable
import java.util.function.IntFunction

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
        weightFn = { _, credits -> if (credits) 1.2f else 1.75f },
        spawnFn = { raid, credits ->
            val wave = raid.wavesSpawned
            val omen = raid.zombieRaidOmenLevel

            listOf(
                WaveSpawnEntry(
                    PazEntities.BROWN_COAT,
                    scaled(4f + wave * 1.8f, omen, credits, min = 3)
                ),
                WaveSpawnEntry(
                    PazEntities.NEWSPAPER_ZOMBIE,
                    scaled(1f + wave * 0.5f, omen, credits, min = 0)
                ),
                WaveSpawnEntry(
                    PazEntities.DIGGER_ZOMBIE,
                    if (wave > 2) scaled(1f + wave * 0.4f, omen, credits) else 0
                ),
                WaveSpawnEntry(
                    PazEntities.IMP,
                    if (wave > 4) scaled(1f + (wave - 4) * 0.7f, omen, credits) else 0
                ),
                WaveSpawnEntry(
                    PazEntities.ALL_STAR,
                    if (wave > 4) scaled(1f + (wave - 4) * 0.5f, omen, credits) else 0
                ),
                WaveSpawnEntry(
                    PazEntities.DISCO_ZOMBIE,
                    if (wave > 5) scaled(0.8f + (wave - 5) * 0.35f, omen, credits) else 0
                ),
                WaveSpawnEntry(
                    PazEntities.GARGANTUAR,
                    if (wave > 8) scaled(1f + (wave - 8) * 0.3f, omen, credits, min = 0) else 0
                ),
                WaveSpawnEntry(
                    PazEntities.ENGINEER_ZOMBIE,
                    if (credits && wave > 4) scaled(1f + (wave - 4) * 0.4f, omen, true) else 0
                ),
                WaveSpawnEntry(
                    PazEntities.SOLDIER_ZOMBIE,
                    if (credits && wave > 7) scaled(1f + (wave - 7) * 0.5f, omen, true) else 0
                )
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
            val wave = raid.wavesSpawned
            val omen = raid.zombieRaidOmenLevel

            listOf(
                WaveSpawnEntry(
                    PazEntities.BROWN_COAT,
                    scaled(5f + wave * 1.6f, omen, credits, min = 3),
                    ::spawnBucketBrigade
                ),
                WaveSpawnEntry(
                    PazEntities.NEWSPAPER_ZOMBIE,
                    scaled(1f + wave * 0.7f, omen, credits, min = 1),
                    ::spawnBucketBrigade
                )
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
            val wave = raid.wavesSpawned
            val omen = raid.zombieRaidOmenLevel

            listOf(
                WaveSpawnEntry(
                    PazEntities.ALL_STAR,
                    scaled(3f + wave * 0.9f, omen, credits, min = 2)
                ),
                WaveSpawnEntry(
                    PazEntities.IMP,
                    scaled(4f + wave * 0.8f, omen, credits, min = 1),
                    ::spawnHalftimeShowdown
                )
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
            val wave = raid.wavesSpawned
            val omen = raid.zombieRaidOmenLevel

            listOf(
                WaveSpawnEntry(
                    PazEntities.BROWN_COAT,
                    scaled(5f + wave * 0.9f, omen, credits, min = 5),
                    ::spawnWinterWonderland
                ),
                WaveSpawnEntry(
                    PazEntities.IMP,
                    scaled(2f + wave * 0.7f, omen, credits, min = 2),
                    ::spawnWinterWonderland
                ),
                WaveSpawnEntry(
                    PazEntities.ZOMBIE_YETI,
                    scaled(1.2f + wave * 0.35f, omen, credits, min = 1)
                )
            )
        },
        lootTableFn = { _, _ -> PazLootTables.MAIL_REWARD_WINTER}
    ),
    PIRATE_INVASION(
        minWave = 5,
        maxWave = 14,
        creditsRequired = false,
        weightFn = { raid, credits ->
            0.13f + (raid.zombieRaidOmenLevel * 0.05f) + if (credits) 0.08f else 0f
        },
        spawnFn = { raid, credits ->
            val wave = raid.wavesSpawned
            val omen = raid.zombieRaidOmenLevel

            listOf(
                WaveSpawnEntry(
                    PazEntities.BROWN_COAT,
                    scaled(6f + wave * 1.4f, omen, credits, min = 5),
                    ::spawnPirateInvasion
                ),
                WaveSpawnEntry(
                    PazEntities.IMP,
                    scaled(3f + wave * 0.6f, omen, credits, min = 2),
                    ::spawnPirateInvasion
                ),
                WaveSpawnEntry(
                    PazEntities.PIRATE_CAPTAIN,
                    if (credits) scaled(2f + wave * 0.4f, omen, true, min = 1) else 0
                ),
                WaveSpawnEntry(
                    PazEntities.GARGANTUAR,
                    scaled(0.6f + wave * 0.25f, omen, credits, min = 0),
                    ::spawnPirateInvasion
                )
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
        spawnFn = { raid, credits ->
            val wave = raid.wavesSpawned
            val omen = raid.zombieRaidOmenLevel

            listOf(
                WaveSpawnEntry(
                    PazEntities.ROBO_ZOMBIE,
                    scaled(2.5f + wave * 0.35f, omen, credits, min = 1)
                ),
                WaveSpawnEntry(
                    PazEntities.ENGINEER_ZOMBIE,
                    scaled(2.5f + wave * 0.45f, omen, credits, min = 2)
                ),
                WaveSpawnEntry(
                    PazEntities.SOLDIER_ZOMBIE,
                    scaled(6f + wave * 0.7f, omen, credits, min = 2)
                )
            )
        },
        lootTableFn = { _, _ -> PazLootTables.MAIL_REWARD_ARMY}
    ),
    LEAGUE_OF_AWESOME(
        minWave = 8,
        maxWave = 20,
        creditsRequired = true,
        weightFn = { raid, credits ->
            if (!credits) 0f else 0.2f + (raid.zombieRaidOmenLevel * 0.05f)
        },
        spawnFn = { raid, credits ->
            val wave = raid.wavesSpawned
            val omen = raid.zombieRaidOmenLevel

            listOf(
                WaveSpawnEntry(
                    PazEntities.BROWN_COAT,
                    scaled(4f + wave * 1.5f, omen, credits, min = 5),
                    ::spawnLeagueOfAwesome
                ),
                WaveSpawnEntry(
                    PazEntities.SUPER_BRAINZ,
                    scaled(1f + wave * 0.3f, omen, credits, min = 3),
                    ::spawnLeagueOfAwesome
                )
            )
        },
        lootTableFn = { _, _ -> PazLootTables.MAIL_REWARD_LEAGUE}
    );


    override fun getSerializedName(): String = name.lowercase()

    companion object {
        val CODEC: Codec<WaveType> = StringRepresentable.fromEnum(WaveType::values)
        private val BY_ID: IntFunction<WaveType> = ByIdMap.continuous(WaveType::ordinal, WaveType.entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, WaveType> = ByteBufCodecs.idMapper<WaveType>(BY_ID, WaveType::ordinal)

        fun omenScale(omen: Int): Float = 1f+(omen-1) * 0.15f // linear omen scaling
        fun creditsBonus(credits: Boolean): Float = if (credits) 1.25f else 1f // credits bonus

        fun scaled(base: Float, omen: Int, credits: Boolean, min: Int = 0, max: Int = Int.MAX_VALUE): Int {
            val raw = base * omenScale(omen) * creditsBonus(credits)
            val jitter = 0.85f + Math.random().toFloat() * 0.3f // +-15 %
            return (raw * jitter).toInt().coerceIn(min, max)
        }

        fun spawnBucketBrigade(zombie: Zombie) {
            zombie.setItemSlot(EquipmentSlot.HEAD, Items.BUCKET.defaultInstance)
            zombie.setDropChance(EquipmentSlot.HEAD, 0.0f)
            for (slot in mutableListOf(EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
                val level = zombie.level() as? ServerLevel?: continue
                val difficulty = level.getCurrentDifficultyAt(zombie.blockPosition())
                if (zombie.random.nextFloat() < 0.7f) {
                    val itemStack = Mob.getEquipmentForSlot(slot, 2)?.defaultInstance?: continue
                    if (zombie.random.nextFloat() < 0.3f * difficulty.specialMultiplier) EnchantmentHelper.enchantItemFromProvider(itemStack, level.registryAccess(), VanillaEnchantmentProviders.MOB_SPAWN_EQUIPMENT, level.getCurrentDifficultyAt(zombie.blockPosition()), zombie.random)
                    zombie.setItemSlot(slot, itemStack)
                }
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

            if (zombie !is Gargantuar) {
                for (slot in mutableListOf(EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).apply { if (zombie is BrownCoat) addFirst(EquipmentSlot.HEAD) }) {
                    val level = zombie.level() as? ServerLevel?: continue
                    val difficulty = level.getCurrentDifficultyAt(zombie.blockPosition())
                    if (slot == EquipmentSlot.HEAD && !zombie.getItemBySlot(slot).isEmpty) continue
                    if (zombie.random.nextFloat() < 0.25f * difficulty.specialMultiplier) {
                        val itemStack = Mob.getEquipmentForSlot(slot, 3)?.defaultInstance?: continue

                        if (zombie.random.nextFloat() < 0.25f) EnchantmentHelper.enchantItemFromProvider(
                            itemStack,
                            level.registryAccess(),
                            VanillaEnchantmentProviders.MOB_SPAWN_EQUIPMENT,
                            level.getCurrentDifficultyAt(zombie.blockPosition()),
                            zombie.random
                        )

                        zombie.setItemSlot(slot, itemStack)
                    }
                }
            }
        }

        fun spawnLeagueOfAwesome(zombie: Zombie) {
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

    data class WaveSpawnEntry(
        val entityType: EntityType<out Zombie>,
        val count: Int = 1,
        val configure: (Zombie) -> Unit = {},
    )
}