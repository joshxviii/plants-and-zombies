package joshxviii.plantz

import com.mojang.datafixers.util.Pair
import joshxviii.plantz.PazWorldGen.GRAVEYARD
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Climate.ParameterPoint
import net.minecraft.world.level.levelgen.material.rule.MaterialRule
import terrablender.api.*
import terrablender.api.ParameterUtils.*
import terrablender.core.TerraBlender
import java.util.function.Consumer


object PazWorldGen: TerraBlender() {

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

    init {
        Regions.register(OverworldRegion("graveyard"))
        MaterialRuleManager.addRules(MaterialRuleManager.RuleCategory.OVERWORLD, PazMain.MODID, GRAVEYARD_MATERIAL)
    }

    @JvmField val TIME_SPACE = ResourceKey.create(Registries.DIMENSION, pazResource("time_space"))
    @JvmField val GNOME_SPACE = ResourceKey.create(Registries.DIMENSION, pazResource("gnome_space"))

    fun isTimeDimension(dimension: ResourceKey<Level>): Boolean = dimension == TIME_SPACE || dimension == GNOME_SPACE
}

class OverworldRegion(
    name: String,
    weight: Int = 1
): Region(pazResource(name), RegionType.OVERWORLD, weight) {

    override fun addBiomes(
        registry: Registry<Biome>,
        mapper: Consumer<Pair<ParameterPoint, ResourceKey<Biome>>>
    ) {
        val builder = VanillaParameterOverlayBuilder()

        ParameterPointListBuilder()
            .temperature(Temperature.span(Temperature.COOL, Temperature.WARM))
            .humidity(Humidity.span(Humidity.NEUTRAL, Humidity.HUMID))
            .continentalness(Continentalness.FAR_INLAND)
            .erosion(Erosion.EROSION_0, Erosion.EROSION_1)
            .depth(Depth.SURFACE, Depth.FLOOR)
            .weirdness(Weirdness.HIGH_SLICE_NORMAL_ASCENDING, Weirdness.MID_SLICE_NORMAL_DESCENDING)
            .build().forEach(Consumer { point: ParameterPoint? -> builder.add(point, GRAVEYARD) })

        builder.build().forEach(mapper)
    }
}