package joshxviii.plantz.raid

import joshxviii.plantz.PazEntities
import joshxviii.plantz.entity.zombie.*
import joshxviii.plantz.pazResource
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.zombie.Zombie

enum class BossType(
    private val weightFn: (ZombieRaid, WaveType) -> Float,
    private val bossSpawns: (ZombieRaid, WaveType) -> List<BossEntry>,
): StringRepresentable {
    GARGANTUAR(
        weightFn = { _, wave -> if (wave == WaveType.DEFAULT || wave == WaveType.PIRATE_INVASION) 1f else 0f },
        bossSpawns = { raid, wave ->
            listOf(
                BossEntry(
                    PazEntities.GARGANTUAR,
                    { if (it is Gargantuar && wave == WaveType.PIRATE_INVASION) it.variant = GargantuarVariant.PIRATE }
                ),
            )
        }
    ),
    ROBO_MECH(
        weightFn = { _, wave -> if (wave == WaveType.ROBO_ARMY) 10f else 0f },
        bossSpawns = { raid, wave ->
            listOf(
                BossEntry(
                    PazEntities.ROBO_ZOMBIE, ::bossify
                ),
            )
        }
    ),
    ZOMBOSS(
        weightFn = { _, _ -> 0f },
        bossSpawns = { raid, wave -> listOf() }
    );

    override fun getSerializedName(): String = name.lowercase()

    companion object {
        fun pickBossType(raid: ZombieRaid, waveType: WaveType): BossType {
            val eligible = BossType.entries
            val totalWeight = eligible.sumOf { it.weight(raid, waveType).toDouble() }.toFloat()
            if (totalWeight <= 0f) return GARGANTUAR

            val roll = raid.random.nextFloat() * totalWeight
            var runningWeight = 0f
            for (wave in eligible) {
                runningWeight += wave.weight(raid, waveType)
                if (roll < runningWeight) return wave
            }
            return GARGANTUAR
        }

        val BOSSIFY: Identifier = pazResource("bossify")
        private val BOSS_SCALE = AttributeModifier(BOSSIFY, 1.0, AttributeModifier.Operation.ADD_VALUE)
        private val BOSS_HEALTH = AttributeModifier(BOSSIFY, 15.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)

        fun bossify(zombie: Zombie) {
            zombie.getAttribute(Attributes.SCALE)?.let { if (!it.hasModifier(BOSSIFY)) it.addPermanentModifier(BOSS_SCALE) }
            zombie.getAttribute(Attributes.MAX_HEALTH)?.let { if (!it.hasModifier(BOSSIFY)) it.addPermanentModifier(BOSS_HEALTH) }
            zombie.health = zombie.maxHealth
            if (zombie is RoboZombie) zombie.isTransformed = true
        }
    }

    fun bossEntityEntries(raid: ZombieRaid, wave: WaveType): List<BossEntry> = bossSpawns(raid, wave)

    fun weight(raid: ZombieRaid, waveType: WaveType): Float = weightFn(raid, waveType)

    data class BossEntry(
        val entityType: EntityType<out Zombie>,
        val configure: (Zombie) -> Unit = {},
    )
}