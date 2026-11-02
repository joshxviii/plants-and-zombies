package joshxviii.plantz.animation.blueprint_machines;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * @author Josh
 */
public class LawnMowerAnimation {
        public static final AnimationDefinition mow = AnimationDefinition.Builder.withLength(0.52F).looping()
                .addAnimation("motor", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.04F, KeyframeAnimations.posVec(-0.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.08F, KeyframeAnimations.posVec(0.04F, -0.25F, -0.5F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.12F, KeyframeAnimations.posVec(0.54F, 0.0F, 0.05F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.16F, KeyframeAnimations.posVec(0.99F, -0.25F, -0.45F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.2F, KeyframeAnimations.posVec(-0.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.24F, KeyframeAnimations.posVec(0.04F, 0.0F, -0.5F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.28F, KeyframeAnimations.posVec(0.54F, -0.25F, 0.05F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.32F, KeyframeAnimations.posVec(-0.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.36F, KeyframeAnimations.posVec(0.04F, 0.0F, -0.5F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.4F, KeyframeAnimations.posVec(0.54F, 0.0F, 0.05F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.44F, KeyframeAnimations.posVec(0.99F, -0.25F, -0.45F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.48F, KeyframeAnimations.posVec(-0.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.52F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                ))
                .build();
}
