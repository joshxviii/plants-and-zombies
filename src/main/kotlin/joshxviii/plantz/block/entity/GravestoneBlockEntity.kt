package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
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
import net.minecraft.world.phys.AABB

class GravestoneBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState,
) : BlockEntity(
    PazBlocks.GRAVESTONE_BLOCK_ENTITY, worldPosition, blockState
) {
    companion object {
        private const val PLAYER_RANGE = 18
        private const val SPAWN_DELAY_MIN = 400
        private const val SPAWN_DELAY_MAX = 600
        private const val MAX_ZOMBIES = 4

        fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: GravestoneBlockEntity) {
            if (level.isClientSide || level !is ServerLevel) return
            if (blockEntity.ticksSinceLastSpawn++ >= blockEntity.spawnDelay) blockEntity.trySpawnZombie(level, pos)
        }
    }

    private var spawnDelay = SPAWN_DELAY_MIN
    private var ticksSinceLastSpawn = 0

    private fun isDarkEnough(level: ServerLevel): Boolean {
        return !(level.isBrightOutside && level.getBrightness(LightLayer.SKY, blockPos) >= 7)
    }

    private fun canSpawn(level: ServerLevel, pos: BlockPos): Boolean {
        if ( !isDarkEnough(level) && !level.getBiome(pos).`is`(PazTags.Biomes.GRAVESTONE_IGNORE_BRIGHTNESS) ) return false

        val nearbyPlayer = level.getNearestPlayer(
            pos.center.x,
            pos.center.y,
            pos.center.z,
            PLAYER_RANGE.toDouble(),
            true
        )
        if (nearbyPlayer == null) return false

        val aabb = AABB.ofSize(pos.center, 32.0, 16.0, 32.0)
        val nearbyZombies = level.getEntitiesOfClass(Zombie::class.java, aabb) { zombie ->
            true
        }

        return nearbyZombies.size < MAX_ZOMBIES
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
        zombie.setPersistenceRequired()

        level.addFreshEntity(zombie)

        spawnDelay = Mth.randomBetweenInclusive(level.random, SPAWN_DELAY_MIN, SPAWN_DELAY_MAX)
        ticksSinceLastSpawn = 0
        level.sendParticles(
            PazServerParticles.ZOMBIE_OMEN,
            pos.center.x,
            pos.center.y,
            pos.center.z,
            4, 0.25, 0.125, 0.25, 0.0
        )
        level.sendParticles(
            PazServerParticles.ZOMBIE_OMEN,
            spawnPos.center.x,
            spawnPos.center.y,
            spawnPos.center.z,
            3, 0.15, 0.0, 0.15, 0.0
        )
        level.playSound(null, pos, PazSounds.APPLY_ZOMBIE_OMEN, SoundSource.BLOCKS, 0.75f, 2.0f)
    }
}