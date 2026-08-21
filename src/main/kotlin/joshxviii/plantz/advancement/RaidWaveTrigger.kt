package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazCriteria
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.criterion.*
import net.minecraft.advancements.criterion.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import java.util.*

class RaidWaveTrigger : SimpleCriterionTrigger<RaidWaveTrigger.TriggerInstance>() {
    override fun codec(): Codec<TriggerInstance> {
        return TriggerInstance.CODEC
    }

    fun trigger(player: ServerPlayer, waveNumber: Int) {
        this.trigger(player) { it.matches(waveNumber) }
    }

    @JvmRecord
    data class TriggerInstance(val triggerPlayer: Optional<ContextAwarePredicate>, val targetWaveNumber: Optional<Int>) :
        SimpleInstance {
        fun matches(waveNumber: Int): Boolean {
            return this.targetWaveNumber.isEmpty || this.targetWaveNumber.get() <= waveNumber
        }

        override fun player(): Optional<ContextAwarePredicate> = this.triggerPlayer

        companion object {
            val CODEC: Codec<TriggerInstance> =
                RecordCodecBuilder.create {
                    it.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        Codec.INT.optionalFieldOf("wave_number").forGetter(TriggerInstance::targetWaveNumber)
                    ).apply(it, ::TriggerInstance)
                }

            fun waveStart(waveNumber: Int): Criterion<TriggerInstance> {
                return PazCriteria.RAID_WAVE_TRIGGER.createCriterion(
                    TriggerInstance(
                        Optional.empty<ContextAwarePredicate>(), Optional.of<Int>(waveNumber)
                    )
                )
            }
        }
    }
}