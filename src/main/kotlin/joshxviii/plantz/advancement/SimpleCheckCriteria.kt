package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.server.level.ServerPlayer
import java.util.Optional

class SimpleCheckCriteria(
    playerCtx: Optional<Holder<LootItemCondition>>,
): SimpleCriterionCondition<Boolean>(playerCtx) {

    companion object {
        val CODEC: Codec<SimpleCheckCriteria> = RecordCodecBuilder.create { it.group(
            LootItemCondition.CODEC.optionalFieldOf("player").forGetter(SimpleCheckCriteria::playerCtx)
        ).apply(it, ::SimpleCheckCriteria) }
    }

    override fun matches(player: ServerPlayer, context: Boolean): Boolean {
        return context
    }
}