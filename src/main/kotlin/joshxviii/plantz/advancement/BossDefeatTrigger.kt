package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazCriteria
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.criterion.ContextAwarePredicate
import net.minecraft.advancements.criterion.EntityPredicate
import net.minecraft.advancements.criterion.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import java.util.Optional

class BossDefeatTrigger : SimpleCriterionTrigger<BossDefeatTrigger.TriggerInstance>() {
    override fun codec(): Codec<TriggerInstance> = TriggerInstance.CODEC

    fun trigger(player: ServerPlayer, bossDefeats: Int) {
        this.trigger(player) { it.matches(bossDefeats) }
    }

    data class TriggerInstance(
        val triggerPlayer: Optional<ContextAwarePredicate>,
        val targetBossDefeats: Optional<Int>,
    ) : SimpleInstance {
        fun matches(bossDefeats: Int): Boolean =
            targetBossDefeats.isEmpty || targetBossDefeats.get() <= bossDefeats

        override fun player(): Optional<ContextAwarePredicate> = triggerPlayer

        companion object {
            val CODEC: Codec<TriggerInstance> = RecordCodecBuilder.create { instance ->
                instance.group(
                    EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                    Codec.INT.optionalFieldOf("boss_defeats").forGetter(TriggerInstance::targetBossDefeats),
                ).apply(instance, ::TriggerInstance)
            }

            fun bossesDefeated(count: Int): Criterion<TriggerInstance> =
                PazCriteria.BOSS_DEFEAT_TRIGGER.createCriterion(
                    TriggerInstance(Optional.empty(), Optional.of(count)),
                )
        }
    }
}
