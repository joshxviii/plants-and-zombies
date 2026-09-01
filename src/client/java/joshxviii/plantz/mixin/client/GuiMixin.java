package joshxviii.plantz.mixin.client;

import joshxviii.plantz.PazEffects;
import joshxviii.plantz.PazModels;
import joshxviii.plantz.ZombieHudMarkers;
import joshxviii.plantz.effect.PaintedMobEffect;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * @author Josh
 */
@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    protected abstract void extractTextureOverlay(GuiGraphicsExtractor graphics, Identifier texture, float alpha);

    @Unique
    private final Identifier FREEZE_OUTLINE_LOCATION = Identifier.withDefaultNamespace("textures/misc/powder_snow_outline.png");

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractCameraOverlays", at = @At("HEAD"))
    public void extractCameraOverlays(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        LocalPlayer player = this.minecraft.player;
        if (player == null) return;
        var effects = PaintedMobEffect.getPaintEffects(player, null);
        effects.forEach( it -> {
            var effect = it.getEffect().value();
            if (effect instanceof PaintedMobEffect paintedMobEffect) {
                extractPaintOverlay(graphics, paintedMobEffect.getRandomness(), paintedMobEffect.getPaintColor(), it.getAmplifier(), (it.getDuration()/80f));
            }
        });
        if (player.hasEffect(PazEffects.CHILLED)) extractTextureOverlay(graphics, FREEZE_OUTLINE_LOCATION, 0.15f);
        if (player.hasEffect(PazEffects.FROZEN)) extractTextureOverlay(graphics, FREEZE_OUTLINE_LOCATION, 1.0f);

        ZombieHudMarkers.INSTANCE.renderIcons(graphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    @Unique
    private void extractPaintOverlay(final GuiGraphicsExtractor graphics, RandomSource random, int color, int amplifier, float alpha) {
        float srcWidth = Math.min(graphics.guiWidth(), graphics.guiHeight());


        for (int i = 0; i < amplifier+1; i++) {
            float scale = 0.2f * (random.nextFloat() + 1.5f);
            float ratio = Math.min(graphics.guiWidth() / srcWidth, graphics.guiHeight() / srcWidth) * scale;
            int width = Mth.floor(srcWidth * ratio * .5);
            int height = Mth.floor(srcWidth * ratio * .5);
            int x = random.nextInt(graphics.guiWidth() - width);
            int y = random.nextInt(graphics.guiHeight() - height);
            graphics.blit(RenderPipelines.GUI_TEXTURED, PazModels.INSTANCE.getOverlayTexture(random.nextFloat()), x, y, 0.0F, 0.0F, width, height, width, height, ARGB.multiplyAlpha(ARGB.opaque(color), alpha));
        }

        //graphics.fill(RenderPipelines.GUI, 0, bottom, graphics.guiWidth(), graphics.guiHeight(), -16777216);
        //graphics.fill(RenderPipelines.GUI, 0, 0, graphics.guiWidth(), top, -16777216);
        //graphics.fill(RenderPipelines.GUI, 0, top, left, bottom, -16777216);
        //graphics.fill(RenderPipelines.GUI, right, top, graphics.guiWidth(), bottom, -16777216);
    }
}
