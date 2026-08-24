package joshxviii.plantz.mixin.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import joshxviii.plantz.PaintInfoUniforms;
import joshxviii.plantz.PazRenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Josh
 */
@Mixin(RenderPass.class)
public abstract class PaintShaderUniformMixin {
    @Shadow
    public abstract void setUniform(String name, GpuBuffer value);

    @Inject(method = "setPipeline", at = @At("TAIL"))
    private void paint(RenderPipeline pipeline, CallbackInfo ci) {
        if (pipeline != PazRenderPipelines.PAINT_OVERLAY) return;
        setUniform("PaintInfo", PaintInfoUniforms.INSTANCE.slice().buffer());
    }
}
