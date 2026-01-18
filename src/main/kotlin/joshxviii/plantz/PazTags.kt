package joshxviii.plantz

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey

object PazTags {
    object BlockTags {
        @JvmField val PLANTABLE = tag("plantable")
        private fun tag(name: String) = TagKey.create(Registries.BLOCK, pazResource(name))
    }

    object ItemTags {
        @JvmField val BLOCKS_PLANT_PROJECTILE = tag("blocks_plant_projectile")

        private fun tag(name: String) = TagKey.create(Registries.ITEM, pazResource(name))
    }

    object EntityTypes {
        @JvmField val PLANT = tag("plant")
        @JvmField val PLANT_PROJECTILE = tag("plant_projectile")
        @JvmField val CANNOT_CHOMP = tag("cannot_be_chomped")
        @JvmField val ZOMBIE_RAIDERS = tag("zombie_raider")
        @JvmField val ATTACKS_PLANTS = tag("attacks_plants")
        private fun tag(name: String) = TagKey.create(Registries.ENTITY_TYPE, pazResource(name))
    }

    object DamageTypes {
        @JvmField val PLANT_PROJECTILE = tag("plant_projectile")
        @JvmField val BLOCKABLE_WITH_HELMET = tag("blockable_with_helmet")

        private fun tag(name: String) = TagKey.create(Registries.DAMAGE_TYPE, pazResource(name))
    }

    object Biomes {

        private fun tag(name: String) = TagKey.create(Registries.BIOME, pazResource(name))
    }

    fun initialize() {}
}