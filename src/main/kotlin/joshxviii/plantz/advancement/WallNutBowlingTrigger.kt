package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazCriteria
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.criterion.*
import net.minecraft.advancements.criterion.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import java.util.*

class WallNutBowlingTrigger : SimpleCriterionTrigger<WallNutBowlingTrigger.TriggerInstance>() {
    override fun codec(): Codec<TriggerInstance> {
        return TriggerInstance.CODEC
    }

    fun trigger(player: ServerPlayer, waveNumber: Int) {
        this.trigger(player) { it.matches(waveNumber) }
    }

    @JvmRecord
    data class TriggerInstance(val triggerPlayer: Optional<ContextAwarePredicate>, val targetEntitiesHit: Optional<Int>) :
        SimpleInstance {
        fun matches(hitCount: Int): Boolean = this.targetEntitiesHit.isEmpty || this.targetEntitiesHit.get() <= hitCount

        override fun player(): Optional<ContextAwarePredicate> = this.triggerPlayer

        companion object {
            val CODEC: Codec<TriggerInstance> =
                RecordCodecBuilder.create {
                    it.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        Codec.INT.optionalFieldOf("hit_count").forGetter(TriggerInstance::targetEntitiesHit)
                    ).apply(it, ::TriggerInstance)
                }

        }
    }
}