package joshxviii.plantz.mixin;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(OverworldBiomeBuilder.class)
public abstract class OverworldBiomeBuilderMixin {
    private static final ResourceKey<Biome> PLANTZ_GRAVEYARD = ResourceKey.create(
        Registries.BIOME,
        Identifier.fromNamespaceAndPath("plantz", "graveyard")
    );

    @Inject(method = "addBiomes", at = @At("HEAD"))
    private void plantz$addGraveyardWithoutTerraBlender(
        Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> biomes,
        CallbackInfo ci
    ) {
        if (FabricLoader.getInstance().isModLoaded("terrablender")) return;

        Climate.Parameter temperature = Climate.Parameter.span(-0.45F, 0.55F);
        Climate.Parameter humidity = Climate.Parameter.span(-0.1F, 1.0F);
        Climate.Parameter continentalness = Climate.Parameter.span(0.3F, 1.0F);
        Climate.Parameter[] erosions = {
            Climate.Parameter.span(-1.0F, -0.78F),
            Climate.Parameter.span(-0.78F, -0.375F)
        };
        Climate.Parameter[] depths = {
            Climate.Parameter.point(0.0F),
            Climate.Parameter.point(1.0F)
        };
        Climate.Parameter[] weirdnesses = {
            Climate.Parameter.span(-0.93333334F, -0.7666667F),
            Climate.Parameter.span(-0.4F, -0.26666668F)
        };

        for (Climate.Parameter erosion : erosions) {
            for (Climate.Parameter depth : depths) {
                for (Climate.Parameter weirdness : weirdnesses) {
                    Climate.ParameterPoint point = new Climate.ParameterPoint(
                        temperature,
                        humidity,
                        continentalness,
                        erosion,
                        depth,
                        weirdness,
                        0L
                    );
                    biomes.accept(Pair.of(point, PLANTZ_GRAVEYARD));
                }
            }
        }
    }
}
