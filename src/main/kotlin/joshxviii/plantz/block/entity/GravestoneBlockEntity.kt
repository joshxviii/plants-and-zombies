package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazEntities.ALL_STAR
import joshxviii.plantz.PazEntities.BROWN_COAT
import joshxviii.plantz.PazEntities.DIGGER_ZOMBIE
import joshxviii.plantz.PazEntities.DISCO_ZOMBIE
import joshxviii.plantz.PazEntities.IMP
import joshxviii.plantz.PazEntities.NEWSPAPER_ZOMBIE
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import joshxviii.plantz.entity.zombie.PazZombie.Companion.SPAWN_TABLE_WEIGHTS
import joshxviii.plantz.entity.zombie.PazZombie.Companion.findSpawnPosition
import joshxviii.plantz.entity.zombie.PazZombie.Companion.rollZombieType
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.UUID
import kotlin.jvm.optionals.getOrDefault

class GravestoneBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState,
) : BlockEntity(
    PazBlocks.GRAVESTONE_BLOCK_ENTITY, worldPosition, blockState
) {
    companion object {
        private const val SPAWN_DELAY_MIN = 400
        private const val SPAWN_DELAY_MAX = 600
        private const val ZOMBIE_DETECTION_RADIUS = 32.0
        private const val MAX_TRACKED_ZOMBIES = 2
        private const val MAX_NEARBY_ZOMBIES = 3

        fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: GravestoneBlockEntity) {
            if (level.isClientSide || level !is ServerLevel) return
            blockEntity.updateTrackedZombies(level)
            if (blockEntity.ticksSinceLastSpawn++ >= blockEntity.spawnDelay) blockEntity.trySpawnZombie(level, pos)
        }
    }

    private var spawnDelay = SPAWN_DELAY_MIN
    private var ticksSinceLastSpawn = 0
    private val trackedZombieIds = mutableSetOf<UUID>()

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.store("tracked_zombies", UUIDUtil.CODEC_SET, trackedZombieIds)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        trackedZombieIds.clear()
        trackedZombieIds.addAll(input.read("tracked_zombies", UUIDUtil.CODEC_SET).getOrDefault(emptySet()))
    }

    private fun updateTrackedZombies(level: ServerLevel) {
        if (trackedZombieIds.removeIf { uuid -> level.getEntity(uuid)?.let { !it.isAlive } == true }) {
            setChanged()
        }
    }

    private fun isDarkEnough(level: ServerLevel): Boolean {
        return !(level.isBrightOutside && level.getBrightness(LightLayer.SKY, blockPos) >= 7)
    }

    private fun canSpawn(level: ServerLevel, pos: BlockPos): Boolean {
        if ( !isDarkEnough(level) && !level.getBiome(pos).`is`(PazTags.Biomes.GRAVESTONE_IGNORE_BRIGHTNESS) ) return false
        val centerPos = Vec3.atCenterOf(pos)
        val nearbyPlayer = level.getNearestPlayer(
            centerPos.x,
            centerPos.y,
            centerPos.z,
            PazConfig.GRAVESTONE_ACTIVATION_RANGE,
            true
        )
        if (nearbyPlayer == null) return false

        val aabb = AABB.ofSize(centerPos, ZOMBIE_DETECTION_RADIUS, 16.0, ZOMBIE_DETECTION_RADIUS)
        val nearbyZombies = level.getEntitiesOfClass(Zombie::class.java, aabb) { it.isAlive }

        return trackedZombieIds.size < MAX_TRACKED_ZOMBIES && nearbyZombies.size < MAX_NEARBY_ZOMBIES
    }

    private fun trySpawnZombie(level: ServerLevel, pos: BlockPos) {
        if (!canSpawn(level, pos)) return

        val type = rollZombieType(level.random) ?: return
        val spawnPos = findSpawnPosition(level, pos) ?: return

        val zombie = type.create(
            level,
            null,
            spawnPos,
            EntitySpawnReason.REINFORCEMENT,
            true,
            false
        ) ?: return

        if (!level.addFreshEntity(zombie)) return
        val centerPos = Vec3.atCenterOf(pos)
        val spawnPosCenter = Vec3.atCenterOf(spawnPos)
        trackedZombieIds.add(zombie.uuid)
        setChanged()

        spawnDelay = Mth.randomBetweenInclusive(level.random, SPAWN_DELAY_MIN, SPAWN_DELAY_MAX)
        ticksSinceLastSpawn = 0
        level.sendParticles(
            PazServerParticles.ZOMBIE_OMEN,
            centerPos.x,
            centerPos.y,
            centerPos.z,
            4, 0.25, 0.125, 0.25, 0.0
        )
        level.sendParticles(
            PazServerParticles.ZOMBIE_OMEN,
            spawnPosCenter.x,
            spawnPosCenter.y,
            spawnPosCenter.z,
            3, 0.15, 0.0, 0.15, 0.0
        )
        level.playSound(null, pos, PazSounds.APPLY_ZOMBIE_OMEN, SoundSource.BLOCKS, 0.75f, 2.0f)
    }
}