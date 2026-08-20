package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.criterion.ContextAwarePredicate
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import java.util.Optional

class ZombieHardestRaidCriteria(
    playerCtx: Optional<ContextAwarePredicate>
): SimpleCriterionCondition<Boolean>(playerCtx) {

    companion object {
        val CODEC: Codec<ZombieHardestRaidCriteria> = RecordCodecBuilder.create { it.group(
            ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(ZombieHardestRaidCriteria::playerCtx),
        ).apply(it, ::ZombieHardestRaidCriteria) }
    }

    override fun matches(player: ServerPlayer, context: Boolean): Boolean {
        return true
    }
}