package joshxviii.plantz

import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.zombie.Miner
import joshxviii.plantz.entity.zombie.ZombieYeti
import joshxviii.plantz.mixin.SpawnPlacementsInvoker
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.SpawnPlacementTypes
import net.minecraft.world.entity.SpawnPlacements
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.Heightmap

object PazSpawnPlacements {

    fun initialize() {
        // region PLANTS
        addBiomeSpawn(PazTags.Biomes.HAS_CACTUS, PazEntities.CACTUS,
            weight = 10, minGroupSize = 1, maxGroupSize = 4)
        registerSpawnPlacement(PazEntities.CACTUS, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_CHOMPER, PazEntities.CHOMPER,
            weight = 10, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.CHOMPER, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_SUNFLOWER, PazEntities.SUNFLOWER,
            weight = 20, minGroupSize = 2, maxGroupSize = 4)
        registerSpawnPlacement(PazEntities.SUNFLOWER, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_PUFFSHROOM, PazEntities.PUFF_SHROOM,
            weight = 25, minGroupSize = 2, maxGroupSize = 5)
        registerSpawnPlacement(PazEntities.PUFF_SHROOM, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_FUMESHROOM, PazEntities.FUME_SHROOM,
            weight = 10, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.FUME_SHROOM, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_SUNSHROOM, PazEntities.SUN_SHROOM,
            weight = 10, minGroupSize = 2, maxGroupSize = 3)
        registerSpawnPlacement(PazEntities.SUN_SHROOM, Plant::checkPlantSpawnRules)
        // endregion

        // region ZOMBIES
        addBiomeSpawn(PazTags.Biomes.HAS_ZOMBIE_YETI, PazEntities.ZOMBIE_YETI, category = MobCategory.MONSTER,
            weight = 50, minGroupSize = 1, maxGroupSize = 2)
        addBiomeSpawn(PazTags.Biomes.HAS_ZOMBIE_YETI_ALT, PazEntities.ZOMBIE_YETI, category = MobCategory.MONSTER,
            weight = 10, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.ZOMBIE_YETI, ZombieYeti::checkZombieYetiSpawnRules)
        addBiomeSpawn(PazTags.Biomes.HAS_MINER, PazEntities.MINER, category = MobCategory.MONSTER,
            weight = 10, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.MINER, Miner::checkMinerSpawnRules)
        // endregion
    }

    fun <T : Mob> addBiomeSpawn(biomeTag: TagKey<Biome>, entityType: EntityType<T>, weight: Int, minGroupSize: Int, maxGroupSize: Int, category: MobCategory = MobCategory.CREATURE) {
        BiomeModifications.addSpawn({ biomeSelector: BiomeSelectionContext -> biomeSelector.hasTag(biomeTag) }, category, entityType, weight, minGroupSize, maxGroupSize)
    }

    fun <T : Mob> registerSpawnPlacement(entityType: EntityType<T>, spawnRules: SpawnPlacements.SpawnPredicate<T>) {
        SpawnPlacementsInvoker.invokeRegister(
            entityType,
            SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            spawnRules
        )
    }
}