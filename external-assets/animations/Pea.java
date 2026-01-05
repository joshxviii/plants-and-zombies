// Save this class in your mod and generate all required imports

/**
 * Made with Blockbench 5.0.7
 * Exported for Minecraft version 1.19 or later with Mojang mappings
 * @author Author
 */
public class PeaAnimation {
	public static final AnimationDefinition shoot = AnimationDefinition.Builder.withLength(0.72F).looping()
		.addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.72F, KeyframeAnimations.degreeVec(0.0F, -360.0F, 360.0F), AnimationChannel.Interpolations.LINEAR)
		))
		.build();
}