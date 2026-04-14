package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.criterion.ContextAwarePredicate
import net.minecraft.server.level.ServerPlayer
import java.util.Optional

class RelocatePlantCriteria(
    playerCtx: Optional<ContextAwarePredicate>,
): SimpleCriterionCondition<Boolean>(playerCtx) {

    companion object {
        val CODEC: Codec<RelocatePlantCriteria> = RecordCodecBuilder.create { it.group(
            ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(RelocatePlantCriteria::playerCtx)
        ).apply(it, ::RelocatePlantCriteria) }
    }

    override fun matches(player: ServerPlayer, context: Boolean): Boolean {
        return context
    }
}