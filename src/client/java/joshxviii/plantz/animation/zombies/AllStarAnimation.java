package joshxviii.plantz.animation.zombies;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * @author Josh
 */
public class AllStarAnimation {
        public static final AnimationDefinition action = AnimationDefinition.Builder.withLength(0.44F).looping()
                .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(2.0F, -1.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.12F, KeyframeAnimations.posVec(2.0F, -0.5F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.28F, KeyframeAnimations.posVec(2.0F, -1.5F, -4.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.44F, KeyframeAnimations.posVec(2.0F, -1.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(17.5F, -47.5F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.28F, KeyframeAnimations.degreeVec(14.0267F, -33.0515F, 5.3278F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.44F, KeyframeAnimations.degreeVec(17.5F, -47.5F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(1.0F, 0.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-3.0171F, -6.8692F, 21.2166F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(0.51F, -0.36F, -0.29F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.28F, KeyframeAnimations.posVec(0.51F, 0.64F, -0.29F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.44F, KeyframeAnimations.posVec(0.51F, -0.36F, -0.29F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(86.4904F, -58.1621F, -42.7866F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(4.0F, 0.0F, -6.25F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.12F, KeyframeAnimations.posVec(3.43F, -0.12F, -6.25F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.28F, KeyframeAnimations.posVec(2.5F, 1.0F, -6.25F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.44F, KeyframeAnimations.posVec(4.0F, 0.0F, -6.25F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .build();

        public static final AnimationDefinition idle = AnimationDefinition.Builder.withLength(0.0F).looping()
                .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-91.0053F, 0.0F, -3.1572F), AnimationChannel.Interpolations.LINEAR)
                ))
                .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-91.0053F, 0.0F, 3.1572F), AnimationChannel.Interpolations.LINEAR)
                ))
                .build();

        public static final AnimationDefinition walk = AnimationDefinition.Builder.withLength(0.0F).looping()
                .addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-6.7986F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                ))
                .addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(6.7986F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
                ))
                .build();
}
