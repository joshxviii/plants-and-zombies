package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.criterion.ContextAwarePredicate
import net.minecraft.server.level.ServerPlayer
import java.util.Optional

class SimpleCheckCriteria(
    playerCtx: Optional<ContextAwarePredicate>,
): SimpleCriterionCondition<Boolean>(playerCtx) {

    companion object {
        val CODEC: Codec<SimpleCheckCriteria> = RecordCodecBuilder.create { it.group(
            ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(SimpleCheckCriteria::playerCtx)
        ).apply(it, ::SimpleCheckCriteria) }
    }

    override fun matches(player: ServerPlayer, context: Boolean): Boolean {
        return true
    }
}