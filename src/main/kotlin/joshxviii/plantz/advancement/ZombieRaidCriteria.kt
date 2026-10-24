package joshxviii.plantz.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import java.util.Optional

class ZombieRaidContext(val pos: BlockPos)

class ZombieRaidCriteria(
    playerCtx: Optional<Holder<LootItemCondition>>
): SimpleCriterionCondition<ZombieRaidContext>(playerCtx) {

    companion object {
        val CODEC: Codec<ZombieRaidCriteria> = RecordCodecBuilder.create { it.group(
            LootItemCondition.CODEC.optionalFieldOf("player").forGetter(ZombieRaidCriteria::playerCtx),
        ).apply(it, ::ZombieRaidCriteria) }
    }

    override fun matches(player: ServerPlayer, context: ZombieRaidContext): Boolean {
        return true
    }
}