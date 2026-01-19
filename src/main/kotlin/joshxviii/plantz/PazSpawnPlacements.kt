package joshxviii.plantz

import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.mixin.SpawnPlacementsInvoker
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.SpawnPlacementType
import net.minecraft.world.entity.SpawnPlacementTypes
import net.minecraft.world.entity.SpawnPlacements
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.Heightmap

object PazSpawnPlacements {

    fun initialize() {
        // region PLANTS
        addPlantSpawn(PazEntities.CACTUS, PazTags.Biomes.HAS_CACTUS,
            weight = 90,
            minGroupSize = 4,
            maxGroupSize = 8,
            Plant::checkPlantSpawnRules
        )

        addPlantSpawn(PazEntities.CHOMPER, PazTags.Biomes.HAS_CHOMPER,
            weight = 90,
            minGroupSize = 3,
            maxGroupSize = 5,
            Plant::checkPlantSpawnRules
        )
        // endregion

        // region ZOMBIES
        addZombieSpawn(PazEntities.ZOMBIE_YETI, PazTags.Biomes.HAS_ZOMBIE_YETI,
            weight = 90,
            minGroupSize = 3,
            maxGroupSize = 5
        )
        // endregion
    }

    fun <T : Mob> addPlantSpawn(entityType: EntityType<T>, biomeTag: TagKey<Biome>, weight: Int, minGroupSize: Int, maxGroupSize: Int, spawnRules: SpawnPlacements.SpawnPredicate<T>) {
        BiomeModifications.addSpawn({ biomeSelector: BiomeSelectionContext -> biomeSelector.hasTag(biomeTag) }, MobCategory.CREATURE, entityType, weight, minGroupSize, maxGroupSize)
        SpawnPlacementsInvoker.invokeRegister(
            entityType,
            SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            spawnRules
        )
    }

    fun <T : Mob> addZombieSpawn(entityType: EntityType<T>, biomeTag: TagKey<Biome>, weight: Int, minGroupSize: Int, maxGroupSize: Int) {
        BiomeModifications.addSpawn({ biomeSelector: BiomeSelectionContext -> biomeSelector.hasTag(biomeTag) }, MobCategory.MONSTER, entityType, weight, minGroupSize, maxGroupSize)
        SpawnPlacementsInvoker.invokeRegister(
            entityType,
            SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules
        )
    }
}