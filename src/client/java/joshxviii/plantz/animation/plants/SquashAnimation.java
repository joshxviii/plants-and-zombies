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
public class SquashAnimation {
	public static final AnimationDefinition idle = AnimationDefinition.Builder.withLength(4.0F).looping()
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.48F, KeyframeAnimations.scaleVec(0.9344F, 1.0656F, 0.9344F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.52F, KeyframeAnimations.scaleVec(1.05F, 0.9F, 1.05F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(4.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("brow", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.48F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.52F, KeyframeAnimations.degreeVec(0.0F, 0.0F, -5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(4.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("brow", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.48F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.52F, KeyframeAnimations.posVec(0.0F, -0.25F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	public static final AnimationDefinition action = AnimationDefinition.Builder.withLength(4.0F)
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(3.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(3.64F, KeyframeAnimations.posVec(-1.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(3.68F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(3.72F, KeyframeAnimations.posVec(1.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(3.76F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(3.8F, KeyframeAnimations.posVec(-0.5F, 0.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(3.84F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.5F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(3.88F, KeyframeAnimations.posVec(0.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(3.92F, KeyframeAnimations.posVec(-0.25F, 0.0F, 0.5F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(4.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.scaleVec(1.2F, 0.9F, 1.2F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.6F, KeyframeAnimations.scaleVec(0.9F, 1.1F, 0.9F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.88F, KeyframeAnimations.scaleVec(1.1F, 1.0F, 1.1F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.04F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.4F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.52F, KeyframeAnimations.scaleVec(0.7F, 1.5F, 0.7F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.64F, KeyframeAnimations.scaleVec(1.5F, 0.7F, 1.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.76F, KeyframeAnimations.scaleVec(1.5F, 0.7F, 1.5F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.88F, KeyframeAnimations.scaleVec(1.1F, 1.0F, 1.1F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(4.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	public static final AnimationDefinition init = AnimationDefinition.Builder.withLength(1.0F)
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.2F, KeyframeAnimations.degreeVec(-7.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.32F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.44F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.56F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 4.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.44F, KeyframeAnimations.posVec(0.0F, 4.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.52F, KeyframeAnimations.posVec(0.0F, -0.5F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.56F, KeyframeAnimations.posVec(-1.0F, -0.5F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.6F, KeyframeAnimations.posVec(0.0F, 0.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.64F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.68F, KeyframeAnimations.posVec(0.0F, 0.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.72F, KeyframeAnimations.posVec(-1.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.76F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.8F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.2F, KeyframeAnimations.scaleVec(0.6F, 1.0F, 0.6F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.32F, KeyframeAnimations.scaleVec(0.9F, 0.8F, 0.9F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.44F, KeyframeAnimations.scaleVec(0.7F, 1.2F, 0.7F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.52F, KeyframeAnimations.scaleVec(1.3F, 0.8F, 1.3F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.72F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	public static final AnimationDefinition sleep = AnimationDefinition.Builder.withLength(6.0F).looping()
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.0F, KeyframeAnimations.degreeVec(-2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(4.0F, KeyframeAnimations.degreeVec(2.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(5.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(6.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
					new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(3.0F, KeyframeAnimations.scaleVec(0.9F, 1.1F, 0.9F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(4.0F, KeyframeAnimations.scaleVec(1.05F, 0.95F, 1.05F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(5.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(6.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();
}