package joshxviii.plantz.animation.zombies;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * @author Josh
 */
public class RaZombieAnimation {
        public static final AnimationDefinition action = AnimationDefinition.Builder.withLength(2.0F).looping()
                .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.48F, KeyframeAnimations.degreeVec(-22.3265F, -2.8631F, -6.9349F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.degreeVec(-32.4389F, -0.184F, -2.7113F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.52F, KeyframeAnimations.degreeVec(-24.7369F, 3.832F, 3.6273F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-83.4608F, -20.7383F, -0.9066F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.56F, KeyframeAnimations.degreeVec(-108.8436F, -21.4058F, 7.0999F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.08F, KeyframeAnimations.degreeVec(-83.4608F, -20.7383F, -0.9066F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.6F, KeyframeAnimations.degreeVec(-108.8436F, -21.4058F, 7.0999F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(-83.4608F, -20.7383F, -0.9066F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(2.0F, 0.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.48F, KeyframeAnimations.posVec(0.0F, -1.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.posVec(2.0F, 1.0F, -5.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.52F, KeyframeAnimations.posVec(4.0F, -1.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.posVec(2.0F, 0.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0.0F, KeyframeAnimations.degreeVec(-84.1487F, 20.7048F, -9.0072F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.56F, KeyframeAnimations.degreeVec(-106.6487F, 20.7048F, -9.0072F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.08F, KeyframeAnimations.degreeVec(-84.1487F, 20.7048F, -9.0072F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.6F, KeyframeAnimations.degreeVec(-106.6487F, 20.7048F, -9.0072F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.degreeVec(-84.1487F, 20.7048F, -9.0072F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0.0F, KeyframeAnimations.posVec(-2.0F, 0.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(0.48F, KeyframeAnimations.posVec(-4.0F, -1.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.0F, KeyframeAnimations.posVec(-2.0F, 1.0F, -5.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(1.52F, KeyframeAnimations.posVec(0.0F, -1.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
                        new Keyframe(2.0F, KeyframeAnimations.posVec(-2.0F, 0.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM)
                ))
                .build();
}
