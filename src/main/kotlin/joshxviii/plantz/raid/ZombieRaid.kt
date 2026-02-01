package joshxviii.plantz.raid

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazTags.EntityTypes.ZOMBIE_RAIDERS
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerBossEvent
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.world.BossEvent
import net.minecraft.world.Difficulty
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3

class ZombieRaid(
    val center: BlockPos,
    var started: Boolean = false,
    var active: Boolean = false,
    var ticksActive: Long = 0,
    var zombieRaidOmenLevel: Int = 0,
    var wavesSpawned: Int = 0,
    var raidCooldownTicks: Int = DEFAULT_PRE_RAID_TICKS,
    var postRaidTicks: Int = 0,
    var totalHealth: Float = 0f,
    var numWaves: Int = 0,
    var status: ZombieRaidStatus = ZombieRaidStatus.ONGOING,
    val difficulty: Difficulty = Difficulty.NORMAL,
) {
    companion object {
        fun getWaveSpawnCount(difficulty: Difficulty): Int = 3 + difficulty.id * 2
        val MAP_CODEC : MapCodec<ZombieRaid> = RecordCodecBuilder.mapCodec {
            it.group(
                BlockPos.CODEC.fieldOf("center").forGetter<ZombieRaid> { r: ZombieRaid -> r.center },
                Codec.BOOL.fieldOf("started").forGetter<ZombieRaid> { r: ZombieRaid -> r.started },
                Codec.BOOL.fieldOf("active").forGetter<ZombieRaid> { r: ZombieRaid -> r.active },
                Codec.LONG.fieldOf("ticks_active").forGetter<ZombieRaid> { r: ZombieRaid -> r.ticksActive },
                Codec.INT.fieldOf("raid_omen_level").forGetter<ZombieRaid> { r: ZombieRaid -> r.zombieRaidOmenLevel },
                Codec.INT.fieldOf("waves_spawned").forGetter<ZombieRaid> { r: ZombieRaid -> r.wavesSpawned },
                Codec.INT.fieldOf("cooldown_ticks").forGetter<ZombieRaid> { r: ZombieRaid -> r.raidCooldownTicks },
                Codec.INT.fieldOf("post_raid_ticks").forGetter<ZombieRaid> { r: ZombieRaid -> r.postRaidTicks },
                Codec.FLOAT.fieldOf("total_health").forGetter<ZombieRaid> { r: ZombieRaid -> r.totalHealth },
                Codec.INT.fieldOf("wave_count").forGetter<ZombieRaid> { r: ZombieRaid -> r.numWaves },
                ZombieRaidStatus.CODEC.fieldOf("status").forGetter<ZombieRaid> { r: ZombieRaid -> r.status },
            ).apply<ZombieRaid>(it, ::ZombieRaid )
        }
        val ZOMBIE_RAID_NAME: Component = Component.translatable("event.plantz.zombie_raid")
        val ZOMBIE_RAID_BAR_VICTORY: Component = Component.translatable("event.plantz.zombie_raid.victory")
        val ZOMBIE_RAID_BAR_DEFEAT: Component = Component.translatable("event.plantz.zombie_raid.defeat")
        const val DEFAULT_PRE_RAID_TICKS: Int = 300
        const val POST_RAID_TICK_LIMIT: Int = 40
    }
    val zombieRaidEvent = ServerBossEvent(ZOMBIE_RAID_NAME, BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10)
    private var raidStatus: ZombieRaidStatus = ZombieRaidStatus.ONGOING
    private val waveZombieMap: MutableMap<Int, MutableSet<Zombie>> = Maps.newHashMap<Int, MutableSet<Zombie>>()
    private val random = RandomSource.create()
    var waveSpawnPos : BlockPos? = null

    enum class ZombieRaidStatus(private val state: String) : StringRepresentable {
        ONGOING("ongoing"),
        VICTORY("victory"),
        LOSS("loss"),
        STOPPED("stopped");

        override fun getSerializedName(): String = this.state

        companion object {
            val CODEC: Codec<ZombieRaidStatus> = StringRepresentable.fromEnum<ZombieRaidStatus> { entries.toTypedArray() }
        }
    }

    init {
        active = true
        zombieRaidEvent.progress = 0.0f
        numWaves = getWaveSpawnCount(difficulty)
    }

    fun tick(level: ServerLevel) {
        if (!active) return
        ticksActive++

        if (raidCooldownTicks > 0) {
            raidCooldownTicks--
            return
        }

        if (shouldSpawnNextWave()) {
            val spawnPos = findRandomSpawnPos(level, 20) ?: center
            spawnNextWave(level, spawnPos)
            raidCooldownTicks = 200 + random.nextInt(200)  // Delay next wave 10-20s
        }

        updateBossbar()

        // Victory check
        if (getTotalZombiesAlive() == 0 && wavesSpawned >= numWaves) {
            status = ZombieRaidStatus.VICTORY
            zombieRaidEvent.progress = 1.0f
            zombieRaidEvent.color = BossEvent.BossBarColor.YELLOW
            zombieRaidEvent.name = ZOMBIE_RAID_BAR_VICTORY
            postRaidTicks = POST_RAID_TICK_LIMIT
            level.players().forEach { it.sendSystemMessage(ZOMBIE_RAID_BAR_VICTORY) }
        }

        if (postRaidTicks > 0) {
            postRaidTicks--
            if (postRaidTicks <= 0) {
                stop()
            }
        }

        zombieRaidEvent.players.forEach { p ->
            if (p.blockPosition().distSqr(center) > 96) zombieRaidEvent.removePlayer(p)
        }
    }

    fun absorbRaidOmen(player: ServerPlayer): Boolean {
        val effect = player.getEffect(PazEffects.ZOMBIE_OMEN)
        if (effect == null) return false
        else {
            zombieRaidOmenLevel += effect.amplifier + 1
            return true
        }
    }


    private fun spawnNextWave(level: ServerLevel, pos: BlockPos) {
        for (i in 0..numWaves) {

            val tag = BuiltInRegistries.ENTITY_TYPE.get(ZOMBIE_RAIDERS)
            val entityType = if (!tag.isEmpty) tag.get().getRandomElement(level.random).get().value() else return

            val entity = entityType.create(level, EntitySpawnReason.EVENT) ?: continue
            entity.moveOrInterpolateTo(Vec3(pos.x + 0.5, pos.y.toDouble(), pos.z + 0.5), level.random.nextFloat() * 360f, 0f)
            if(level.addFreshEntity(entity) && entity is Zombie) {
                addWaveMob(level, i, entity)
            }
        }
        wavesSpawned++
    }

    fun addWaveMob(level: ServerLevel, wave: Int, zombie: Zombie): Boolean {
        waveZombieMap.computeIfAbsent(wave) { Sets.newHashSet<Zombie>() }
        val zombies = waveZombieMap[wave] as MutableSet<Zombie>
        var existingCopy: Zombie? = null

        for (r in zombies) {
            if (r.getUUID() == zombie.getUUID()) {
                existingCopy = r
                break
            }
        }

        if (existingCopy != null) {
            zombies.remove(existingCopy)
            zombies.add(zombie)
        }

        zombies.add(zombie)
        totalHealth += zombie.health

        updateBossbar()
        return true
    }

    fun updateBossbar() {
        zombieRaidEvent.setProgress(Mth.clamp(getHealthOfZombies() / totalHealth, 0.0f, 1.0f))
    }

    fun getTotalZombiesAlive(): Int = waveZombieMap.values.stream().mapToInt { it.size }.sum()
    fun getHealthOfZombies(): Float = waveZombieMap.values.map { it.map { it.health }.sum() }.sum()

    private fun shouldSpawnNextWave(): Boolean =  getTotalZombiesAlive() == 0 && wavesSpawned < numWaves

    private fun findRandomSpawnPos(level: ServerLevel, maxTries: Int): BlockPos? {
        val secondsRemaining = raidCooldownTicks / 20
        val howFar = 0.22f * secondsRemaining - 0.24f
        val spawnPos = MutableBlockPos()
        val startAngle = random.nextFloat() * (Math.PI * 2).toFloat()

        for (i in 0..<maxTries) {
            val angle = startAngle + Math.PI.toFloat() * i / 8.0f
            val spawnX = center.x + Mth.floor(Mth.cos(angle.toDouble()) * 32.0f * howFar) + random.nextInt(3) * Mth.floor(howFar)
            val spawnZ = center.z + Mth.floor(Mth.sin(angle.toDouble()) * 32.0f * howFar) + random.nextInt(3) * Mth.floor(howFar)
            val spawnY = level.getHeight(Heightmap.Types.WORLD_SURFACE, spawnX, spawnZ)
            if (Mth.abs(spawnY - this.center.y) <= 96) {
                spawnPos.set(spawnX, spawnY, spawnZ)
                if (secondsRemaining <= 7) {
                    val delta = 10
                    if (level.hasChunksAt(
                            spawnPos.x - 10,
                            spawnPos.z - 10,
                            spawnPos.x + 10,
                            spawnPos.z + 10
                        )
                        && level.isPositionEntityTicking(spawnPos)
                    ) return spawnPos
                }
            }
        }
        return null
    }

    fun stop() {
        active = false
        zombieRaidEvent.removeAllPlayers()
        status = ZombieRaidStatus.STOPPED
    }

    fun isStopped(): Boolean = status == ZombieRaidStatus.STOPPED
}