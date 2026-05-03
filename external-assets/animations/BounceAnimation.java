// Save this class in your mod and generate all required imports

/**
 * Made with Blockbench 5.1.4
 * Exported for Minecraft version 1.19 or later with Mojang mappings
 * @author Author
 */
public class ChomperAnimation {
	public static final AnimationDefinition feed = AnimationDefinition.Builder.withLength(0.48F)
		.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE, 
			new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.08F, KeyframeAnimations.scaleVec(1.2F, 0.9F, 1.2F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.2F, KeyframeAnimations.scaleVec(0.8F, 1.2F, 0.8F), AnimationChannel.Interpolations.CATMULLROM),
			new Keyframe(0.36F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
		))
		.build();
}