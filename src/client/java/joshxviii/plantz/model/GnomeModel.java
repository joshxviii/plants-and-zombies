package joshxviii.plantz.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import joshxviii.plantz.GnomeRenderState;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jspecify.annotations.NonNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class GnomeModel<T extends GnomeRenderState> extends EntityModel<T> implements ArmedModel<T>, HeadedModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("gnome"), "main");
	private final ModelPart body;
	private final ModelPart torso;
	private final ModelPart head;
	private final ModelPart eyebrows;
	private final ModelPart eyebrow_L;
	private final ModelPart eyebrow_R;
	private final ModelPart arms;
	private final ModelPart arm_L;
	private final ModelPart arm_R;
	private final ModelPart legs;
	private final ModelPart leg_L;
	private final ModelPart leg_R;

	public GnomeModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.torso = this.body.getChild("torso");
		this.head = this.torso.getChild("head");
		this.eyebrows = this.head.getChild("eyebrows");
		this.eyebrow_L = this.eyebrows.getChild("eyebrow_L");
		this.eyebrow_R = this.eyebrows.getChild("eyebrow_R");
		this.arms = this.torso.getChild("arms");
		this.arm_L = this.arms.getChild("arm_L");
		this.arm_R = this.arms.getChild("arm_R");
		this.legs = this.torso.getChild("legs");
		this.leg_L = this.legs.getChild("leg_L");
		this.leg_R = this.legs.getChild("leg_R");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 13).addBox(-2.0F, -4.0F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition head = torso.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(12, 2).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 13).addBox(-1.5F, -6.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(24, 14).addBox(-0.5F, -7.0F, -0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 8).addBox(-2.5F, -1.5F, -1.0F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, -2.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -2.05F));

		PartDefinition eyebrow_L = eyebrows.addOrReplaceChild("eyebrow_L", CubeListBuilder.create().texOffs(14, 12).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 0.0F, -0.05F));

		PartDefinition eyebrow_R = eyebrows.addOrReplaceChild("eyebrow_R", CubeListBuilder.create().texOffs(18, 12).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 0.0F, -0.05F));

		PartDefinition arms = torso.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition arm_L = arms.addOrReplaceChild("arm_L", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -1.0F, 0.0F));

		PartDefinition arm_R = arms.addOrReplaceChild("arm_R", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -1.0F, 0.0F));

		PartDefinition legs = torso.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leg_L = legs.addOrReplaceChild("leg_L", CubeListBuilder.create().texOffs(0, 19).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, 0.0F));

		PartDefinition leg_R = legs.addOrReplaceChild("leg_R", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(@NotNull GnomeRenderState state) {
		head.xRot = state.xRot * (float) (Math.PI / 180.0);
		head.yRot = state.yRot * (float) (Math.PI / 180.0);
		float animationPos = state.walkAnimationPos;
		float animationSpeed = state.walkAnimationSpeed;
		arm_L.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
		arm_R.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
	}

	@Override
	public void translateToHand(T state, @NonNull HumanoidArm arm, @NonNull PoseStack poseStack) {
		root.translateAndRotate(poseStack);
		body.translateAndRotate(poseStack);
		torso.translateAndRotate(poseStack);
		arms.translateAndRotate(poseStack);
		getArm(arm).translateAndRotate(poseStack);
		poseStack.scale(0.55F, 0.55F, 0.55F);
		poseStack.translate(0.046875 * (arm == HumanoidArm.LEFT? 1f : -1f), -0.12, 0.078125);
	}

	@Override
	public @NonNull ModelPart getHead() {
		return this.head;
	}

	public ModelPart getArm(final HumanoidArm arm) {
		return arm == HumanoidArm.LEFT ? this.arm_L : this.arm_R;
	}

}