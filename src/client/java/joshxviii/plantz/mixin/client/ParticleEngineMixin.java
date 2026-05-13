package joshxviii.plantz.mixin.client;

import joshxviii.plantz.particles.ElectricArcParticleGroup;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticlesRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @author Josh
 */
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Shadow
    @Final
    private static List<ParticleRenderType> RENDER_ORDER;

    @Inject(method = "createParticleGroup", at = @At("HEAD"), cancellable = true)
    private void assignElectricArcGroup(ParticleRenderType type, CallbackInfoReturnable<ParticleGroup<?>> cir) {
        if (type == ElectricArcParticleGroup.ELECTRIC_ARC_GROUP) cir.setReturnValue(new ElectricArcParticleGroup((ParticleEngine) (Object) this));
    }

    @Inject(method = "extract", at = @At(value = "HEAD"))
    private void insertElectricArcGroup(ParticlesRenderState particlesRenderState, Frustum frustum, Camera camera, float partialTickTime, CallbackInfo ci) {
        if (!RENDER_ORDER.contains(ElectricArcParticleGroup.ELECTRIC_ARC_GROUP)) RENDER_ORDER.add(ElectricArcParticleGroup.ELECTRIC_ARC_GROUP);
    }
}
