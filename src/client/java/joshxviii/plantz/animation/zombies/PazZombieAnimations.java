package joshxviii.plantz.animation.zombies;// Save this class in your mod and generate all required imports

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * Made with Blockbench 5.1.4
 * Exported for Minecraft version 1.19 or later with Mojang mappings
 * @author Author
 */
public class PazZombieAnimations {
	public static final AnimationDefinition idle = AnimationDefinition.Builder.withLength(0.0F).looping()
			.addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(-90.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(-90.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.build();

	public static final AnimationDefinition emerge = AnimationDefinition.Builder.withLength(2.0F)
			.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.32F, KeyframeAnimations.degreeVec(127.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.24F, KeyframeAnimations.degreeVec(46.17F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.68F, KeyframeAnimations.degreeVec(59.38F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.24F, KeyframeAnimations.degreeVec(34.67F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.32F, KeyframeAnimations.degreeVec(-90.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.76F, KeyframeAnimations.degreeVec(-6.5551F, -34.6893F, 4.9764F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.2F, KeyframeAnimations.degreeVec(64.4784F, -23.8636F, -2.2099F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.52F, KeyframeAnimations.degreeVec(70.8102F, -10.4915F, 7.6469F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.32F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.72F, KeyframeAnimations.posVec(0.0F, 2.0F, -5.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.96F, KeyframeAnimations.posVec(0.0F, 4.69F, -3.37F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.2F, KeyframeAnimations.posVec(0.0F, 2.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(-90.0F, -42.5F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.32F, KeyframeAnimations.degreeVec(-90.0811F, -22.4964F, 0.4087F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.6F, KeyframeAnimations.degreeVec(-69.7659F, -5.117F, -72.7517F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.76F, KeyframeAnimations.degreeVec(-31.9894F, 44.5586F, -40.5352F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.96F, KeyframeAnimations.degreeVec(68.7013F, 21.0F, 17.2951F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.2F, KeyframeAnimations.degreeVec(62.8323F, 13.6345F, -2.208F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.36F, KeyframeAnimations.degreeVec(62.1103F, 21.0024F, 3.9694F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.52F, KeyframeAnimations.degreeVec(82.031F, 14.5006F, 4.9088F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.12F, KeyframeAnimations.posVec(0.0F, 10.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.32F, KeyframeAnimations.posVec(0.0F, 9.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.6F, KeyframeAnimations.posVec(2.0F, 4.0F, -6.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.76F, KeyframeAnimations.posVec(2.0F, 2.0F, -6.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.96F, KeyframeAnimations.posVec(2.0F, 7.0F, -3.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.2F, KeyframeAnimations.posVec(1.0F, 4.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.36F, KeyframeAnimations.posVec(0.7F, 1.5F, -0.35F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.32F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.24F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.56F, KeyframeAnimations.degreeVec(1.7412F, 12.4945F, 0.3768F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.32F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.24F, KeyframeAnimations.posVec(0.0F, 2.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.56F, KeyframeAnimations.posVec(0.0F, 1.34F, 2.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.32F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.24F, KeyframeAnimations.degreeVec(17.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.24F, KeyframeAnimations.posVec(0.0F, 8.0F, -2.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.56F, KeyframeAnimations.posVec(0.0F, 6.35F, -2.34F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.addAnimation("root", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.32F, KeyframeAnimations.posVec(0.0F, -33.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(0.76F, KeyframeAnimations.posVec(0.0F, -23.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.04F, KeyframeAnimations.posVec(0.0F, -23.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.24F, KeyframeAnimations.posVec(0.0F, -16.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(1.6F, KeyframeAnimations.posVec(0.0F, -13.07F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
					new Keyframe(2.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
			))
			.build();

	public static final AnimationDefinition balloon_float = AnimationDefinition.Builder.withLength(0.0F).looping()
			.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(30.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(-24.2617F, 4.661F, 10.7802F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 2.0F, 7.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(-24.7873F, -3.7419F, -6.5045F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 2.0F, 7.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("root", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(45.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.addAnimation("root", new AnimationChannel(AnimationChannel.Targets.POSITION,
					new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -1.575F, -1.0F), AnimationChannel.Interpolations.LINEAR)
			))
			.build();
}