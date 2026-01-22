package joshxviii.plantz.mixin.client;

import joshxviii.plantz.PazEffects;
import joshxviii.plantz.mixin.LivingEntityAccessor;
import joshxviii.plantz.mixin.LivingEntityMixin;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Unique
    private static final RenderStateDataKey<Boolean> IS_HYPNOTIZED_KEY = RenderStateDataKey.create(() -> "plantz:hypnotized");

    @Inject(method = "extractRenderState*", at = @At("TAIL"))
    private void checkForHypnoEffect(T entity, S state, float partialTicks, CallbackInfo ci) {
        boolean hasHypno = ((LivingEntityAccessor) entity).plantz$getHypnoId();
        state.setData(IS_HYPNOTIZED_KEY, hasHypno);
    }

    @Inject(method = "getModelTint", at = @At("RETURN"), cancellable = true)
    public void applyHypnoTint(S state, CallbackInfoReturnable<Integer> cir){
        boolean isHypno = state.getDataOrDefault(IS_HYPNOTIZED_KEY, false);
        if (isHypno) {
            cir.setReturnValue(0xFFD036FF);
        }
    }
}
