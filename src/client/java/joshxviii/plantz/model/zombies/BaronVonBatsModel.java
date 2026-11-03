// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


package joshxviii.plantz.model.zombies;

import joshxviii.plantz.animation.zombies.BaronVonBatsAnimation;
import joshxviii.plantz.animation.zombies.DiscoZombieAnimation;
import joshxviii.plantz.renderer.RenderingUtilsKt;
import joshxviii.plantz.renderer.entity.zombie.BaronVonBatsRenderState;
import joshxviii.plantz.renderer.entity.zombie.DiggerZombieRenderState;
import joshxviii.plantz.renderer.entity.zombie.PazZombieRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class BaronVonBatsModel<S extends PazZombieRenderState> extends PazZombieModel<S> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("baron_von_bats"), "main");
	private final KeyframeAnimation walkAnimation;
	private final KeyframeAnimation summonAnimation;
	private final KeyframeAnimation vanishAnimation;
	private final KeyframeAnimation appearAnimation;
	private final KeyframeAnimation transformBatAnimation;
	ModelPart top_hat;

	public BaronVonBatsModel(final ModelPart root) {
		super(null, root);
		var mainRoot = root.hasChild("root") ? root.getChild("root") : root;
		this.walkAnimation = BaronVonBatsAnimation.walk.bake(mainRoot);
		this.summonAnimation = BaronVonBatsAnimation.summon.bake(mainRoot);
		this.vanishAnimation = BaronVonBatsAnimation.vanish.bake(mainRoot);
		this.appearAnimation = BaronVonBatsAnimation.emerge.bake(mainRoot);
		this.transformBatAnimation = BaronVonBatsAnimation.transform.bake(mainRoot);
		top_hat = getHead().getChild("top_hat");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 4.6667F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -4.6667F, 0.0F));

		PartDefinition top_hat = head.addOrReplaceChild("top_hat", CubeListBuilder.create().texOffs(64, 0).addBox(-4.0F, -8.5F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.5F))
				.texOffs(56, 17).addBox(-6.0F, -1.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition cube_r1 = top_hat.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(56, 17).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition hair = head.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(67, 39).addBox(-5.0F, -6.9F, 1.5F, 10.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(64, 31).addBox(-6.0F, -3.5F, 2.0F, 12.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -4.6667F, 0.0F));

		PartDefinition Body_r1 = body.addOrReplaceChild("Body_r1", CubeListBuilder.create().texOffs(68, 41).mirror().addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition Body_r2 = body.addOrReplaceChild("Body_r2", CubeListBuilder.create().texOffs(80, 41).addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, -2.6667F, 0.0F));

		PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(48, 48).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(5.0F, -2.6667F, 0.0F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.9F, 7.3333F, 0.0F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(1.9F, 7.3333F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull S state) {
		super.setupAnim(state);
		super.resetPose();
		AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, false, state);
		this.top_hat.visible = (state.headItem.isEmpty() && state.headEquipment.isEmpty());

		this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
		this.head.yRot = state.yRot * (float) (Math.PI / 180.0);

		if (!(state instanceof BaronVonBatsRenderState baronState)) return;
		walkAnimation.applyWalk(baronState.walkAnimationPos, baronState.walkAnimationSpeed, 2f, 2f);
		summonAnimation.apply(baronState.getActionAnimationState(), state.ageInTicks);
		transformBatAnimation.apply(baronState.getTransformAnimationState(), state.ageInTicks);
		var armRot = state.xRot * (Mth.PI / 180f) - Mth.HALF_PI;
		if (state.isUsingItem) {
			if (state.mainArm == HumanoidArm.RIGHT) this.rightArm.xRot = armRot;
			else this.leftArm.xRot = armRot;
		}
		var appearing = baronState.isAppearing();
		(appearing ? appearAnimation : vanishAnimation).apply(baronState.getTeleportAnimationState(), state.ageInTicks);
	}
}
