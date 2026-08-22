package joshxviii.plantz.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import joshxviii.plantz.renderer.DuckyTubeRenderLayer;
import joshxviii.plantz.renderer.DyeVatRenderLayer;
import joshxviii.plantz.renderer.PaintLayer;
import joshxviii.plantz.mixin.LivingEntityAccessor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static joshxviii.plantz.PazModels.*;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> implements RenderLayerParent<S, M> {

    @Shadow
    protected abstract boolean addLayer(RenderLayer<S, M> layer);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void plantz$addDuckyTubeLayer(EntityRendererProvider.Context context, M model, float shadow, CallbackInfo ci) {
        this.addLayer(new DuckyTubeRenderLayer<>(this));
        this.addLayer(new DyeVatRenderLayer<>(this));
        this.addLayer(new PaintLayer<>(this));
    }

    @Inject(method = "extractRenderState*", at = @At("TAIL"))
    private void checkForHypnoEffect(T entity, S state, float partialTicks, CallbackInfo ci) {
        boolean hasHypno = ((LivingEntityAccessor) entity).plantz$getHypnoId();
        state.setData(HAS_HYPNO_KEY, hasHypno);
        boolean hasFreeze = ((LivingEntityAccessor) entity).plantz$getChillId();
        state.setData(HAS_FREEZE_KEY, hasFreeze);
        Map<Integer, Integer> paintColors = ((LivingEntityAccessor) entity).plantz$getPaintedColors();
        state.setData(PAINT_COLORS_KEY, paintColors);
    }

    @Unique
    private static final int PLANTZ_HYPNO_TINT = ARGB.opaque(0xD036FF);
    @Unique
    private static final int PLANTZ_FREEZE_TINT = ARGB.opaque(0x0098DC);

    @ModifyExpressionValue(
        method = "submit*",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ARGB;multiply(II)I")
    )
    private int plantz$applyHypnoTint(int tintedColor, S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        AtomicInteger finalColor = new AtomicInteger(tintedColor);

        if (state.getDataOrDefault(HAS_HYPNO_KEY, false)) {
            finalColor.set(ARGB.multiply(finalColor.get(), PLANTZ_HYPNO_TINT));
        }
        if (state.getDataOrDefault(HAS_FREEZE_KEY, false)) {
            finalColor.set(ARGB.multiply(finalColor.get(), PLANTZ_FREEZE_TINT));
        }

        // multiple colors end up just looking black. not gonna use this for now.
//        Map<Integer, Integer> colors = state.getDataOrDefault(PAINT_COLORS_KEY, new HashMap<>());
//        colors.forEach( (color, amplifier) -> {
//            if (color != -1) finalColor.set(ARGB.multiply(finalColor.get(), ARGB.opaque(color)));
//        });

        return finalColor.get();
    }
}
