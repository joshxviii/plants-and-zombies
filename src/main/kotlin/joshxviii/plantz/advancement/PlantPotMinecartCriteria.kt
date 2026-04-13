package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.criterion.ContextAwarePredicate
import net.minecraft.server.level.ServerPlayer
import java.util.Optional

class PlantPotMinecartCriteria(
    playerCtx: Optional<ContextAwarePredicate>
): SimpleCriterionCondition<Boolean>(playerCtx) {

    companion object {
        val CODEC: Codec<PlantPotMinecartCriteria> = RecordCodecBuilder.create { it.group(
            ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(PlantPotMinecartCriteria::playerCtx),
        ).apply(it, ::PlantPotMinecartCriteria) }
    }

    override fun matches(player: ServerPlayer, context: Boolean): Boolean {
        return context
    }
}