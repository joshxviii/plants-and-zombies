package joshxviii.plantz

import joshxviii.plantz.entity.gnome.Gnome
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.zombie.DiggerZombie
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.entity.zombie.ZombieYeti
import joshxviii.plantz.mixin.SpawnPlacementsInvoker
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.*
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.levelgen.Heightmap

object PazSpawnPlacements {

    //TODO make a layer for more dynamic spawning mechanics
    fun initialize() {
        // region PLANTS
        addBiomeSpawn(PazTags.Biomes.HAS_CACTUS, PazEntities.CACTUS,
            weight = 6, minGroupSize = 1, maxGroupSize = 3)
        registerSpawnPlacement(PazEntities.CACTUS, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_CHERRYBOMB, PazEntities.CHERRY_BOMB,
            weight = 4, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.CHERRY_BOMB, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_CHOMPER, PazEntities.CHOMPER,
            weight = 3, minGroupSize = 1, maxGroupSize = 1, category = MobCategory.MONSTER)
        registerSpawnPlacement(PazEntities.CHOMPER, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_FIRE_PEASHOOTER, PazEntities.FIRE_PEA_SHOOTER,
            weight = 40, minGroupSize = 1, maxGroupSize = 2, category = MobCategory.MONSTER)
        registerSpawnPlacement(PazEntities.FIRE_PEA_SHOOTER, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_FUMESHROOM, PazEntities.FUME_SHROOM,
            weight = 8, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.FUME_SHROOM, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_HYPNOSHROOM, PazEntities.HYPNOSHROOM,
            weight = 2, minGroupSize = 1, maxGroupSize = 1, category = MobCategory.MONSTER)
        registerSpawnPlacement(PazEntities.HYPNOSHROOM, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_ICE_PEASHOOTER, PazEntities.ICE_PEA_SHOOTER,
            weight = 5, minGroupSize = 1, maxGroupSize = 2)
        registerSpawnPlacement(PazEntities.ICE_PEA_SHOOTER, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_CABBAGEPULT, PazEntities.CABBAGE_PULT,
            weight = 4, minGroupSize = 1, maxGroupSize = 2)
        registerSpawnPlacement(PazEntities.CABBAGE_PULT, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_KERNELPULT, PazEntities.KERNEL_PULT,
            weight = 4, minGroupSize = 1, maxGroupSize = 2)
        registerSpawnPlacement(PazEntities.KERNEL_PULT, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_MELONPULT, PazEntities.MELON_PULT, category = MobCategory.MONSTER,
            weight = 1, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.MELON_PULT, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_PEASHOOTER, PazEntities.PEA_SHOOTER,
            weight = 8, minGroupSize = 1, maxGroupSize = 3)
        registerSpawnPlacement(PazEntities.PEA_SHOOTER, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_POTATOMINE, PazEntities.POTATO_MINE,
            weight = 3, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.POTATO_MINE, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_REPEATER, PazEntities.REPEATER,
            weight = 5, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.REPEATER, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_SCAREDYSHROOM, PazEntities.SCAREDY_SHROOM,
            weight = 7, minGroupSize = 1, maxGroupSize = 2)
        registerSpawnPlacement(PazEntities.SCAREDY_SHROOM, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_SUNFLOWER, PazEntities.SUNFLOWER,
            weight = 8, minGroupSize = 1, maxGroupSize = 3)
        addBiomeSpawn(PazTags.Biomes.HAS_SUNFLOWER_ALT, PazEntities.SUNFLOWER,
            weight = 10, minGroupSize = 2, maxGroupSize = 4)
        registerSpawnPlacement(PazEntities.SUNFLOWER, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_PUFFSHROOM, PazEntities.PUFF_SHROOM,
            weight = 20, minGroupSize = 2, maxGroupSize = 5)
        registerSpawnPlacement(PazEntities.PUFF_SHROOM, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_WALLNUT, PazEntities.WALL_NUT,
            weight = 5, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.WALL_NUT, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_SUNSHROOM, PazEntities.SUN_SHROOM,
            weight = 15, minGroupSize = 2, maxGroupSize = 3, category = MobCategory.MONSTER)
        addBiomeSpawn(PazTags.Biomes.HAS_SUNFLOWER, PazEntities.SUN_SHROOM,
            weight = 1, minGroupSize = 1, maxGroupSize = 2)
        registerSpawnPlacement(PazEntities.SUN_SHROOM, Plant::checkPlantSpawnRules)

        addBiomeSpawn(PazTags.Biomes.HAS_COFFEE_BEAN, PazEntities.COFFEE_BEAN,
            weight = 8, minGroupSize = 1, maxGroupSize = 2)
        registerSpawnPlacement(PazEntities.COFFEE_BEAN, Plant::checkPlantSpawnRules, Heightmap.Types.MOTION_BLOCKING)
        // endregion

        // region ZOMBIES
        addBiomeSpawn(PazTags.Biomes.HAS_BROWNCOAT, PazEntities.BROWN_COAT, category = MobCategory.MONSTER,
            weight = 25, minGroupSize = 1, maxGroupSize = 4)
        registerSpawnPlacement(PazEntities.BROWN_COAT, PazZombie::checkZombieSpawnRules)
        addBiomeSpawn(PazTags.Biomes.HAS_NEWSPAPER_ZOMBIE, PazEntities.NEWSPAPER_ZOMBIE, category = MobCategory.MONSTER,
            weight = 8, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.NEWSPAPER_ZOMBIE, PazZombie::checkZombieSpawnRules)
        addBiomeSpawn(PazTags.Biomes.HAS_ZOMBIE_YETI, PazEntities.ZOMBIE_YETI, category = MobCategory.MONSTER,
            weight = 10, minGroupSize = 1, maxGroupSize = 1)
        addBiomeSpawn(PazTags.Biomes.HAS_ZOMBIE_YETI_ALT, PazEntities.ZOMBIE_YETI, category = MobCategory.MONSTER,
            weight = 30, minGroupSize = 1, maxGroupSize = 2)
        registerSpawnPlacement(PazEntities.ZOMBIE_YETI, ZombieYeti::checkZombieYetiSpawnRules)
        addBiomeSpawn(PazTags.Biomes.HAS_DIGGER, PazEntities.DIGGER_ZOMBIE, category = MobCategory.MONSTER,
            weight = 2, minGroupSize = 1, maxGroupSize = 1)
        addBiomeSpawn(PazTags.Biomes.HAS_DIGGER_ALT, PazEntities.DIGGER_ZOMBIE, category = MobCategory.MONSTER,
            weight = 30, minGroupSize = 1, maxGroupSize = 2)
        registerSpawnPlacement(PazEntities.DIGGER_ZOMBIE, DiggerZombie::checkMinerSpawnRules)
        addBiomeSpawn(PazTags.Biomes.HAS_DISCO_ZOMBIE, PazEntities.DISCO_ZOMBIE, category = MobCategory.MONSTER,
            weight = 4, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.DISCO_ZOMBIE, PazZombie::checkZombieSpawnRules)
        addBiomeSpawn(PazTags.Biomes.HAS_ALL_STAR, PazEntities.ALL_STAR, category = MobCategory.MONSTER,
            weight = 6, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.ALL_STAR, PazZombie::checkZombieSpawnRules)
        addBiomeSpawn(PazTags.Biomes.HAS_IMP, PazEntities.IMP, category = MobCategory.MONSTER,
            weight = 5, minGroupSize = 1, maxGroupSize = 1)
        registerSpawnPlacement(PazEntities.IMP, PazZombie::checkZombieSpawnRules)
        // endregion
    }

    fun <T : Mob> addBiomeSpawn(biomeTag: TagKey<Biome>, entityType: EntityType<T>, weight: Int, minGroupSize: Int, maxGroupSize: Int, category: MobCategory = MobCategory.CREATURE) {
        BiomeModifications.addSpawn({ biomeSelector: BiomeSelectionContext -> biomeSelector.hasTag(biomeTag) }, category, entityType, weight, minGroupSize, maxGroupSize)
    }

    fun <T : Mob> registerSpawnPlacement(entityType: EntityType<T>, spawnRules: SpawnPlacements.SpawnPredicate<T>, heightmap: Heightmap.Types = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES) {
        SpawnPlacementsInvoker.invokeRegister(
            entityType,
            SpawnPlacementTypes.ON_GROUND,
            heightmap,
            spawnRules
        )
    }
}