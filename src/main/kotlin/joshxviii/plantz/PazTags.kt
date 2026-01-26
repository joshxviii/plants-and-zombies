package joshxviii.plantz

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Block

object PazTags {
    object BlockTags {
        @JvmField val PLANTABLE = tag("plantable")
        @JvmField val YETI_SPAWNABLE_ON = tag("yeti_spawnable_on")
        @JvmField val MINER_BREAKABLE = tag("miner_breakable")
        private fun tag(name: String): TagKey<Block> = TagKey.create(Registries.BLOCK, pazResource(name))
    }

    object ItemTags {
        @JvmField val BLOCKS_PLANT_PROJECTILE = tag("blocks_plant_projectile")
        @JvmField val GNOME_PREFERRED_WEAPONS = tag("gnome_preferred_weapons")
        private fun tag(name: String): TagKey<Item> = TagKey.create(Registries.ITEM, pazResource(name))
    }

    object EntityTypes {
        @JvmField val PLANT = tag("plant")
        @JvmField val PLANT_PROJECTILE = tag("plant_projectile")
        @JvmField val CANNOT_CHOMP = tag("cannot_be_chomped")
        @JvmField val CANNOT_HYPNOTIZE = tag("cannot_be_hypnotized")
        @JvmField val ZOMBIE_RAIDERS = tag("zombie_raider")
        @JvmField val ATTACKS_PLANTS = tag("attacks_plants")
        @JvmField val IGNORED_BY_PLANT_ATTACKERS = tag("ignored_by_plant_attackers")
        @JvmField val GNOME_RIDEABLE = tag("gnome_rideable")
        private fun tag(name: String): TagKey<EntityType<*>> = TagKey.create(Registries.ENTITY_TYPE, pazResource(name))
    }

    object DamageTypes {
        @JvmField val PLANT_PROJECTILE = tag("plant_projectile")
        @JvmField val BLOCKABLE_WITH_HELMET = tag("blockable_with_helmet")
        private fun tag(name: String): TagKey<DamageType> = TagKey.create(Registries.DAMAGE_TYPE, pazResource(name))
    }

    object Biomes {

        val HAS_CACTUS = tag("plant/has_cactus")
        val HAS_CHERRYBOMB = tag("plant/has_cherrybomb")
        val HAS_CHOMPER = tag("plant/has_chomper")
        val HAS_FIRE_PEASHOOTER = tag("plant/has_fire_peashooter")
        val HAS_FUMESHROOM = tag("plant/has_fumeshroom")
        val HAS_ICE_PEASHOOTER = tag("plant/has_ice_peashooter")
        val HAS_MELONPULT = tag("plant/has_melonpult")
        val HAS_PEASHOOTER = tag("plant/has_peashooter")
        val HAS_POTATOMINE = tag("plant/has_potatomine")
        val HAS_PUFFSHROOM = tag("plant/has_puffshroom")
        val HAS_REPEATER = tag("plant/has_repeater")
        val HAS_SUNFLOWER = tag("plant/has_sunflower")
        val HAS_SUNSHROOM = tag("plant/has_sunshroom")
        val HAS_WALLNUT = tag("plant/has_wallnut")

        val HAS_BROWNCOAT = tag("zombie/has_browncoat")
        val HAS_ZOMBIE_YETI = tag("zombie/has_zombie_yeti")
        val HAS_ZOMBIE_YETI_ALT = tag("zombie/has_zombie_yeti_alt")

        private fun tag(name: String): TagKey<Biome>  = TagKey.create(Registries.BIOME, pazResource(name))
    }

    fun initialize() {}
}