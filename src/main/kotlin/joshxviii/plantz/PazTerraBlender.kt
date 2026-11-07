package joshxviii.plantz

import com.mojang.datafixers.util.Pair
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Climate.ParameterPoint
import terrablender.api.*
import terrablender.api.ParameterUtils.*
import java.util.function.Consumer

/** Only loaded when TerraBlender is present (see [PazWorldGen.initialize]). */
internal object PazTerraBlender {
    fun register() {
        Regions.register(OverworldRegion("graveyard"))
        MaterialRuleManager.addRules(MaterialRuleManager.RuleCategory.OVERWORLD, PazMain.MODID, PazWorldGen.GRAVEYARD_MATERIAL)
    }
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
            .build().forEach(Consumer { point: ParameterPoint? -> builder.add(point, PazWorldGen.GRAVEYARD) })

        builder.build().forEach(mapper)
    }
}