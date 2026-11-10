package joshxviii.plantz.mixin.client;

import joshxviii.plantz.PazComponents;
import joshxviii.plantz.animation.HumanoidModelAnimation;
import joshxviii.plantz.renderer.RenderingUtilsKt;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

import static joshxviii.plantz.PazModels.HAS_HYPNO_KEY;
import static joshxviii.plantz.PazModels.IS_SKATING;

/**
 * @author Josh
 */
@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {

    @Unique
    private KeyframeAnimation skateAnimation;

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

    @Inject(method = "<init>(Lnet/minecraft/client/model/geom/ModelPart;Ljava/util/function/Function;)V", at = @At("TAIL"))
    public void addAnim(ModelPart root, Function<String, ModelPart> renderType, CallbackInfo ci) {
        skateAnimation = HumanoidModelAnimation.roller_skate.bake(root);
    }

    @Inject(method = "setupAnim*", at = @At("TAIL"))
    public void setupAnim(@Coerce Object o, CallbackInfo ci) {
        if (!(o instanceof HumanoidRenderState state)) return;

        var isSkating = state.getDataOrDefault(IS_SKATING, false);
        if (isSkating){
            var pos = state.walkAnimationPos;
            var speed = state.walkAnimationSpeed;
            var f = 1-(speed);
            RenderingUtilsKt.scaleDownRotation(leftArm, f);
            RenderingUtilsKt.scaleDownRotation(rightArm, f);
            RenderingUtilsKt.scaleDownRotation(leftLeg, f);
            RenderingUtilsKt.scaleDownRotation(rightLeg, f);
            skateAnimation.applyWalk(pos, speed, 2f, 1f);
        }

        var hasBodyBlocker = state.chestEquipment.has(PazComponents.WORN_BODY_BLOCKER);
        if (hasBodyBlocker) {
            head.x = 0f;
            head.z = 0f;
            head.xRot = body.xRot;
            head.yRot = body.yRot;
            head.zRot = body.zRot;
        }
    }
}
