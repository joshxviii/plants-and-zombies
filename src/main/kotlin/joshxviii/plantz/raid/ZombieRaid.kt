package joshxviii.plantz.raid

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.ints.IntList
import joshxviii.plantz.*
import joshxviii.plantz.block.entity.FlagBlockEntity
import joshxviii.plantz.networking.ZombieRaidClientData
import joshxviii.plantz.networking.ZombieRaidResponsePayload
import net.minecraft.SharedConstants
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.server.level.ServerBossEvent
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.world.BossEvent
import net.minecraft.world.Difficulty
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.minecraft.world.level.levelgen.Heightmap
import java.util.function.Predicate
import java.util.Optional
import java.util.UUID

class ZombieRaid(
    val center: BlockPos,
    var started: Boolean = false,
    private var active: Boolean = false,
    var ticksActive: Long = 0,
    var zombieRaidOmenLevel: Int = 0,
    var wavesSpawned: Int = 0,
    var waveTypes: MutableList<WaveType> = mutableListOf(),
    var raidCooldownTicks: Int = PRE_RAID_TICKS,
    var postRaidTicks: Int = 0,
    var totalZombieHealth: Float = 0f,
    var numWaves: Int = 0,
    var waveTimer: Int = 0,
    var status: ZombieRaidStatus = ZombieRaidStatus.ONGOING,
    var startedBy: UUID? = null,
    var starterHasSeenCredits: Boolean = false,
    val difficulty: Difficulty = Difficulty.NORMAL,
) {
    companion object {
        private fun getVictoryFirework(): ItemStack {
            val rocket = ItemStack(Items.FIREWORK_ROCKET).apply { set(
                DataComponents.FIREWORKS,
                Fireworks(
                    1,
                    listOf(
                        FireworkExplosion(FireworkExplosion.Shape.BURST, IntList.of(0xF2C55C, 0xFFFD700), IntList.of(), false, true),
                    )
                )
            ) }
            return rocket
        }

        fun getWaveSpawnCount(difficulty: Difficulty, omenLevel: Int, creditsUnlocked: Boolean): Int {
            //if (creditsUnlocked && omenLevel >= 5) return 20 // Force Zomboss wave with lvl 5 omen and after credits are unlocked
            val baseWaves = 3 + difficulty.id
            val omenBonus = omenLevel.coerceAtLeast(1) * 2
            val creditsBonus = if (creditsUnlocked) 2 else 0
            return (baseWaves + omenBonus + creditsBonus).coerceAtMost(MAXIMUM_WAVE_COUNT)
        }
        fun getStartMessage(creditsUnlocked: Boolean): Component {
            return if (creditsUnlocked) ZOMBIE_RAID_BAR_START_CREDITS else ZOMBIE_RAID_BAR_START
        }
        val MAP_CODEC : MapCodec<ZombieRaid> = RecordCodecBuilder.mapCodec { r ->
            r.group(
                BlockPos.CODEC.fieldOf("center").forGetter<ZombieRaid> { it.center },
                Codec.BOOL.fieldOf("started").forGetter<ZombieRaid> { it.started },
                Codec.BOOL.fieldOf("active").forGetter<ZombieRaid> { it.active },
                Codec.LONG.fieldOf("ticks_active").forGetter<ZombieRaid> { it.ticksActive },
                Codec.INT.fieldOf("raid_omen_level").forGetter<ZombieRaid> { it.zombieRaidOmenLevel },
                Codec.INT.fieldOf("waves_spawned").forGetter<ZombieRaid> { it.wavesSpawned },
                WaveType.CODEC.listOf().fieldOf("wave_types").forGetter<ZombieRaid> { it.waveTypes },
                Codec.INT.fieldOf("cooldown_ticks").forGetter<ZombieRaid> { it.raidCooldownTicks },
                Codec.INT.fieldOf("post_raid_ticks").forGetter<ZombieRaid> { it.postRaidTicks },
                Codec.FLOAT.fieldOf("total_health").forGetter<ZombieRaid> { it.totalZombieHealth },
                Codec.INT.fieldOf("wave_count").forGetter<ZombieRaid> { it.numWaves },
                Codec.INT.fieldOf("wave_timer").forGetter<ZombieRaid> { it.waveTimer },
                ZombieRaidStatus.CODEC.fieldOf("status").forGetter<ZombieRaid> { it.status },
                Codec.STRING.xmap(UUID::fromString, UUID::toString)
                    .optionalFieldOf("started_by")
                    .xmap({ it.orElse(null) }, { Optional.ofNullable(it) })
                    .forGetter<ZombieRaid> { it.startedBy },
                Codec.BOOL.optionalFieldOf("starter_has_seen_credits", false).forGetter<ZombieRaid> { it.starterHasSeenCredits },
            ).apply<ZombieRaid>(r, ::ZombieRaid )
        }
        val ZOMBIE_RAID_BAR_START: Component = Component.translatable("event.plantz.zombie_raid.start").withStyle(ChatFormatting.GOLD)
        val ZOMBIE_RAID_BAR_START_CREDITS: Component = Component.translatable("event.plantz.zombie_raid.start.after_credits").withStyle(ChatFormatting.GOLD)
        val ZOMBIE_RAID_VICTORY: Component = Component.translatable("event.plantz.zombie_raid.victory").withStyle(ChatFormatting.YELLOW)
        val ZOMBIE_RAID_VICTORY_TITLE: Component = Component.translatable("event.plantz.zombie_raid.victory_title").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD)
        val ZOMBIE_RAID_DEFEAT: Component = Component.translatable("event.plantz.zombie_raid.defeat").withStyle(ChatFormatting.RED)
        val ZOMBIE_RAID_DEFEAT_TITLE: Component = Component.translatable("event.plantz.zombie_raid.defeat_title").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD)
        const val WAVE_DURATION_TICKS: Int = 3000 // 2.5 minutes
        const val PRE_RAID_TICKS: Int = 100
        const val POST_RAID_TICKS: Int = 80
        const val SPAWN_DISTANCE: Int = 96
        const val GARDEN_HERO_EFFECT_DURATION: Int = 72000
        const val COUNTDOWN_BEFORE_LOSS: Int = 200 //10 seconds
        const val MAXIMUM_WAVE_COUNT: Int = 20
        const val TACO_REWARD_INTERVAL = 5
    }

    private val waveToLeaderMap: MutableMap<Int, Zombie> = Maps.newHashMap<Int, Zombie>()
    val random: RandomSource = RandomSource.create()
    val zombieRaidEvent = ServerBossEvent(Mth.createInsecureUUID(random), ZOMBIE_RAID_BAR_START, BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10)
    private val waveZombieMap: MutableMap<Int, MutableSet<Zombie>> = Maps.newHashMap<Int, MutableSet<Zombie>>()
    var waveSpawnPos : BlockPos? = null

    enum class ZombieRaidStatus(private val state: String) : StringRepresentable {
        ONGOING("ongoing"),
        NEXT_WAVE("next_wave"),
        VICTORY("victory"),
        LOSS("loss"),
        STOPPED("stopped");

        override fun getSerializedName(): String = state

        companion object {
            val CODEC: Codec<ZombieRaidStatus> = StringRepresentable.fromEnum<ZombieRaidStatus> { entries.toTypedArray() }
        }
    }

    init {
        active = true
        zombieRaidEvent.progress = 0.0f
    }

    fun tick(level: ServerLevel) {
        updateRaid(level)
        if (ticksActive % 20L == 0L) {
            updatePlayers(level)
            updateZombieRaiders(level)
        }
        sendClientUpdate(level)
    }

    fun updateRaid(level: ServerLevel) {
        if (!active) return
        if (level.tickRateManager().isFrozen) return
        ticksActive++
        refreshRaidProfile(level)
        if (ticksActive == 1L) {
            if (numWaves>=MAXIMUM_WAVE_COUNT && starterHasSeenCredits) {
                zombieRaidEvent.players.forEach { player ->
                    PazCriteria.START_HARDEST_RAID.trigger(player, true)
                }
            }
        }

        if (SharedConstants.DEBUG_RAIDS) {// DEBUG INFO
            level.playSound(null, center, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.05f, 1.5f)

            level.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                center.x + 0.5,
                center.y + 0.5,
                center.z + 0.5,
                1, 0.1, 0.1, 0.1, 0.1
            )
        }

        if (postRaidTicks > 0) { postRaidTicks-- // post-loading time
            if (postRaidTicks <= 0) stop()
            if (status == ZombieRaidStatus.VICTORY) {
                if (ticksActive % 20L == 0L) {
                    val firework = getVictoryFirework()
                    Projectile.spawnProjectile(FireworkRocketEntity(level, firework, center.x.toDouble(), center.y.toDouble(), center.z.toDouble(), false), level, firework)
                }
            }
            return
        }

        if (
            ((getTotalZombiesAlive() == 0 && raidCooldownTicks > 0) ||
                    (waveTimer == 0 && wavesSpawned < numWaves)) && wavesSpawned < numWaves
        ) {
            raidCooldownTicks-- // pre-loading time
            waveTimer = 0
            if (raidCooldownTicks <= 0) {
                status = ZombieRaidStatus.ONGOING
                raidCooldownTicks = PRE_RAID_TICKS
            }
            else {
                status = ZombieRaidStatus.NEXT_WAVE
                return
            }
        }

        if (waveTimer>0) {
            waveTimer--
            if (isFinalWave() && waveTimer<=COUNTDOWN_BEFORE_LOSS+20 && waveTimer%20==19) {// count down before loss
                showTitleMessage(Component.literal(waveTimer.tickSecondFormat()).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD))
            }
        }

        // victory condition (all zombies dead and all waves completed)
        if (getTotalZombiesAlive() == 0 && isFinalWave()) {
            status = ZombieRaidStatus.VICTORY
            postRaidTicks = POST_RAID_TICKS
            zombieRaidEvent.players.forEach { player ->// advancement
                PazCriteria.WIN_ZOMBIE_RAID.trigger(player, true)
                val effect = MobEffectInstance(PazEffects.GARDEN_HERO, GARDEN_HERO_EFFECT_DURATION, (waveTypes.size-1).coerceAtLeast(0), false, true)
                (player as GardenHeroRewards).`plantz$setWaveList`(waveTypes)
                player.addEffect(effect)
                player.sendSystemMessage(ZOMBIE_RAID_VICTORY)
                showTitleMessage(ZOMBIE_RAID_VICTORY_TITLE)
            }
            return
        }
        // lose condition (flag destroyed)
        else if (!level.getBlockState(center).`is`(PazBlocks.PLANTZ_FLAG)) {
            status = ZombieRaidStatus.LOSS
            postRaidTicks = POST_RAID_TICKS
            zombieRaidEvent.players.forEach { player ->
                player.sendSystemMessage(ZOMBIE_RAID_DEFEAT)
                showTitleMessage(ZOMBIE_RAID_DEFEAT_TITLE)
            }
            return
        }

        if (shouldSpawnNextWave()) {
            val spawnPos = findRandomSpawnPos(level, 20) ?: center
            spawnNextWave(level, spawnPos)
            zombieRaidEvent.players.forEach { player ->
                PazCriteria.RAID_WAVE_TRIGGER.trigger(player, wavesSpawned)
            }
        }
        else if (waveTimer <= 0) {// destroy flag when timer runs out
            (level.getBlockEntity(center) as? FlagBlockEntity)?.hurt(999f)
        }
    }

    fun isFinalWave(): Boolean = wavesSpawned >= numWaves

    private fun validPlayer(): Predicate<ServerPlayer> {
        return Predicate { player: ServerPlayer ->
            val pos = player.blockPosition()
            player.isAlive && player.level().getZombieRaids().getNearbyRaid(pos, 9216) === this
        }
    }

    private fun updatePlayers(level: ServerLevel) {
        val currentPlayersInRaid: MutableSet<ServerPlayer> = Sets.newHashSet<ServerPlayer>(zombieRaidEvent.players)
        val newPlayersInRaid = level.getPlayers(validPlayer())
        for (player in newPlayersInRaid) if (!currentPlayersInRaid.contains(player)) {
            zombieRaidEvent.addPlayer(player)
            sendClientUpdate(level)
        }
        for (player in currentPlayersInRaid) if (!newPlayersInRaid.contains(player)) {
            sendClientUpdate(level, true)
            zombieRaidEvent.removePlayer(player)
        }
    }

    private fun updateZombieRaiders(level: ServerLevel) {
        val zombies: MutableIterator<MutableSet<Zombie>> = waveZombieMap.values.iterator()
        val toRemove: HashSet<Zombie> = Sets.newHashSet<Zombie>()
        while (zombies.hasNext()) {
            val zombieSet = zombies.next()
            for (zombie in zombieSet) {
                val zombiePos = zombie.blockPosition()
                if (zombie.isRemoved || zombie.level().dimension() !== level.dimension() || this.center.distSqr(
                        zombiePos
                    ) >= 12544.0
                ) {
                    toRemove.add(zombie)
                    continue
                }
                if (zombie.tickCount <= 600) continue
                if (level.getEntity(zombie.getUUID()) == null) toRemove.add(zombie)
            }
        }
        for (zombie in toRemove) removeFromRaid(level, zombie, true)
    }

    fun absorbRaidOmen(player: ServerPlayer): Boolean {
        val effect = player.getEffect(PazEffects.ZOMBIE_OMEN)?: return false
        zombieRaidOmenLevel += effect.amplifier+1
        starterHasSeenCredits = starterHasSeenCredits || player.seenCredits
        return true
    }

    private fun spawnNextWave(level: ServerLevel, pos: BlockPos) {
        waveTimer = WAVE_DURATION_TICKS
        var leaderSet = false
        totalZombieHealth = 0.0f
        val creditsUnlocked = hasRaidStarterSeenCredits(level)
        val waveType = pickWaveType(creditsUnlocked)
        if (waveType != WaveType.DEFAULT) announceSpecialWave(waveType)
        waveTypes = waveTypes.toMutableList()
        waveTypes.add(waveType)
        val waveEntries = waveType.spawnEntries(this, creditsUnlocked)

        for ((type, count, configure) in waveEntries) {
            for (i in 0..<count) {
                val zombie = type.create(level, EntitySpawnReason.EVENT) ?: break
                if (!leaderSet) {
                    setLeader(wavesSpawned + 1, zombie)
                    leaderSet = true
                }
                val randX = pos.x + random.nextInt(11) - 5
                val randZ = pos.z + random.nextInt(11) - 5
                val randY = level.getHeight(Heightmap.Types.OCEAN_FLOOR, randX, randZ)
                val randomPos = MutableBlockPos(randX,randY,randZ)
                joinRaid(level, zombie, wavesSpawned + 1, false, randomPos, configure)
            }
        }

        wavesSpawned++
    }

    fun joinRaid(
        level: ServerLevel,
        zombie: Zombie,
        waveNumber: Int = wavesSpawned,
        exists: Boolean = true,
        pos: BlockPos? = null,
        configure: (Zombie) -> Unit = {},
    ) {
        if (status != ZombieRaidStatus.ONGOING) return
        val added = addWaveMob(zombie, waveNumber, level)
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
                zombie.setPersistenceRequired()
                configure(zombie)
                level.addFreshEntityWithPassengers(zombie)
            }
        }
    }

    fun removeFromRaid(level: ServerLevel, zombie: Zombie, removeFromTotalHealth: Boolean = true) {
        for (zombies in waveZombieMap.values) {
            if (zombies.remove(zombie)) {
                if (removeFromTotalHealth) totalZombieHealth -= zombie.health
                (zombie as? ZombieRaider)?.`plantz$setIsFromRaid`(false)
                sendClientUpdate(level)
                setDirty(level)
                break
            }
        }
    }
    private fun setDirty(level: ServerLevel) { level.getZombieRaids().setDirty() }

    fun setLeader(wave: Int, zombie: Zombie) {
        waveToLeaderMap[wave] = zombie
        zombie.setItemSlot(EquipmentSlot.OFFHAND, PazBlocks.BRAINZ_FLAG.asItem().defaultInstance)
        zombie.setDropChance(EquipmentSlot.OFFHAND, 0.0f)
    }
    fun getLeader(wave: Int): Zombie? = waveToLeaderMap[wave]

    fun addWaveMob(zombie: Zombie, wave: Int = wavesSpawned, level: ServerLevel): Boolean {
        waveZombieMap.computeIfAbsent(wave) { Sets.newHashSet<Zombie>() }
        val zombies = waveZombieMap[wave] as MutableSet<Zombie>
        if (zombies.contains(zombie)) return false
        var existingCopy: Zombie? = null

        zombies.firstOrNull { it.getUUID() == zombie.getUUID() }?.let { existingCopy = it }

        if (existingCopy != null) zombies.remove(existingCopy)

        zombies.add(zombie)
        totalZombieHealth += zombie.maxHealth
        (zombie as? ZombieRaider)?.`plantz$setIsFromRaid`(true)

        sendClientUpdate(level)
        return true
    }

    fun sendClientUpdate(level: ServerLevel, terminate: Boolean = false) {
        val flag = level.getBlockEntity(center) as? FlagBlockEntity
        val data = ZombieRaidClientData(
            id = zombieRaidEvent.id,
            status = status,
            currentWaveType = waveTypes.lastOrNull()?: WaveType.DEFAULT,
            wavesSpawned = wavesSpawned,
            activeTime = ticksActive.toInt(),
            numWaves = numWaves,
            waveTimer = waveTimer,
            zombieHealthMax = if (status != ZombieRaidStatus.NEXT_WAVE) totalZombieHealth else 1f,
            zombieHealth = getHealthOfZombies(),
            flagHealth = flag?.health ?: 0f,
            seenCredits = starterHasSeenCredits
        )

        val packet = ZombieRaidResponsePayload(data, terminate)
        zombieRaidEvent.players.forEach { player ->
            player.connection.send(ClientboundCustomPayloadPacket(packet))
        }
    }

    fun getTotalZombiesAlive(): Int = waveZombieMap.values.stream().mapToInt { it.map { z -> if (z.isAlive) 1 else 0 }.sum() }.sum()
    fun getHealthOfZombies(): Float =
        if (status != ZombieRaidStatus.NEXT_WAVE)// zombie health bar
            waveZombieMap.values.map { it.map { z -> if (z.isAlive) z.health else 0f }.sum() }.sum()
        else// next wave loading bar
            1f - (raidCooldownTicks.toFloat() / PRE_RAID_TICKS.toFloat())

    private fun shouldSpawnNextWave(): Boolean {
        return (getTotalZombiesAlive() == 0 || waveTimer == 0)
            && status == ZombieRaidStatus.ONGOING
            && !isFinalWave()
    }

    private fun refreshRaidProfile(level: ServerLevel) {
        if (!starterHasSeenCredits) starterHasSeenCredits = hasRaidStarterSeenCredits(level)
        numWaves = getWaveSpawnCount(difficulty, zombieRaidOmenLevel, starterHasSeenCredits)
    }

    private fun hasRaidStarterSeenCredits(level: ServerLevel): Boolean {
        val starter = startedBy ?: return starterHasSeenCredits
        val player = level.server.playerList.getPlayer(starter) ?: return starterHasSeenCredits
        return starterHasSeenCredits || player.seenCredits
    }

    private fun announceSpecialWave(wave: WaveType) {
        val title: Component = Component.translatable("event.plantz.zombie_raid.special_wave.title").apply { withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD) }
        val subtitle: Component = wave.popupMessage().copy().apply { withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD) }
        showTitleMessage(title, subtitle)
    }

    private fun showTitleMessage(title: Component, subtitle: Component = Component.empty(), sound: Holder.Reference<SoundEvent> = PazSounds.SPECIAL_WAVE) {
        zombieRaidEvent.players.forEach {
            it.connection.send(ClientboundSoundPacket(sound, SoundSource.UI, it.x, it.y, it.z, 1.0f, 1.0f, random.nextLong()))
            it.connection.send(ClientboundSetTitleTextPacket(title))
            it.connection.send(ClientboundSetSubtitleTextPacket(subtitle))
        }
    }

    private fun pickWaveType(creditsUnlocked: Boolean): WaveType {
        val eligible = WaveType.entries.filter { it.isAvailable(this, creditsUnlocked) }
        val totalWeight = eligible.sumOf { it.weight(this, creditsUnlocked).toDouble() }.toFloat()
        if (totalWeight <= 0f) return WaveType.DEFAULT

        val roll = random.nextFloat() * totalWeight
        var runningWeight = 0f
        for (wave in eligible) {
            runningWeight += wave.weight(this, creditsUnlocked)
            if (roll < runningWeight) return wave
        }
        return WaveType.DEFAULT
    }

    private fun findRandomSpawnPos(level: ServerLevel, maxTries: Int): BlockPos? {
        val spawnPos = MutableBlockPos()
        val startAngle = random.nextFloat() * (Math.PI * 2).toFloat()

        for (i in 0..<maxTries) {
            val angle = startAngle + Math.PI.toFloat() * i / 8.0f
            val spawnX = center.x + Mth.floor(Mth.cos(angle.toDouble()) * SPAWN_DISTANCE)
            val spawnZ = center.z + Mth.floor(Mth.sin(angle.toDouble()) * SPAWN_DISTANCE)
            var spawnY = level.getHeight(Heightmap.Types.WORLD_SURFACE, spawnX, spawnZ)
            if (level.getFluidState(BlockPos(spawnX, spawnY + 1, spawnZ)).`is`(FluidTags.WATER)) {
                while (level.getFluidState(BlockPos(spawnX, spawnY + 1, spawnZ)).`is`(FluidTags.WATER)) spawnY++
                spawnY++
            }
            spawnPos.set(spawnX, spawnY, spawnZ)
            if (level.isPositionEntityTicking(spawnPos)) return spawnPos
        }
        return null
    }

    fun stop() {
        active = false
        val data = ZombieRaidClientData(id = zombieRaidEvent.id)
        status = ZombieRaidStatus.STOPPED
        zombieRaidEvent.players.forEach { player ->// terminate raid connection
            player.connection.send(ClientboundCustomPayloadPacket(ZombieRaidResponsePayload(data, true)))
        }
        zombieRaidEvent.removeAllPlayers()
    }
    fun isStopped(): Boolean = status == ZombieRaidStatus.STOPPED
}