// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


package joshxviii.plantz.model.zombies;

import joshxviii.plantz.animation.zombies.DiggerZombieAnimation;
import joshxviii.plantz.renderer.entity.zombie.DiggerZombieRenderState;
import joshxviii.plantz.renderer.entity.zombie.PazZombieRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class DiggerZombieModel<S extends PazZombieRenderState> extends PazZombieModel<S> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("digger"), "main");
	private final KeyframeAnimation mineBlockAnimation;
	private final KeyframeAnimation walkAnimation;
	private final KeyframeAnimation tunnelDownAnimation;
	private final KeyframeAnimation tunnelUpAnimation;

	public DiggerZombieModel(final ModelPart root) {
		super(null, root);
		//TODO: figure out how to properly determine the root for custom Paz Zombies :,)
		var mainRoot = root.hasChild("root") ? root.getChild("root") : root;
		this.mineBlockAnimation = DiggerZombieAnimation.action.bake(mainRoot);
		this.walkAnimation = DiggerZombieAnimation.walk.bake(mainRoot);
		this.tunnelDownAnimation = DiggerZombieAnimation.tunnel_down.bake(mainRoot);
		this.tunnelUpAnimation = DiggerZombieAnimation.tunnel_up.bake(mainRoot);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 18).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 36).addBox(-4.5F, 5.9217F, -4.5078F, 9.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition helmet_r1 = head.addOrReplaceChild("helmet_r1", CubeListBuilder.create().texOffs(0, 9).addBox(-1.5F, -3.0F, -6.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(28, 5).addBox(-4.5F, -4.0F, -4.5F, 9.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(23, 18).addBox(-5.5F, 0.0F, -5.5F, 11.0F, 0.0F, 11.0F, new CubeDeformation(0.0F))
				.texOffs(0, 2).addBox(-1.5F, -5.0F, -5.5F, 3.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.5F, 0.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 48).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -10.0F, 0.0F));

		PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(48, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	//TODO fix swing animation
	@Override
	public void setupAnim(@NotNull S state) {
		super.setupAnim(state);
		this.resetPose();
		AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, state.isAggressive, state);

		this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
		this.head.yRot = state.yRot * (float) (Math.PI / 180.0);

		if (!(state instanceof DiggerZombieRenderState diggerState)) return;
		walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2f, 2f);
		mineBlockAnimation.apply(diggerState.getActionAnimationState(), state.ageInTicks);

		var tunnelUp = diggerState.isTunnelingUp();
		(tunnelUp ? tunnelUpAnimation : tunnelDownAnimation).apply(diggerState.getTunnelAnimationState(), state.ageInTicks);
	}
}
