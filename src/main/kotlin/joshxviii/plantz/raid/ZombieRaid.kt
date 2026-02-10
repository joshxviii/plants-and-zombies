package joshxviii.plantz.raid

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerBossEvent
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.world.BossEvent
import net.minecraft.world.Difficulty
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.raid.Raid
import net.minecraft.world.entity.raid.Raider
import net.minecraft.world.level.levelgen.Heightmap

class ZombieRaid(
    val center: BlockPos,
    var started: Boolean = false,
    var active: Boolean = false,
    var ticksActive: Long = 0,
    var zombieRaidOmenLevel: Int = 0,
    var wavesSpawned: Int = 0,
    var raidCooldownTicks: Int = PRE_RAID_TICKS,
    var postRaidTicks: Int = 0,
    var totalHealth: Float = 0f,
    var numWaves: Int = 0,
    var status: ZombieRaidStatus = ZombieRaidStatus.ONGOING,
    val difficulty: Difficulty = Difficulty.NORMAL,
) {
    companion object {
        fun getWaveSpawnCount(difficulty: Difficulty): Int = 3 + difficulty.id * 2
        val MAP_CODEC : MapCodec<ZombieRaid> = RecordCodecBuilder.mapCodec { r ->
            r.group(
                BlockPos.CODEC.fieldOf("center").forGetter<ZombieRaid> { it.center },
                Codec.BOOL.fieldOf("started").forGetter<ZombieRaid> { it.started },
                Codec.BOOL.fieldOf("active").forGetter<ZombieRaid> { it.active },
                Codec.LONG.fieldOf("ticks_active").forGetter<ZombieRaid> { it.ticksActive },
                Codec.INT.fieldOf("raid_omen_level").forGetter<ZombieRaid> { it.zombieRaidOmenLevel },
                Codec.INT.fieldOf("waves_spawned").forGetter<ZombieRaid> { it.wavesSpawned },
                Codec.INT.fieldOf("cooldown_ticks").forGetter<ZombieRaid> { it.raidCooldownTicks },
                Codec.INT.fieldOf("post_raid_ticks").forGetter<ZombieRaid> { it.postRaidTicks },
                Codec.FLOAT.fieldOf("total_health").forGetter<ZombieRaid> { it.totalHealth },
                Codec.INT.fieldOf("wave_count").forGetter<ZombieRaid> { it.numWaves },
                ZombieRaidStatus.CODEC.fieldOf("status").forGetter<ZombieRaid> { it.status },
            ).apply<ZombieRaid>(r, ::ZombieRaid )
        }
        val ZOMBIE_RAID_BAR: Component = Component.translatable("event.plantz.zombie_raid")
        val ZOMBIE_RAID_BAR_START: Component = Component.translatable("event.plantz.zombie_raid.start")
        val ZOMBIE_RAID_BAR_VICTORY: Component = Component.translatable("event.plantz.zombie_raid.victory")
        val ZOMBIE_RAID_BAR_DEFEAT: Component = Component.translatable("event.plantz.zombie_raid.defeat")
        const val PRE_RAID_TICKS: Int = 300
        const val POST_RAID_TICKS: Int = 80
    }

    private val waveToLeaderMap: MutableMap<Int, Zombie> = Maps.newHashMap<Int, Zombie>()
    val zombieRaidEvent = ServerBossEvent(ZOMBIE_RAID_BAR_START, BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10)
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

        level.playSound(null, center, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.4f, 0.4f)

        if (postRaidTicks > 0) { postRaidTicks-- // post-loading time
            if (postRaidTicks <= 0) stop()
            return
        }
        if (raidCooldownTicks > 0) { raidCooldownTicks-- // pre-loading time
            zombieRaidEvent.progress += 1f/PRE_RAID_TICKS
            if (raidCooldownTicks <= 0) zombieRaidEvent.name = ZOMBIE_RAID_BAR
            return
        }

        if (shouldSpawnNextWave()) {
            val spawnPos = findRandomSpawnPos(level, 20) ?: center
            spawnNextWave(level, spawnPos)
            //raidCooldownTicks = 200 + random.nextInt(200)  // Delay next wave
        }

        updateBossbar()

        // victory condition
        if (getTotalZombiesAlive() == 0 && wavesSpawned >= numWaves) {
            status = ZombieRaidStatus.VICTORY
            zombieRaidEvent.progress = 1.0f
            zombieRaidEvent.color = BossEvent.BossBarColor.YELLOW
            zombieRaidEvent.name = ZOMBIE_RAID_BAR_VICTORY
            postRaidTicks = POST_RAID_TICKS
        }
        // lose condition
        if (!level.getBlockState(center).`is`(PazBlocks.PLANTZ_FLAG)) {
            status = ZombieRaidStatus.LOSS
            zombieRaidEvent.progress = 1.0f
            zombieRaidEvent.color = BossEvent.BossBarColor.RED
            zombieRaidEvent.name = ZOMBIE_RAID_BAR_DEFEAT
            postRaidTicks = POST_RAID_TICKS
        }

        zombieRaidEvent.players.forEach { p ->
            //if (p.blockPosition().distSqr(center) > 96) zombieRaidEvent.removePlayer(p)
        }

    }

    fun absorbRaidOmen(player: ServerPlayer): Boolean {
        val effect = player.getEffect(PazEffects.ZOMBIE_OMEN)?: return false
        zombieRaidOmenLevel += effect.amplifier + 1
        return true
    }


    private fun spawnNextWave(level: ServerLevel, pos: BlockPos) {
//        for (i in 0..getWaveSpawnCount(difficulty)) {
//
//            val tag = BuiltInRegistries.ENTITY_TYPE.get(ZOMBIE_RAIDERS)
//            val entityType = if (!tag.isEmpty) tag.get().getRandomElement(level.random).get().value() else return
//
//            val entity = entityType.create(level, EntitySpawnReason.EVENT) ?: continue
//            entity.moveOrInterpolateTo(Vec3(pos.x + 0.5, pos.y.toDouble(), pos.z + 0.5), level.random.nextFloat() * 360f, 0f)
//            if(level.addFreshEntity(entity) && entity is Zombie) {
//                addWaveMob(level, i, entity)
//            }
//        }
        //totalHealth = 0f


        var leaderSet = false
        val waveNumber = wavesSpawned + 1
        for (zombieType in ZombieRaiderType.VALUES) {
            val numSpawns = zombieType.spawnsPerWaveBeforeBonus[waveNumber] + getPotentialBonusSpawns(zombieType, random)

            for (i in 0..<numSpawns) {
                val zombie = zombieType.entityType.create(level, EntitySpawnReason.EVENT) ?: break

                if (!leaderSet) {
                    setLeader(numWaves, zombie)
                    leaderSet = true
                }

                joinRaid(level, waveNumber, zombie, pos, false)
            }
        }
        wavesSpawned++
    }

    fun joinRaid(level: ServerLevel, waveNumber: Int, zombie: Zombie, pos: BlockPos?, exists: Boolean) {
        val added = addWaveMob(level, waveNumber, zombie)
        if (added) {
            if (!exists && pos != null) {
                zombie.setPos(pos.x.toDouble() + 0.5, pos.y.toDouble() + 1.0, pos.z.toDouble() + 0.5)
                zombie.finalizeSpawn(
                    level,
                    level.getCurrentDifficultyAt(pos),
                    EntitySpawnReason.EVENT,
                    null as SpawnGroupData?
                )
                zombie.setOnGround(true)
                level.addFreshEntityWithPassengers(zombie)
            }
        }
    }

    fun setLeader(wave: Int, zombie: Zombie) {
        waveToLeaderMap[wave] = zombie
        zombie.setItemSlot(EquipmentSlot.OFFHAND, PazBlocks.BRAINZ_FLAG.asItem().defaultInstance)
        zombie.setDropChance(EquipmentSlot.OFFHAND, 2.0f)
    }
    fun getLeader(wave: Int): Zombie? = waveToLeaderMap[wave]

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
        totalHealth += zombie.maxHealth

        updateBossbar()
        return true
    }

    fun updateBossbar() {
        zombieRaidEvent.setProgress(Mth.clamp(getHealthOfZombies() / totalHealth, 0.0f, 1.0f))
    }

    fun getTotalZombiesAlive(): Int = waveZombieMap.values.stream().mapToInt { it.map { if (it.isAlive) 1 else 0 }.sum() }.sum()
    fun getHealthOfZombies(): Float = waveZombieMap.values.map { it.map { if (it.isAlive) it.health else 0f }.sum() }.sum()

    private fun shouldSpawnNextWave(): Boolean {
        return getTotalZombiesAlive() == 0
            && status == ZombieRaidStatus.ONGOING
            && wavesSpawned < numWaves
    }


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

    private fun getDefaultNumSpawns(type: ZombieRaiderType): Int {
        return type.spawnsPerWaveBeforeBonus[numWaves]
    }

    private fun getPotentialBonusSpawns(
        type: ZombieRaiderType,
        random: RandomSource,
    ): Int {
        val isEasy = difficulty == Difficulty.EASY
        val isNormal = difficulty == Difficulty.NORMAL
        val wave = wavesSpawned
        val bonusSpawns: Int
        when (type) {
            ZombieRaiderType.BROWN_COAT -> bonusSpawns =
                if (isEasy) random.nextInt(2)
                else if (isNormal) 3
                else 4
            ZombieRaiderType.NEWSPAPER_ZOMBIE -> return 0
            ZombieRaiderType.DIGGER_ZOMBIE -> return 0
            ZombieRaiderType.DISCO_ZOMBIE -> {
                if (isEasy || wave <= 2 || wave == 4) return 0
                bonusSpawns = 1
            }

            ZombieRaiderType.ZOMBIE_YETI -> bonusSpawns = if (!isEasy && wave > 3) 1 else 0
            ZombieRaiderType.GARGANTUAR -> return 0
        }

        return if (bonusSpawns > 0) random.nextInt(bonusSpawns + 1) else 0
    }

    enum class ZombieRaiderType(
        val entityType: EntityType<out Zombie>,
        val spawnsPerWaveBeforeBonus: IntArray
    ) {
        BROWN_COAT(PazEntities.BROWN_COAT, intArrayOf(5, 5, 7, 9, 9, 12, 16, 19)),
        NEWSPAPER_ZOMBIE(PazEntities.NEWSPAPER_ZOMBIE, intArrayOf(0, 1, 0, 1, 0, 1, 2, 1)),
        DIGGER_ZOMBIE(PazEntities.DIGGER_ZOMBIE, intArrayOf(0, 0, 1, 0, 4, 1, 1, 2)),
        DISCO_ZOMBIE(PazEntities.DISCO_ZOMBIE, intArrayOf(0, 0, 1, 3, 4, 4, 4, 2)),
        ZOMBIE_YETI(PazEntities.ZOMBIE_YETI, intArrayOf(0, 0, 0, 1, 3, 0, 2, 1)),
        GARGANTUAR(PazEntities.GARGANTUAR, intArrayOf(0, 0, 0, 1, 0, 1, 0, 2));

        companion object {
            val VALUES = entries.toTypedArray()
        }
    }
}