package joshxviii.plantz

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey

object PazTags {
    object BlockTags {
        @JvmField val TAG_PLANTABLE = tag("plantable")
        private fun tag(name: String) = TagKey.create(Registries.BLOCK, pazResource(name))
    }

    object ItemTags {

        private fun tag(name: String) = TagKey.create(Registries.ITEM, pazResource(name))
    }

    object EntityTypes {
        @JvmField val TAG_PLANT = tag("plant")
        @JvmField val TAG_PLANT_PROJECTILE = tag("plant_projectile")
        @JvmField val TAG_CANNOT_CHOMP = tag("cannot_be_chomped")
        @JvmField val ZOMBIE_RAIDERS = tag("zombie_raider")
        @JvmField val ATTACKS_PLANTS = tag("attacks_plants")
        private fun tag(name: String) = TagKey.create(Registries.ENTITY_TYPE, pazResource(name))
    }

    object Biomes {

        private fun tag(name: String) = TagKey.create(Registries.BIOME, pazResource(name))
    }

    fun initialize() {}
}