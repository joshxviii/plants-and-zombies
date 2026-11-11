package joshxviii.plantz.raid

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import joshxviii.plantz.PazPoiTypes.PLANTZ_FLAG
import joshxviii.plantz.block.entity.FlagBlockEntity
import joshxviii.plantz.effect.ZombieOmenMobEffect.Companion.MAX_FLAG_DISTANCE
import joshxviii.plantz.pazResource
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.VisibleForDebug
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.Difficulty
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import kotlin.jvm.optionals.getOrNull

fun ServerLevel.getZombieRaids(): ZombieRaids {
    return dataStorage.computeIfAbsent(ZombieRaids.TYPE)
}

fun LevelAccessor.isNearRaid(pos: BlockPos): Boolean {
    val dimension = (this as? ServerLevelAccessor)?.level?.dimension() ?: return false
    return ZombieRaids.isNearActiveRaid(dimension, pos)
}

class ZombieRaids(
    val zombieRaidMap: Int2ObjectMap<ZombieRaid> = Int2ObjectOpenHashMap<ZombieRaid>(),
    var nextId: Int = 1,
    var tick: Int = 0
) : SavedData() {

    init {
        setDirty()
    }

    private constructor(raids: MutableList<ZombieRaidWithId>, nextId: Int, tick: Int) : this() {
        for ((id, raid1) in raids) zombieRaidMap.put(id, raid1)
        this.nextId = nextId
        this.tick = tick
    }

    fun get(raidId: Int): ZombieRaid = zombieRaidMap.get(raidId)

    fun tick(level: ServerLevel) {
        tick++
        val raidIterator: MutableIterator<ZombieRaid> = zombieRaidMap.values.iterator()

        while (raidIterator.hasNext()) {
            val raid = raidIterator.next()
            if (!level.gameRules.get(GameRules.RAIDS) || level.difficulty == Difficulty.PEACEFUL) raid.stop(level)

            if (raid.isStopped()) {
                (level.getBlockEntity(raid.center) as? FlagBlockEntity)?.setMaxHealth(refill = true)
                raidIterator.remove()
                setDirty()
            } else raid.tick(level)
        }

        activeRaidCenters[level.dimension()] = zombieRaidMap.values.filter { !it.isStopped() }.map { it.center }

        if (tick % 200 == 0) setDirty()
    }

    fun createOrExtendZombieRaid(player: ServerPlayer, waveCount: Int = 0): ZombieRaid? {
        if (player.isSpectator) return null
        val level = player.level()
        val flagPosition = level.poiManager.findClosest(
            { it.`is`(PLANTZ_FLAG) },
            player.blockPosition(),
            MAX_FLAG_DISTANCE,
            PoiManager.Occupancy.HAS_SPACE
        ).getOrNull() ?: return null

        if (!level.gameRules.get(GameRules.RAIDS) || level.difficulty == Difficulty.PEACEFUL) return null

        val raid = getOrCreateRaid(level, flagPosition, waveCount)

        if (raid.startedBy == null) raid.startedBy = player.uuid

        if (raid.status == ZombieRaid.ZombieRaidStatus.INIT && !zombieRaidMap.containsValue(raid)) {
            zombieRaidMap.put(uniqueId, raid)
            level.players().filter { it.blockPosition().distSqr(flagPosition) < ZombieRaid.RAID_DISTANCE*ZombieRaid.RAID_DISTANCE } .forEach {
                it.sendSystemMessage(ZombieRaid.getStartMessage(player.seenCredits))
                raid.zombieRaidEvent.addPlayer(it)
            }
            raid.absorbRaidOmen(player)
        }

        raid.starterHasSeenCredits = raid.starterHasSeenCredits || player.seenCredits
        setDirty()
        return raid
    }

    private fun getOrCreateRaid(level: ServerLevel, pos: BlockPos, waveCount: Int): ZombieRaid {
        val zombieRaid = level.getZombieRaids().getNearbyRaid(pos, 16)
        return zombieRaid ?: ZombieRaid(center = pos, numWaves = waveCount)
    }

    private val uniqueId: Int
        get() = ++nextId

    fun getNearbyRaid(pos: BlockPos, maxDist: Int): ZombieRaid? {
        if (zombieRaidMap.isEmpty()) return null
        var closest: ZombieRaid? = null
        var closestDistanceSqr = (maxDist * maxDist).toDouble()

        for (raid in zombieRaidMap.values) {
            val distance = raid.center.distSqr(pos)
            if (!raid.isStopped() && distance < closestDistanceSqr) {
                closest = raid
                closestDistanceSqr = distance
            }
        }

        return closest
    }

    @JvmRecord
    private data class ZombieRaidWithId(val id: Int, val raid: ZombieRaid) {
        companion object {
            val CODEC: Codec<ZombieRaidWithId> = RecordCodecBuilder.create<ZombieRaidWithId> { i: RecordCodecBuilder.Instance<ZombieRaidWithId> ->
                i.group<Int, ZombieRaid>(
                    Codec.INT.fieldOf("id").forGetter<ZombieRaidWithId>(ZombieRaidWithId::id),
                    ZombieRaid.MAP_CODEC.forGetter<ZombieRaidWithId>(ZombieRaidWithId::raid)
                ).apply<ZombieRaidWithId>(i) { id: Int, raid: ZombieRaid -> ZombieRaidWithId(id, raid) }
            }

            fun from(entry: Int2ObjectMap.Entry<ZombieRaid>): ZombieRaidWithId = ZombieRaidWithId(entry.intKey, entry.value)
        }
    }

    companion object {
        private val RAID_FILE_ID = pazResource("zombie_raids")
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
            SavedDataType<ZombieRaids>(RAID_FILE_ID, ::ZombieRaids, CODEC, DataFixTypes.SAVED_DATA_RAIDS)

        private val activeRaidCenters = ConcurrentHashMap<ResourceKey<Level>, List<BlockPos>>()

        fun isNearActiveRaid(dimension: ResourceKey<Level>, pos: BlockPos): Boolean {
            val maxDistSqr = (ZombieRaid.RAID_DISTANCE * ZombieRaid.RAID_DISTANCE).toDouble()
            return activeRaidCenters[dimension]?.any { it.distSqr(pos) < maxDistSqr } ?: false
        }

        fun clearActiveRaidCenters() = activeRaidCenters.clear()
    }
}
