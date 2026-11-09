package joshxviii.plantz.mixin.client;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Josh
 */
@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    @Unique
    private final KeyframeAnimation skateAnimation;

    @Shadow @Final
    public ModelPart head;
    @Shadow @Final
    public ModelPart hat;
    @Shadow @Final
    public ModelPart rightArm;
    @Shadow @Final
    public ModelPart leftArm;
    @Shadow @Final
    public ModelPart rightLeg;
    @Shadow @Final
    public ModelPart leftLeg;
    @Shadow @Final
    public ModelPart body;

    public HumanoidModelMixin(KeyframeAnimation skateAnimation) {
        this.skateAnimation = skateAnimation;
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    public void addAnim(CallbackInfo ci) {
        skateAnimation
    }

    @Inject(method = "setupAnim*", at = @At("TAIL"))
    public void setupAnim(@Coerce Object o, CallbackInfo ci) {
        if (!(o instanceof HumanoidRenderState state)) return;
        state
    }

}
