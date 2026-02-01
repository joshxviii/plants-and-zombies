package joshxviii.plantz.raid

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.VisibleForDebug
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.*
import java.util.function.Function

fun ServerLevel.getZombieRaids(): ZombieRaids {
    return dataStorage.computeIfAbsent(ZombieRaids.TYPE)
}

class ZombieRaids(
    val zombieRaidMap: Int2ObjectMap<ZombieRaid> = Int2ObjectOpenHashMap<ZombieRaid>(),
    var nextId: Int = 1,
    var tick: Int = 0
) : SavedData() {

    init {
        this.setDirty()
    }

    private constructor(raids: MutableList<ZombieRaidWithId>, nextId: Int, tick: Int) : this() {
        for (raid in raids) {
            this.zombieRaidMap.put(raid.id, raid.raid)
        }

        this.nextId = nextId
        this.tick = tick
    }

    fun get(raidId: Int): ZombieRaid = this.zombieRaidMap.get(raidId)

    fun getId(raid: ZombieRaid): OptionalInt {
        for (entry in this.zombieRaidMap.int2ObjectEntrySet()) {
            if (entry.value === raid) return OptionalInt.of(entry.intKey)
        }

        return OptionalInt.empty()
    }

    fun tick(level: ServerLevel) {
        this.tick++
        val raidIterator: MutableIterator<ZombieRaid> = zombieRaidMap.values.iterator()

        while (raidIterator.hasNext()) {
            val raid = raidIterator.next()
            if (!level.gameRules.get<Boolean>(GameRules.RAIDS)!!) raid.stop()

            if (raid.isStopped()) {
                raidIterator.remove()
                this.setDirty()
            } else raid.tick(level)
        }

        if (this.tick % 200 == 0) this.setDirty()
    }

    fun createOrExtendZombieRaid(player: ServerPlayer, flagPosition: BlockPos): ZombieRaid? {
        if (player.isSpectator) return null
        else {
            val level = player.level()
            if (!level.gameRules.get<Boolean>(GameRules.RAIDS)!!) return null

            val raid = this.getOrCreateRaid(level, flagPosition)

            if (!raid.started && !this.zombieRaidMap.containsValue(raid)) {
                zombieRaidMap.put(this.uniqueId, raid)
                level.players().filter { it.blockPosition().distSqr(flagPosition) < 96 } .forEach {
                    it.sendSystemMessage(Component.translatable("event.plantz.zombie_raid.start"))
                    raid.zombieRaidEvent.addPlayer(it)
                }
            }

            if (!raid.started || raid.zombieRaidOmenLevel > 0) {
                raid.absorbRaidOmen(player)
            }

            this.setDirty()
            return raid
        }
    }

    private fun getOrCreateRaid(level: ServerLevel, pos: BlockPos): ZombieRaid {
        val zombieRaid = level.getZombieRaids().getNearbyRaid(pos, 64)
        return zombieRaid ?: ZombieRaid(center = pos, difficulty = level.difficulty)
    }

    private val uniqueId: Int
        get() = ++this.nextId

    fun getNearbyRaid(pos: BlockPos, maxDistSqr: Int): ZombieRaid? {
        var closest: ZombieRaid? = null
        var closestDistanceSqr = maxDistSqr.toDouble()

        for (raid in this.zombieRaidMap.values) {
            val distance = raid.center.distSqr(pos)
            if (raid.active && distance < closestDistanceSqr) {
                closest = raid
                closestDistanceSqr = distance
            }
        }

        return closest
    }

    @VisibleForDebug
    fun getRaidCentersInChunk(chunkPos: ChunkPos): MutableList<BlockPos> {
        return this.zombieRaidMap.values.stream().map<BlockPos> { obj: ZombieRaid -> obj.center }
            .filter { chunkPos.contains(it) }.toList()
    }

    @JvmRecord
    private data class ZombieRaidWithId(val id: Int, val raid: ZombieRaid) {
        companion object {
            val CODEC: Codec<ZombieRaidWithId> = RecordCodecBuilder.create<ZombieRaidWithId>(
                Function { i: RecordCodecBuilder.Instance<ZombieRaidWithId> ->
                    i.group<Int, ZombieRaid>(
                        Codec.INT.fieldOf("id").forGetter<ZombieRaidWithId>(ZombieRaidWithId::id),
                        ZombieRaid.MAP_CODEC.forGetter<ZombieRaidWithId>(ZombieRaidWithId::raid)
                    ).apply<ZombieRaidWithId>(i) { id: Int, raid: ZombieRaid -> ZombieRaidWithId(id, raid) }
                }
            )

            fun from(entry: Int2ObjectMap.Entry<ZombieRaid>): ZombieRaidWithId = ZombieRaidWithId(entry.intKey, entry.value)
        }
    }

    companion object {
        private const val RAID_FILE_ID = "zombie_raids"
        val CODEC: Codec<ZombieRaids> = RecordCodecBuilder.create<ZombieRaids>(
            Function { i: RecordCodecBuilder.Instance<ZombieRaids> ->
                i.group(
                    ZombieRaidWithId.CODEC
                        .listOf()
                        .optionalFieldOf("zombie_raids", mutableListOf<ZombieRaidWithId>())
                        .forGetter<ZombieRaids> { r: ZombieRaids ->
                            r.zombieRaidMap.int2ObjectEntrySet().stream()
                                .map<ZombieRaidWithId> { entry: Int2ObjectMap.Entry<ZombieRaid> ->
                                    ZombieRaidWithId.from(entry)
                                }.toList()
                        },
                    Codec.INT.fieldOf("next_id").forGetter<ZombieRaids> { r: ZombieRaids -> r.nextId },
                    Codec.INT.fieldOf("tick").forGetter<ZombieRaids> { r: ZombieRaids -> r.tick }
                ).apply<ZombieRaids>(i, ::ZombieRaids)
            }
        )
        val TYPE: SavedDataType<ZombieRaids> =
            SavedDataType<ZombieRaids>("zombie_raids", ::ZombieRaids, CODEC, DataFixTypes.SAVED_DATA_RAIDS)
        val TYPE_END: SavedDataType<ZombieRaids> =
            SavedDataType<ZombieRaids>("zombie_raids_end", ::ZombieRaids, CODEC, DataFixTypes.SAVED_DATA_RAIDS)

        fun getType(type: Holder<DimensionType>): SavedDataType<ZombieRaids> {
            return if (type.`is`(BuiltinDimensionTypes.END)) TYPE_END else TYPE
        }

        fun canJoinRaid(zombie: Zombie): Boolean {
            return zombie.isAlive && zombie.getNoActionTime() <= 2400
        }

        fun load(tag: CompoundTag): ZombieRaids {
            return CODEC.parse<Tag>(NbtOps.INSTANCE, tag).resultOrPartial().orElseGet(::ZombieRaids) as ZombieRaids
        }
    }
}