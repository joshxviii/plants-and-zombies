package joshxviii.plantz.raid

import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.zombie.*
import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.zombie.Zombie

enum class BossType(
    private val weightFn: (ZombieRaid) -> Float,
    private val bossSpawns: (ZombieRaid, WaveType) -> List<BossEntry>,
): StringRepresentable {
    GARGANTUAR(
        weightFn = { 1f },
        bossSpawns = { raid, wave ->
            listOf(
                BossEntry(
                    PazEntities.GARGANTUAR,
                    { if (it is Gargantuar && wave == WaveType.PIRATE_INVASION) it.variant = GargantuarVariant.PIRATE }
                ),
            )
        }
    ),
    ZOMBOSS(
        weightFn = { 0f },
        bossSpawns = { raid, wave -> listOf() }
    );

    override fun getSerializedName(): String = name.lowercase()

    companion object {
        fun pickBossType(raid: ZombieRaid): BossType {
            val eligible = BossType.entries
            val totalWeight = eligible.sumOf { it.weight(raid).toDouble() }.toFloat()
            if (totalWeight <= 0f) return GARGANTUAR

            val roll = raid.random.nextFloat() * totalWeight
            var runningWeight = 0f
            for (wave in eligible) {
                runningWeight += wave.weight(raid)
                if (roll < runningWeight) return wave
            }
            return GARGANTUAR
        }
    }

    fun bossEntityEntries(raid: ZombieRaid, wave: WaveType): List<BossEntry> = bossSpawns(raid, wave)

    fun weight(raid: ZombieRaid): Float = weightFn(raid)

    data class BossEntry(
        val entityType: EntityType<out Zombie>,
        val configure: (Zombie) -> Unit = {},
    )
}