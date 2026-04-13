package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.criterion.ContextAwarePredicate
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import java.util.Optional

class ZombieRaidContext(val pos: BlockPos)

class ZombieRaidCriteria(
    playerCtx: Optional<ContextAwarePredicate>
): SimpleCriterionCondition<ZombieRaidContext>(playerCtx) {

    companion object {
        val CODEC: Codec<ZombieRaidCriteria> = RecordCodecBuilder.create { it.group(
            ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(ZombieRaidCriteria::playerCtx),
        ).apply(it, ::ZombieRaidCriteria) }
    }

    override fun matches(player: ServerPlayer, context: ZombieRaidContext): Boolean {
        return true
    }
}