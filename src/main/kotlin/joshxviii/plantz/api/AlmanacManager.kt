package joshxviii.plantz.api

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.pazResource
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.UUID

/** Per-player almanac contents. Stored on the server, independent of the almanac item. */
data class AlmanacData(
    val seedPackets: List<ItemStack> = emptyList(),
    val zombieProgress: Set<Identifier> = emptySet(),
) {
    companion object {
        const val MAX_SEED_PACKETS = 512

        val CODEC: Codec<AlmanacData> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(MAX_SEED_PACKETS).optionalFieldOf("seed_packets", emptyList())
                    .forGetter(AlmanacData::seedPackets),
                Identifier.CODEC.listOf().optionalFieldOf("zombie_progress", emptyList())
                    .forGetter { it.zombieProgress.toList() },
            ).apply(instance) { packets, zombies -> AlmanacData(packets, zombies.toSet()) }
        }
    }
}

fun MinecraftServer.getAlmanacManager(): AlmanacManager =
    overworld().dataStorage.computeIfAbsent(AlmanacManager.TYPE)

class AlmanacManager private constructor(
    private val almanacs: MutableMap<UUID, AlmanacData> = mutableMapOf()
) : SavedData() {

    fun get(player: UUID): AlmanacData = almanacs[player] ?: AlmanacData()

    fun set(player: UUID, data: AlmanacData) {
        almanacs[player] = data
        setDirty()
    }

    fun setSeedPackets(player: UUID, seedPackets: List<ItemStack>) =
        set(player, get(player).copy(seedPackets = seedPackets.map { it.copy() }))

    fun discoverZombie(player: UUID, zombie: Identifier) {
        val data = get(player)
        if (zombie in data.zombieProgress) return
        set(player, data.copy(zombieProgress = data.zombieProgress + zombie))
    }

    companion object {
        private val ENTRY_CODEC: Codec<Pair<UUID, AlmanacData>> = RecordCodecBuilder.create { instance ->
            instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter { it.first },
                AlmanacData.CODEC.fieldOf("data").forGetter { it.second },
            ).apply(instance) { id, data -> id to data }
        }

        val CODEC: Codec<AlmanacManager> = ENTRY_CODEC.listOf().fieldOf("almanacs").codec().xmap(
            { entries -> AlmanacManager(entries.toMap().toMutableMap()) },
            { manager -> manager.almanacs.map { it.key to it.value } }
        )

        val TYPE = SavedDataType(pazResource("almanacs"), ::AlmanacManager, CODEC, DataFixTypes.LEVEL)
    }
}
