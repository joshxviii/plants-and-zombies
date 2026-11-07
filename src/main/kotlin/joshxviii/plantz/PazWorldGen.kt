package joshxviii.plantz

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.levelgen.material.rule.MaterialRule


object PazWorldGen {

    @JvmField val GRAVEYARD = registerBiome("graveyard")
    @JvmField val ANCIENT_EGYPT = registerBiome("ancient_egypt")
    @JvmField val JURASSIC_PERIOD = registerBiome("jurassic_period")
    @JvmField val DARK_AGES = registerBiome("dark_ages")
    @JvmField val FAR_FUTURE = registerBiome("far_future")
    @JvmField val GNOME_SPACE_BIOME = registerBiome("gnome_space")


    private fun registerBiome(name: String) : ResourceKey<Biome> {
        return ResourceKey.create(Registries.BIOME, pazResource(name) )
    }

    @JvmField val GRAVEYARD_MATERIAL = registerMaterialRule("graveyard")
    private fun registerMaterialRule(name: String) : ResourceKey<MaterialRule> {
        return ResourceKey.create(Registries.MATERIAL_RULE, pazResource(name) )
    }

    @JvmField val TIME_SPACE = ResourceKey.create(Registries.DIMENSION, pazResource("time_space"))
    @JvmField val GNOME_SPACE = ResourceKey.create(Registries.DIMENSION, pazResource("gnome_space"))

    fun isTimeDimension(dimension: ResourceKey<Level>): Boolean = dimension == TIME_SPACE || dimension == GNOME_SPACE

}
