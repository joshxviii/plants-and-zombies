// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


package joshxviii.plantz.model.zombies;

import joshxviii.plantz.animation.zombies.GraveDiggerAnimation;
import joshxviii.plantz.renderer.RenderingUtilsKt;
import joshxviii.plantz.renderer.entity.zombie.PazZombieRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class GraveDiggerModel<S extends PazZombieRenderState> extends PazZombieModel<S> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("grave_digger"), "main");
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation actionAnimation;
	private final KeyframeAnimation walkAnimation;
	private final ModelPart big_hat;


	public GraveDiggerModel(final ModelPart root) {
		super(null, root);
		this.idleAnimation = GraveDiggerAnimation.idle.bake(root);
		this.walkAnimation = GraveDiggerAnimation.walk.bake(root);
		this.actionAnimation = GraveDiggerAnimation.action.bake(root);
		this.big_hat = RenderingUtilsKt.getChildOrNull(this.getHead(), "big_hat");
	}

	@Override
	public <T extends PazZombieRenderState> PazZombieModel<T> forArmor(ModelPart root) {
		return new GraveDiggerModel<>(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition big_hat = head.addOrReplaceChild("big_hat", CubeListBuilder.create().texOffs(66, 0).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(42, 14).addBox(-11.0F, 0.0F, -11.0F, 22.0F, 0.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.175F, 0.1184F, 0.1036F));

		PartDefinition cube_r1 = big_hat.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(96, 4).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -3.25F, -5.25F, -0.0342F, 0.6503F, 0.3776F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition hair = head.addOrReplaceChild("hair", CubeListBuilder.create(), PartPose.offset(0.0F, -7.25F, -0.25F));

		PartDefinition cube_r2 = hair.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(72, 63).addBox(-3.0F, -2.0F, 0.0F, 6.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.25F, -4.25F, -0.0873F, 0.0F, -0.1745F));

		PartDefinition hair_back = hair.addOrReplaceChild("hair_back", CubeListBuilder.create().texOffs(66, 36).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 13.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(64, 51).addBox(-7.0F, 6.0F, 0.0F, 14.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.25F, 4.25F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r3 = body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 82).addBox(-2.0F, -4.5F, -2.5F, 4.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.5F, 0.0F, 0.0F, 0.0F, -1.2654F));

		PartDefinition Gravestone = body.addOrReplaceChild("Gravestone", CubeListBuilder.create().texOffs(0, 64).addBox(-6.0F, -8.0F, -2.0F, 12.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(32, 74).addBox(-8.0F, 6.0F, -3.0F, 16.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 4.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(48, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(40, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 48).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull S state) {
		super.setupAnim(state);
		this.resetPose();
		if (this.big_hat != null) this.big_hat.visible = (state.headItem.isEmpty() && state.headEquipment.isEmpty());
		idleAnimation.applyStatic();

		AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, false, state);

		walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2f, 2f);
		actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
	}
}
