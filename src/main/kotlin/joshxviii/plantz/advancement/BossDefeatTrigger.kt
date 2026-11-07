package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazCriteria
import joshxviii.plantz.pazResource
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.advancements.triggers.Criterion
import net.minecraft.advancements.triggers.SimpleCriterionTrigger
import net.minecraft.core.Holder
import net.minecraft.server.level.ServerPlayer
import java.util.*

class BossDefeatTrigger : SimpleCriterionTrigger<BossDefeatTrigger.TriggerInstance>() {
    override fun codec(): Codec<TriggerInstance> = TriggerInstance.CODEC

    fun trigger(player: ServerPlayer) {
        val advancement = player.level().server.advancements
            .get(pazResource("boss_slayer"))
            ?: error("Missing required advancement plantz:boss_slayer")
        val defeatedBossCount = player.advancements
            .getOrStartProgress(advancement)
            .completedCriteria
            .count()

        this.trigger(player) { it.matches(defeatedBossCount + 1) }
    }

    data class TriggerInstance(
        val triggerPlayer: Optional<Holder<LootItemCondition>>,
        val targetBossDefeats: Optional<Int>,
    ) : SimpleInstance {
        fun matches(bossDefeats: Int): Boolean =
            targetBossDefeats.isEmpty || targetBossDefeats.get() == bossDefeats

        override fun player(): Optional<Holder<LootItemCondition>> = triggerPlayer

        companion object {
            val CODEC: Codec<TriggerInstance> = RecordCodecBuilder.create { instance ->
                instance.group(
                    LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
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
