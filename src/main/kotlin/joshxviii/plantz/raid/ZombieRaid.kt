package joshxviii.plantz.raid

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import it.unimi.dsi.fastutil.ints.IntList
import joshxviii.plantz.*
import joshxviii.plantz.block.entity.FlagBlockEntity
import joshxviii.plantz.entity.RaiderRecord
import joshxviii.plantz.entity.ZombieRaider
import joshxviii.plantz.entity.getEffectiveRaidHealth
import joshxviii.plantz.entity.isBoss
import joshxviii.plantz.entity.isLeader
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.networking.ZombieRaidClientData
import joshxviii.plantz.networking.ZombieRaidResponsePayload
import net.minecraft.ChatFormatting
import net.minecraft.SharedConstants
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.server.level.ServerBossEvent
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.world.BossEvent
import net.minecraft.world.Difficulty
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
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.AABB
import java.util.*
import java.util.function.Predicate
import kotlin.math.abs

class ZombieRaid(
    val center: BlockPos,
    var ticksActive: Long = 0,
    var zombieRaidOmenLevel: Float = 0f,
    var waveTypes: MutableList<WaveType> = mutableListOf(),
    var raidCooldownTicks: Int = PRE_RAID_TICKS,
    var postRaidTicks: Int = 0,
    var totalZombieHealth: Float = 0f,
    var totalBossHealth: Float = 0f,
    var numWaves: Int = 0,
    var waveTimer: Int = 0,
    var status: ZombieRaidStatus = ZombieRaidStatus.INIT,
    var startedBy: UUID? = null,
    var starterHasSeenCredits: Boolean = false,
    savedRaiders: List<RaiderRecord> = listOf(),
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

        fun getWaveSpawnCount(difficulty: Difficulty, raid: ZombieRaid): Int {
            //if (creditsUnlocked && omenLevel >= 5) return 20 // Force Zomboss wave with lvl 5 omen and after credits are unlocked
            val baseWaves = 2 + difficulty.id
            val omenBonus = Mth.ceil(raid.zombieRaidOmenLevel * 2)
            val creditsBonus = if (raid.starterHasSeenCredits) 2 else 0
            return (baseWaves + omenBonus + creditsBonus).coerceAtMost(MAXIMUM_WAVE_COUNT)
        }
        fun getStartMessage(creditsUnlocked: Boolean): Component {
            return if (creditsUnlocked) ZOMBIE_RAID_BAR_START_CREDITS else ZOMBIE_RAID_BAR_START
        }
        val MAP_CODEC : MapCodec<ZombieRaid> = RecordCodecBuilder.mapCodec { r ->
            r.group(
                BlockPos.CODEC.fieldOf("center").forGetter<ZombieRaid> { it.center },
                Codec.LONG.fieldOf("ticks_active").forGetter<ZombieRaid> { it.ticksActive },
                Codec.FLOAT.fieldOf("raid_omen_level").forGetter<ZombieRaid> { it.zombieRaidOmenLevel },
                WaveType.CODEC.listOf().fieldOf("wave_types").forGetter<ZombieRaid> { it.waveTypes },
                Codec.INT.fieldOf("cooldown_ticks").forGetter<ZombieRaid> { it.raidCooldownTicks },
                Codec.INT.fieldOf("post_raid_ticks").forGetter<ZombieRaid> { it.postRaidTicks },
                Codec.FLOAT.fieldOf("total_health").forGetter<ZombieRaid> { it.totalZombieHealth },
                Codec.FLOAT.fieldOf("total_boss").forGetter<ZombieRaid> { it.totalBossHealth },
                Codec.INT.fieldOf("wave_count").forGetter<ZombieRaid> { it.numWaves },
                Codec.INT.fieldOf("wave_timer").forGetter<ZombieRaid> { it.waveTimer },
                ZombieRaidStatus.CODEC.fieldOf("status").forGetter<ZombieRaid> { it.status },
                Codec.STRING.xmap(UUID::fromString, UUID::toString)
                    .optionalFieldOf("started_by")
                    .xmap({ it.orElse(null) }, { Optional.ofNullable(it) })
                    .forGetter<ZombieRaid> { it.startedBy },
                Codec.BOOL.optionalFieldOf("starter_has_seen_credits", false).forGetter<ZombieRaid> { it.starterHasSeenCredits },
                RaiderRecord.CODEC.listOf().optionalFieldOf("raiders", listOf()).forGetter<ZombieRaid> { it.raiderRecords() },
            ).apply<ZombieRaid>(r, ::ZombieRaid )
        }
        val ZOMBIE_RAID_BAR_START: Component = Component.translatable("event.plantz.zombie_raid.start").withStyle(ChatFormatting.GOLD)
        val ZOMBIE_RAID_BAR_START_CREDITS: Component = Component.translatable("event.plantz.zombie_raid.start.after_credits").withStyle(ChatFormatting.GOLD)
        val ZOMBIE_RAID_VICTORY: Component = Component.translatable("event.plantz.zombie_raid.victory").withStyle(ChatFormatting.GOLD)
        val ZOMBIE_RAID_VICTORY_TITLE: Component = Component.translatable("event.plantz.zombie_raid.victory_title").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD)
        val ZOMBIE_RAID_DEFEAT: Component = Component.translatable("event.plantz.zombie_raid.defeat").withStyle(ChatFormatting.RED)
        val ZOMBIE_RAID_DEFEAT_TITLE: Component = Component.translatable("event.plantz.zombie_raid.defeat_title").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD)
        const val WAVE_DURATION: Int = 3000 // 2.5 minutes
        const val BOSS_WAVE_DURATION: Int = 4500 // 3.75 minutes
        const val FINAL_WAVE_DURATION: Int = WAVE_DURATION * 2 // 5 minutes

        const val FLAG_MAX_HEALTH: Int = 400

        const val PRE_RAID_TICKS: Int = 100
        const val POST_RAID_TICKS: Int = 80
        const val SPAWN_DISTANCE: Int = 88
        const val MAX_SPAWN_HEIGHT_DIFFERENCE: Int = 32
        const val RAID_DISTANCE: Int = 160
        const val GARDEN_HERO_EFFECT_DURATION: Int = 72000
        const val COUNTDOWN_BEFORE_LOSS: Int = 200 //10 seconds
        const val MAXIMUM_WAVE_COUNT: Int = 20
        const val BONUS_REWARD_INTERVAL = 5
    }

    val random: RandomSource = RandomSource.create()
    val zombieRaidEvent = ServerBossEvent(Mth.createInsecureUUID(random), ZOMBIE_RAID_BAR_START, BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10)
    private val waveZombieMap: MutableMap<Int, MutableSet<Zombie>> = Maps.newHashMap<Int, MutableSet<Zombie>>()
    // raiders that are part of the raid but not currently loaded in the world (unloaded chunk or not yet loaded after a restart)
    private val pendingRaiders: MutableMap<UUID, RaiderRecord> = savedRaiders.associateByTo(Maps.newHashMap()) { it.uuid }

    enum class ZombieRaidStatus(private val state: String) : StringRepresentable {
        INIT("init"),
        ONGOING("ongoing"),
        NEXT_WAVE("next_wave"),
        VICTORY("victory"),
        LOSS("loss"),
        STOPPED("stopped"),
        TERMINATE("terminate");

        override fun getSerializedName(): String = state

        companion object {
            val CODEC: Codec<ZombieRaidStatus> = StringRepresentable.fromEnum<ZombieRaidStatus> { entries.toTypedArray() }
        }
    }

    init {
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
        if (!isStopped()) (level.getBlockEntity(center) as? FlagBlockEntity)
            ?.setMaxHealth(isRaid = true, refill = status == ZombieRaidStatus.INIT)

        if (status == ZombieRaidStatus.INIT) vanishAllWildPlants(level)

        if (isStopped()) return
        if (level.tickRateManager().isFrozen) return
        ticksActive++
        refreshRaidProfile(level)

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
            ((getAliveRaiders() == 0 && raidCooldownTicks > 0) || waveTimer == 0) && !isFinalWave()
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
            if ((isFinalWave() || getBosses().isNotEmpty()) && (waveTimer<=COUNTDOWN_BEFORE_LOSS+20 && waveTimer%20==19 || waveTimer==600)) {// count down before loss
                showTitleMessage(Component.literal(waveTimer.tickSecondFormat()).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD))
            }
        }

        // victory condition (all zombies dead and all waves completed)
        if (getAliveRaiders() == 0 && isFinalWave()) {
            status = ZombieRaidStatus.VICTORY
            postRaidTicks = POST_RAID_TICKS
            zombieRaidEvent.players.forEach { player ->// advancement
                PazCriteria.WIN_ZOMBIE_RAID.trigger(player, true)
                PazCriteria.WIN_HARDEST_RAID.trigger(player, numWaves>=MAXIMUM_WAVE_COUNT && starterHasSeenCredits)
                val effect = MobEffectInstance(PazEffects.GARDEN_HERO, GARDEN_HERO_EFFECT_DURATION, (waveTypes.size-1).coerceAtLeast(0), false, true)
                (player as GardenHeroRewards).`plantz$setWaveList`(waveTypes)
                player.addEffect(effect)
                player.sendSystemMessage(ZOMBIE_RAID_VICTORY)
                showTitleMessage(ZOMBIE_RAID_VICTORY_TITLE, sound = PazSounds.RAID_VICTORY, soundSource = SoundSource.MUSIC)
            }
            vanishAllZombies(level)
            return
        }
        // lose condition (flag destroyed)
        else if (!level.getBlockState(center).`is`(PazBlocks.PLANTZ_FLAG)) {
            status = ZombieRaidStatus.LOSS
            postRaidTicks = POST_RAID_TICKS
            zombieRaidEvent.players.forEach { player ->
                player.sendSystemMessage(ZOMBIE_RAID_DEFEAT)
                showTitleMessage(ZOMBIE_RAID_DEFEAT_TITLE, sound = PazSounds.RAID_DEFEAT, soundSource = SoundSource.MUSIC)
            }
            vanishAllZombies(level)
            return
        }

        if (shouldSpawnNextWave()) {
            val spawnPos = findRandomSpawnPos(level, 20) ?: return
            spawnNextWave(level, spawnPos)
            zombieRaidEvent.players.forEach { player ->
                PazCriteria.RAID_WAVE_TRIGGER.trigger(player, currentWaveNumber())
            }
        }
        else if (waveTimer <= 0) {// destroy flag when timer runs out
            (level.getBlockEntity(center) as? FlagBlockEntity)?.destroy()
        }
    }

    fun isNear(level: ServerLevel, pos: BlockPos): Boolean {
        return level.getZombieRaids().getNearbyRaid(pos, RAID_DISTANCE) === this
    }

    private fun validPlayer(): Predicate<ServerPlayer> {
        return Predicate { player: ServerPlayer ->
            player.isAlive && isNear(player.level(), player.blockPosition())
        }
    }

    private fun updatePlayers(level: ServerLevel) {
        val currentPlayersInRaid: MutableSet<ServerPlayer> = Sets.newHashSet<ServerPlayer>(zombieRaidEvent.players)
        val newPlayersInRaid = level.getPlayers( validPlayer() )
        for (player in newPlayersInRaid) if (!currentPlayersInRaid.contains(player)) {
            zombieRaidEvent.addPlayer(player)
            sendClientUpdate(level)
        }
        for (player in currentPlayersInRaid) if (!newPlayersInRaid.contains(player)) {
            sendClientUpdate(level, true)
            zombieRaidEvent.removePlayer(player)
        }

        zombieRaidEvent.players.filter { it.hasEffect(PazEffects.ZOMBIE_OMEN) }.forEach(::absorbRaidOmen)
    }

    private fun updateZombieRaiders(level: ServerLevel) {
        // relink raiders that have been loaded back into the world
        val pending = pendingRaiders.values.iterator()
        while (pending.hasNext()) {
            val record = pending.next()
            val entity = level.getEntity(record.uuid)
            if (entity is Zombie && entity.isAlive) {
                pending.remove()
                addWaveMob(entity, record.wave, level, false)
                RaiderRecord.loadInto(entity, record)
                setDirty(level)
            }
            else if (level.areEntitiesLoaded(ChunkPos.containing(record.pos).pack())) {// chunk is loaded but the zombie is gone
                pending.remove()
                totalZombieHealth -= record.health
                if (record.isBoss) totalBossHealth -= record.health
                setDirty(level)
            }
        }

        val zombies: MutableIterator<Map.Entry<Int, MutableSet<Zombie>>> = waveZombieMap.entries.iterator()
        val toRemove: HashSet<Zombie> = Sets.newHashSet<Zombie>()
        while (zombies.hasNext()) {
            val (wave, zombieSet) = zombies.next()
            val unloaded = zombieSet.filter { it.isRemoved && it.removalReason?.shouldSave() == true }
            for (zombie in unloaded) {// keep track of raiders in unloaded chunks until they load back in
                zombieSet.remove(zombie)
                pendingRaiders[zombie.uuid] = RaiderRecord.save(zombie, wave)
            }
            for (zombie in zombieSet) {
                val zombiePos = zombie.blockPosition()
                if (zombie.isRemoved || zombie.level().dimension() !== level.dimension() || this.center.distSqr(
                        zombiePos
                    ) >= RAID_DISTANCE * RAID_DISTANCE * 2
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
        val amp = effect.amplifier+1f
        val value = if (status == ZombieRaidStatus.INIT) amp
        else if (isFinalWave()) 0f else .5f * amp
        zombieRaidOmenLevel += value
        player.removeEffect(PazEffects.ZOMBIE_OMEN)
        return true
    }

    private fun spawnNextWave(level: ServerLevel, pos: BlockPos) {
        val creditsUnlocked = hasRaidStarterSeenCredits(level)
        val waveType = WaveType.pickWaveType(this, creditsUnlocked)
        waveTypes = waveTypes.toMutableList()
        waveTypes.add(waveType)
        val wave = currentWaveNumber()
        val shouldSpawnBoss = wave % 10 == 0
        var leaderSet = false
        totalZombieHealth = 0f
        totalBossHealth = 0f
        waveTimer = if (isFinalWave()) FINAL_WAVE_DURATION else if (shouldSpawnBoss) BOSS_WAVE_DURATION else WAVE_DURATION
        if (waveType != WaveType.DEFAULT) announceSpecialWave(waveType)
        if (shouldSpawnBoss) spawnBoss(level, pos, waveType)

        val waveEntries = waveType.spawnEntries(this, creditsUnlocked)

        for ((type, count, configure) in waveEntries) {
            for (i in 0..<count) {
                val zombie = type.create(level, EntitySpawnReason.EVENT) ?: break
                val spawnPos = findClearSpawnPos(level, zombie, pos, 20) ?: pos
                if (!leaderSet) {
                    setLeader(zombie)
                    leaderSet = true
                }
                joinRaid(level, zombie, true, wave, false, spawnPos, configure)
            }
        }
    }

    fun spawnBoss(level: ServerLevel, pos: BlockPos, waveType: WaveType) {
        val bossType: BossType = BossType.pickBossType(this)
        val bossEntities = bossType.bossEntityEntries(this, waveType)
        for ((type, configure) in bossEntities) {
            val zombie = type.create(level, EntitySpawnReason.EVENT) ?: return
            val spawnPos = pos
            setBoss(zombie)
            joinRaid(level, zombie, exists = false, pos = pos, configure = configure)
        }
        val bossName = bossEntities.firstOrNull()?.entityType?.description?: Component.empty()
        if (waveType == WaveType.DEFAULT) announceBoss(bossName)
        zombieRaidEvent.players.forEach {
            it.sendSystemMessage(Component.translatable("event.plantz.zombie_raid.boss.spawn", bossName).withColor(0x872727))
        }
    }

    fun joinRaid(
        level: ServerLevel,
        zombie: Zombie,
        addMaxHealth: Boolean = true,
        waveNumber: Int = currentWaveNumber(),
        exists: Boolean = true,
        pos: BlockPos? = null,
        configure: (Zombie) -> Unit = {},
    ) {
        if (status != ZombieRaidStatus.ONGOING) return
        val added = addWaveMob(zombie, waveNumber, level, addMaxHealth)
        if (added) {
            if (!exists && pos != null) {
                zombie.setPos(pos.x.toDouble() + 0.5, pos.y.toDouble(), pos.z.toDouble() + 0.5)
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
                if (removeFromTotalHealth) {
                    totalZombieHealth -= zombie.getEffectiveRaidHealth()
                    if (zombie.isBoss()) totalBossHealth -= zombie.getEffectiveRaidHealth()
                }
                (zombie as? ZombieRaider)?.`plantz$setIsFromRaid`(false)
                sendClientUpdate(level)
                setDirty(level)
                break
            }
        }
    }

    private fun vanishAllZombies(level: ServerLevel) {
        level.playLocalSound(center, SoundEvents.WITHER_DEATH, SoundSource.UI, 1f, 0.4f, false)
        waveZombieMap.values.forEach { zombies ->
            zombies.forEach { zombie ->
                if (zombie is PazZombie) {
                    level.sendParticles(ParticleTypes.LARGE_SMOKE, zombie.x, zombie.y, zombie.z, 10, 0.5, 0.5, 0.5, 0.02)
                    zombie.discard()
                }
            }
        }
        pendingRaiders.clear()
        totalZombieHealth = 0f
        totalBossHealth = 0f
        setDirty(level)
    }

    private fun vanishAllWildPlants(level: ServerLevel) {
        val maxDistSqr = (RAID_DISTANCE * RAID_DISTANCE).toDouble()
        level.getEntitiesOfClass(Plant::class.java, AABB(center).inflate(RAID_DISTANCE.toDouble())) {
            !it.isTame && it.blockPosition().distSqr(center) < maxDistSqr
        }.forEach { it.vanish(level) }
    }

    private fun setDirty(level: ServerLevel) { level.getZombieRaids().setDirty() }

    private fun addWaveMob(zombie: Zombie, wave: Int = currentWaveNumber(), level: ServerLevel, addMaxHealth: Boolean): Boolean {
        waveZombieMap.computeIfAbsent(wave) { Sets.newHashSet<Zombie>() }
        val zombies = waveZombieMap[wave] as MutableSet<Zombie>
        if (zombies.contains(zombie)) return false
        var existingCopy: Zombie? = null

        zombies.firstOrNull { it.getUUID() == zombie.getUUID() }?.let { existingCopy = it }

        if (existingCopy != null) zombies.remove(existingCopy)

        zombies.add(zombie)
        (zombie as? ZombieRaider)?.`plantz$setIsFromRaid`(true)
        val countHealth = (zombie as? ZombieRaider)?.`plantz$getAddsHealthToRaid`() ?: true
        if (countHealth && addMaxHealth) {
            totalZombieHealth += zombie.maxHealth
            if (zombie.isBoss()) totalBossHealth += zombie.maxHealth
        }
        sendClientUpdate(level)
        return true
    }

    private fun sendClientUpdate(level: ServerLevel, terminate: Boolean = false) {
        val data = ZombieRaidClientData(
            id = zombieRaidEvent.id,
            status = if (terminate) ZombieRaidStatus.TERMINATE else status,
            waveTypes = waveTypes,
            activeTime = ticksActive.toInt(),
            numWaves = numWaves,
            waveTimer = waveTimer,
            bossProgressBar = bossProgressBar(),
            zombieProgressBar = zombieProgressBar(),
            plantProgressBar = plantProgressBar(level),
            seenCredits = starterHasSeenCredits
        )

        val packet = ZombieRaidResponsePayload(data)
        zombieRaidEvent.players.forEach { player ->
            player.connection.send(ClientboundCustomPayloadPacket(packet))
        }
    }

    fun setLeader(zombie: Zombie) {
        (zombie as? ZombieRaider)?.`plantz$setIsLeader`(true)
        zombie.setItemSlot(EquipmentSlot.OFFHAND, PazBlocks.BRAINZ_FLAG.asItem().defaultInstance)
        zombie.setDropChance(EquipmentSlot.OFFHAND, 0.0f)
    }

    fun setBoss(zombie: Zombie) {
        (zombie as? ZombieRaider)?.`plantz$setIsBoss`(true)
    }

    fun currentWaveNumber(): Int = waveTypes.size
    fun isFinalWave(): Boolean = currentWaveNumber() >= numWaves
    fun getLeaders(): List<Zombie> = waveZombieMap.values.flatMap { it.filter { z -> z.isLeader() } }
    fun getBosses(): List<Zombie> = waveZombieMap.values.flatMap { it.filter { z -> z.isBoss() } }
    fun getAliveRaiders(): Int = waveZombieMap.values.stream().mapToInt { it.map { z -> if (z.getEffectiveRaidHealth()>0f) 1 else 0 }.sum() }.sum() + pendingRaiders.size
    fun plantProgressBar(level: ServerLevel): Float {
        if (status == ZombieRaidStatus.LOSS) return 0f
        else {
            val flag = level.getBlockEntity(center) as? FlagBlockEntity
            return flag?.healthProgress() ?: 0f
        }
    }
    fun zombieProgressBar(): Float =
        (if (status != ZombieRaidStatus.NEXT_WAVE)// zombie health bar
            waveZombieMap.values.map { it.map { z -> z.getEffectiveRaidHealth() }.sum() }.sum() / totalZombieHealth
        else// next wave loading bar
            1f - (raidCooldownTicks.toFloat() / PRE_RAID_TICKS.toFloat())).coerceIn(0f, 1f)
    fun bossProgressBar(): Float {
        val l = (getBosses().map { it.getEffectiveRaidHealth() }.sum() / totalBossHealth).coerceIn(0f, 1f)
        return l
    }

    private fun raiderRecords(): List<RaiderRecord> =
        waveZombieMap.flatMap { (wave, zombies) ->
            zombies.filter { it.isAlive && (!it.isRemoved || it.removalReason?.shouldSave() == true) }.map { RaiderRecord.save(it, wave) }
        } + pendingRaiders.values

    private fun shouldSpawnNextWave(): Boolean {
        return (getAliveRaiders() == 0 || waveTimer == 0)
            && status == ZombieRaidStatus.ONGOING
            && !isFinalWave()
            && getBosses().isEmpty()
    }

    private fun refreshRaidProfile(level: ServerLevel) {
        if (!starterHasSeenCredits) starterHasSeenCredits = hasRaidStarterSeenCredits(level)
        numWaves = getWaveSpawnCount(level.difficulty, this)
    }

    private fun hasRaidStarterSeenCredits(level: ServerLevel): Boolean {
        val starter = startedBy ?: return starterHasSeenCredits
        val player = level.server.playerList.getPlayer(starter) ?: return starterHasSeenCredits
        return starterHasSeenCredits || player.seenCredits
    }

    private fun announceSpecialWave(wave: WaveType) {
        val titleColor = if (wave == WaveType.ZOMBOWEEN) ChatFormatting.DARK_PURPLE else ChatFormatting.GREEN
        val subColor = if (wave == WaveType.ZOMBOWEEN) ChatFormatting.GOLD else ChatFormatting.DARK_PURPLE
        val title: Component = Component.translatable("event.plantz.zombie_raid.special_wave").apply { withStyle(titleColor, ChatFormatting.BOLD) }
        val subtitle: Component = wave.popupMessage().copy().apply { withStyle(subColor, ChatFormatting.BOLD) }
        showTitleMessage(title, subtitle)
    }

    private fun announceBoss(bossName: Component) {
        val title = Component.translatable("event.plantz.zombie_raid.boss_wave").apply { withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD) }
        val subtitle = Component.translatable("event.plantz.zombie_raid.boss", bossName).apply { withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD) }
        showTitleMessage(title, subtitle)
    }

    private fun showTitleMessage(title: Component, subtitle: Component = Component.empty(), sound: Holder.Reference<SoundEvent> = PazSounds.SPECIAL_WAVE, soundSource: SoundSource = SoundSource.UI) {
        zombieRaidEvent.players.forEach {
            it.connection.send(ClientboundSoundPacket(sound, soundSource, it.x, it.y, it.z, 1.0f, 1.0f, random.nextLong()))
            it.connection.send(ClientboundSetTitleTextPacket(title))
            it.connection.send(ClientboundSetSubtitleTextPacket(subtitle))
        }
    }

    /** Find the center location for the zombies to spawn */
    private fun findRandomSpawnPos(level: ServerLevel, maxTries: Int): BlockPos? {
        val spawnPos = MutableBlockPos()
        val startAngle = random.nextFloat() * (Math.PI * 2).toFloat()

        for (i in 0..<maxTries) {
            val angle = startAngle + Math.PI.toFloat() * i / 8.0f
            val spawnX = center.x + Mth.floor(Mth.cos(angle.toDouble()) * SPAWN_DISTANCE)
            val spawnZ = center.z + Mth.floor(Mth.sin(angle.toDouble()) * SPAWN_DISTANCE)
            val spawnY = findSpawnSurfaceY(level, spawnX, spawnZ)
            if (abs(spawnY - center.y) > MAX_SPAWN_HEIGHT_DIFFERENCE) continue
            val pos = BlockPos(spawnX, spawnY - 1, spawnZ)
            if (level.getBlockState(pos).`is`(PazTags.BlockTags.INVALID_RAID_SPAWN)) continue
            spawnPos.set(spawnX, spawnY, spawnZ)
            if (level.isPositionEntityTicking(spawnPos)) return spawnPos
        }
        return null
    }

    /** Scatter the zombies across the center spawn area */
    private fun findClearSpawnPos(level: ServerLevel, zombie: Zombie, origin: BlockPos, maxTries: Int): BlockPos? {
        for (attempt in 0 until maxTries) {
            val x = origin.x + random.nextInt(11) - 5
            val z = origin.z + random.nextInt(11) - 5
            val y = findSpawnSurfaceY(level, x, z)
            if (abs(y - center.y) > MAX_SPAWN_HEIGHT_DIFFERENCE || abs(y - origin.y) > MAX_SPAWN_HEIGHT_DIFFERENCE) continue
            if (level.getBlockState(BlockPos(x, y - 1, z)).`is`(PazTags.BlockTags.INVALID_RAID_SPAWN)) continue

            val candidate = MutableBlockPos(x, y, z)
            if (level.isPositionEntityTicking(candidate) && canZombieSpawnAt(level, zombie, candidate)) return candidate
        }
        return null
    }

    private fun findSpawnSurfaceY(level: ServerLevel, x: Int, z: Int): Int {
        var spawnY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)
        while (level.getFluidState(BlockPos(x, spawnY + 1, z)).`is`(FluidTags.WATER)) spawnY++
        return spawnY
    }

    private fun canZombieSpawnAt(level: ServerLevel, zombie: Zombie, pos: BlockPos): Boolean {
        zombie.setPos(pos.x.toDouble() + 0.5, pos.y.toDouble(), pos.z.toDouble() + 0.5)
        return level.noCollision(zombie)
    }

    fun stop() {
        val data = ZombieRaidClientData(id = zombieRaidEvent.id, status = ZombieRaidStatus.TERMINATE)
        status = ZombieRaidStatus.STOPPED
        zombieRaidEvent.players.forEach { player ->// terminate raid connection
            player.connection.send(ClientboundCustomPayloadPacket(ZombieRaidResponsePayload(data)))
        }
        zombieRaidEvent.removeAllPlayers()
    }
    fun isStopped(): Boolean = status == ZombieRaidStatus.STOPPED
}