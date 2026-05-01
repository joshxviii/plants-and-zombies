package joshxviii.plantz

import com.mojang.datafixers.util.Pair
import joshxviii.plantz.PazWorldGen.ZOMBIEVILLE
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Climate.ParameterPoint
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.MyceliumBlock
import net.minecraft.world.level.levelgen.SurfaceRules
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource
import terrablender.api.*
import terrablender.api.ParameterUtils.*
import java.util.function.Consumer


object PazWorldGen: TerraBlenderApi {

    override fun onTerraBlenderInitialized() {
        Regions.register(OverworldRegion("zombieville"))
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, PazMain.MODID, SurfaceRuleData.makeRules())
    }

    @JvmField val ZOMBIEVILLE = registerBiome("zombieville")

    private fun registerBiome(name: String) : ResourceKey<Biome> {
        return ResourceKey.create(Registries.BIOME, pazResource(name) )
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
            .build().forEach(Consumer { point: ParameterPoint? -> builder.add(point, ZOMBIEVILLE) })

        builder.build().forEach(mapper)
    }
}

object SurfaceRuleData {
    private val DIRT = makeStateRule(Blocks.DIRT)
    private val MYCELIUM_BLOCK = makeStateRule(Blocks.MYCELIUM)
    private val RED_TERRACOTTA = makeStateRule(Blocks.RED_TERRACOTTA)

    internal fun makeRules(): RuleSource {
        val isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0)
        val myceliumSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, MYCELIUM_BLOCK), DIRT)

        return SurfaceRules.sequence(
            SurfaceRules.ifTrue(SurfaceRules.isBiome(ZOMBIEVILLE), myceliumSurface),
        )
    }

    private fun makeStateRule(block: Block): RuleSource {
        return SurfaceRules.state(block.defaultBlockState())
    }
}