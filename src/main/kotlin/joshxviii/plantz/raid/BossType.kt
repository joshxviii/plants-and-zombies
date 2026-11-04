package joshxviii.plantz.raid

import com.mojang.serialization.Codec
import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.zombie.*
import joshxviii.plantz.pazResource
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.zombie.Zombie

// Hero reward bonus loot should only be given out when a boss wave happens, rather than every 5 waves (current).

/**
 * Defines a boss type for a zombie raid. Multiple entities can be spawned as a single boss type.
 */
enum class BossType(
    private val minWave: Int,
    private val maxWave: Int,
    private val weightFn: (ZombieRaid, WaveType) -> Float,
    private val bossSpawns: (ZombieRaid, WaveType) -> List<BossEntry>,
): StringRepresentable {
    //TODO: I would like to make this a data-driven system. Boss types should be defined as "plantz/boss_type/[boss_name].json" files.
    // SpawnRequirements, Weights, BossEntries, and Boss Configurations should all be editable in the .json.
    // Parsing should be done in a new [PazAPI] class.
    GIGA_ALL_STAR(
        minWave = 1,
        maxWave = 7,
        weightFn = { raid, wave -> 1f },
        bossSpawns = { raid, wave ->
            listOf(
                BossEntry(
                    PazEntities.ALL_STAR, ::bossify
                ),
            )
        }
    ),
    BARON_VON_BATS(
        minWave = 6,
        maxWave = 18,
        weightFn = { _, wave -> if (wave == WaveType.DEFAULT || wave == WaveType.ZOMBOWEEN) 1f else 0f },
        bossSpawns = { raid, wave ->
            listOf(
                BossEntry(
                    PazEntities.BARON_VON_BATS,
                ),
            )
        }
    ),
    GARGANTUAR(
        minWave = 9,
        maxWave = 20,
        weightFn = { _, wave -> if (wave == WaveType.DEFAULT || wave == WaveType.PIRATE_INVASION) 2f else 0f },
        bossSpawns = { raid, wave ->
            listOf(
                BossEntry(
                    PazEntities.GARGANTUAR,
                    { if (it is Gargantuar && wave == WaveType.PIRATE_INVASION) it.variant = GargantuarVariant.PIRATE }
                ),
            )
        }
    ),
    ROBO_GENERAL(
        minWave = 10,
        maxWave = 20,
        weightFn = { _, wave -> if (wave == WaveType.ROBO_ARMY) 1f else 0f },
        bossSpawns = { raid, wave ->
            listOf(
                BossEntry(
                    PazEntities.ROBO_ZOMBIE, ::bossify
                ),
            )
        }
    ),
    ZOMBOSS(
        minWave = 20,
        maxWave = 20,
        weightFn = { _, wave -> if (wave == WaveType.ZOMBOSS) 1f else 0f },
        bossSpawns = { raid, wave -> listOf() }
    );

    override fun getSerializedName(): String = name.lowercase()

    companion object {
        val CODEC: Codec<BossType> = StringRepresentable.fromEnum<BossType> { entries.toTypedArray() }
        private val BY_ID = ByIdMap.continuous(BossType::ordinal, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO)
        val STREAM_CODEC: StreamCodec<ByteBuf, BossType> = ByteBufCodecs.idMapper(BY_ID, BossType::ordinal)

        fun pickBossType(raid: ZombieRaid, waveType: WaveType): BossType? {
            val eligible = BossType.entries.filter { it.isAvailable(raid) }
            val totalWeight = eligible.sumOf { it.weight(raid, waveType).toDouble() }.toFloat()
            if (totalWeight <= 0f) return null

            val roll = raid.random.nextFloat() * totalWeight
            var runningWeight = 0f
            for (bossType in eligible) {
                runningWeight += bossType.weight(raid, waveType)
                if (roll < runningWeight) return bossType
            }
            return null
        }

        val BOSSIFY: Identifier = pazResource("bossify")
        private val BOSS_SCALE = AttributeModifier(BOSSIFY, 0.25, AttributeModifier.Operation.ADD_VALUE)
        private val BOSS_HEALTH = AttributeModifier(BOSSIFY, 2.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        private val BOSS_DAMAGE = AttributeModifier(BOSSIFY, 1.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)

        fun bossify(zombie: Zombie) {
            zombie.getAttribute(Attributes.SCALE)?.let { if (!it.hasModifier(BOSSIFY)) it.addPermanentModifier(BOSS_SCALE) }
            zombie.getAttribute(Attributes.MAX_HEALTH)?.let { if (!it.hasModifier(BOSSIFY)) it.addPermanentModifier(BOSS_HEALTH) }
            zombie.getAttribute(Attributes.ATTACK_DAMAGE)?.let { if (!it.hasModifier(BOSSIFY)) it.addPermanentModifier(BOSS_DAMAGE) }
            zombie.health = zombie.maxHealth
            if (zombie is RoboZombie) {
                zombie.isTransformed = true
                zombie.variant = RoboZombieVariant.GENERAL
            }
            if (zombie is AllStar) zombie.variant = AllStarVariant.GIGA
        }
    }

    fun bossEntityEntries(raid: ZombieRaid, wave: WaveType): List<BossEntry> = bossSpawns(raid, wave)

    fun isAvailable(raid: ZombieRaid): Boolean = raid.currentWaveNumber() + 1 in minWave..maxWave

    fun weight(raid: ZombieRaid, waveType: WaveType): Float = weightFn(raid, waveType)

    data class BossEntry(
        val entityType: EntityType<out Zombie>,
        val configure: (Zombie) -> Unit = {},
    )
}