package joshxviii.plantz.animation.plants;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * Made with Blockbench 5.0.7
 * Exported for Minecraft version 1.19 or later with Mojang mappings
 * @author Nate
 */
public class IceShroomAnimation {
	public static final AnimationDefinition idle = AnimationDefinition.Builder.withLength(2.0F).looping()
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.96F, KeyframeAnimations.scaleVec(1.1F, 0.9F, 1.1F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("cap", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	public static final AnimationDefinition init = AnimationDefinition.Builder.withLength(1.0F)
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -6.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.28F, KeyframeAnimations.posVec(0.0F, -6.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.28F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.76F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.8F, KeyframeAnimations.scaleVec(1.0F, 1.1F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.88F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.04F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.08F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.12F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.16F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.2F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.24F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.28F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.32F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.36F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.4F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.44F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.48F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.52F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.56F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.64F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.68F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.72F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.76F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.8F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.84F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.88F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.96F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("cap", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.72F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.8F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.88F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	public static final AnimationDefinition sleep = AnimationDefinition.Builder.withLength(4.0F).looping()
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.48F, KeyframeAnimations.scaleVec(1.0F, 1.1F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.0F, KeyframeAnimations.scaleVec(1.1F, 0.9397F, 1.1F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(4.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	public static final AnimationDefinition action = AnimationDefinition.Builder.withLength(0.72F)
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.2F, KeyframeAnimations.degreeVec(16.99F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.32F, KeyframeAnimations.degreeVec(9.49F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.4F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.56F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.08F, KeyframeAnimations.scaleVec(1.06F, 0.94F, 1.06F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.32F, KeyframeAnimations.scaleVec(1.06F, 0.94F, 1.06F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.4F, KeyframeAnimations.scaleVec(0.8F, 1.2F, 0.8F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.52F, KeyframeAnimations.scaleVec(1.1F, 0.9F, 1.1F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.6F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("cap", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.32F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.4F, KeyframeAnimations.degreeVec(-35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.56F, KeyframeAnimations.degreeVec(7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.72F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
}