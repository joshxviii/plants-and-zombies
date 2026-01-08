package joshxviii.plantz.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.PeaShooterAnimation;
import joshxviii.plantz.animation.RepeaterAnimation;
import joshxviii.plantz.entity.Repeater;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;
import static joshxviii.plantz.UtilsKt.pazResource;

public class RepeaterModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("repeater"), "main");
	private final ModelPart body;
	private final ModelPart stem;
	private final ModelPart stem_2;
	private final ModelPart head;
	private final ModelPart eyebrow;
	private final ModelPart left_brow;
	private final ModelPart right_brow;
	private final ModelPart barrel;
	private final ModelPart head_leaf;
	private final ModelPart head_leaf_tip;
	private final ModelPart head_leaf_2;
	private final ModelPart head_leaf_tip_2;
	private final ModelPart head_leaf_3;
	private final ModelPart head_leaf_tip_3;
	private final ModelPart leaves;
	private final ModelPart leaf_1;
	private final ModelPart leaf_tip_1;
	private final ModelPart leaf_2;
	private final ModelPart leaf_tip_2;
	private final ModelPart leaf_3;
	private final ModelPart leaf_tip_3;
	private final ModelPart leaf_4;
	private final ModelPart leaf_tip_4;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation actionAnimation;

	public RepeaterModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.stem = this.body.getChild("stem");
		this.stem_2 = this.stem.getChild("stem_2");
		this.head = this.stem_2.getChild("head");
		this.eyebrow = this.head.getChild("eyebrow");
		this.left_brow = this.eyebrow.getChild("left_brow");
		this.right_brow = this.eyebrow.getChild("right_brow");
		this.barrel = this.head.getChild("barrel");
		this.head_leaf = this.head.getChild("head_leaf");
		this.head_leaf_tip = this.head_leaf.getChild("head_leaf_tip");
		this.head_leaf_2 = this.head.getChild("head_leaf_2");
		this.head_leaf_tip_2 = this.head_leaf_2.getChild("head_leaf_tip_2");
		this.head_leaf_3 = this.head.getChild("head_leaf_3");
		this.head_leaf_tip_3 = this.head_leaf_3.getChild("head_leaf_tip_3");
		this.leaves = this.body.getChild("leaves");
		this.leaf_1 = this.leaves.getChild("leaf_1");
		this.leaf_tip_1 = this.leaf_1.getChild("leaf_tip_1");
		this.leaf_2 = this.leaves.getChild("leaf_2");
		this.leaf_tip_2 = this.leaf_2.getChild("leaf_tip_2");
		this.leaf_3 = this.leaves.getChild("leaf_3");
		this.leaf_tip_3 = this.leaf_3.getChild("leaf_tip_3");
		this.leaf_4 = this.leaves.getChild("leaf_4");
		this.leaf_tip_4 = this.leaf_4.getChild("leaf_tip_4");
		this.idleAnimation = RepeaterAnimation.idle.bake(root);
		this.actionAnimation = RepeaterAnimation.action.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition stem = body.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(4, 25).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition stem_2 = stem.addOrReplaceChild("stem_2", CubeListBuilder.create().texOffs(4, 18).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(2, 12).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition head = stem_2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(19, 19).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition eyebrow = head.addOrReplaceChild("eyebrow", CubeListBuilder.create(), PartPose.offset(0.0F, -5.25F, -3.1F));

		PartDefinition left_brow = eyebrow.addOrReplaceChild("left_brow", CubeListBuilder.create().texOffs(36, 23).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition right_brow = eyebrow.addOrReplaceChild("right_brow", CubeListBuilder.create().texOffs(36, 23).mirror().addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition barrel = head.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(18, 25).addBox(-2.5F, -2.5F, -2.0F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -5.0F));

		PartDefinition head_leaf = head.addOrReplaceChild("head_leaf", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 2.9167F));

		PartDefinition cube_r1 = head_leaf.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(27, -5).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0833F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_tip = head_leaf.addOrReplaceChild("head_leaf_tip", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 6.0833F));

		PartDefinition cube_r2 = head_leaf_tip.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(39, -3).addBox(0.0F, -4.0F, 3.0F, 0.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_2 = head.addOrReplaceChild("head_leaf_2", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 2.9167F));

		PartDefinition cube_r3 = head_leaf_2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(27, -5).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0833F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_tip_2 = head_leaf_2.addOrReplaceChild("head_leaf_tip_2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 6.0833F));

		PartDefinition cube_r4 = head_leaf_tip_2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(39, -3).addBox(0.0F, -4.0F, 3.0F, 0.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_3 = head.addOrReplaceChild("head_leaf_3", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 2.9167F));

		PartDefinition cube_r5 = head_leaf_3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(27, -5).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0833F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_tip_3 = head_leaf_3.addOrReplaceChild("head_leaf_tip_3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 6.0833F));

		PartDefinition cube_r6 = head_leaf_tip_3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(39, -3).addBox(0.0F, -4.0F, 3.0F, 0.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition leaves = body.addOrReplaceChild("leaves", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leaf_1 = leaves.addOrReplaceChild("leaf_1", CubeListBuilder.create().texOffs(31, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7071F, 0.0F, -0.6464F, 0.0F, -0.7854F, 0.0F));

		PartDefinition leaf_tip_1 = leaf_1.addOrReplaceChild("leaf_tip_1", CubeListBuilder.create().texOffs(31, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_2 = leaves.addOrReplaceChild("leaf_2", CubeListBuilder.create().texOffs(31, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7071F, 0.0F, 0.7678F, 0.0F, -2.3562F, 0.0F));

		PartDefinition leaf_tip_2 = leaf_2.addOrReplaceChild("leaf_tip_2", CubeListBuilder.create().texOffs(31, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_3 = leaves.addOrReplaceChild("leaf_3", CubeListBuilder.create().texOffs(31, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7071F, 0.0F, -0.6464F, 0.0F, 0.7854F, 0.0F));

		PartDefinition leaf_tip_3 = leaf_3.addOrReplaceChild("leaf_tip_3", CubeListBuilder.create().texOffs(31, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_4 = leaves.addOrReplaceChild("leaf_4", CubeListBuilder.create().texOffs(31, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7071F, 0.0F, 0.7678F, 0.0F, 2.3562F, 0.0F));

		PartDefinition leaf_tip_4 = leaf_4.addOrReplaceChild("leaf_tip_4", CubeListBuilder.create().texOffs(31, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.stem.yRot = state.yRot * (float) (Math.PI / 180.0);
		this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
		this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);	}
}