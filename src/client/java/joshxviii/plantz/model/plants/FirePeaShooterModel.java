package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.FirePeaAnimation;
import joshxviii.plantz.renderer.entity.plant.PlantRenderState;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class FirePeaShooterModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("fire_peashooter"), "main");
	private final ModelPart body;
	private final ModelPart stem;
	private final ModelPart stem_2;
	private final ModelPart head;
	private final ModelPart barrel;
	private final ModelPart head_leaf;
	private final ModelPart head_leaf_mid;
	private final ModelPart head_leaf_tip;
	private final ModelPart head_leaf2;
	private final ModelPart head_leaf_mid2;
	private final ModelPart head_leaf_tip2;
	private final ModelPart head_leaf3;
	private final ModelPart head_leaf_mid3;
	private final ModelPart head_leaf_tip3;
	private final ModelPart head_leaf4;
	private final ModelPart leaves;
	private final ModelPart leaf_1;
	private final ModelPart leaf_tip_1;
	private final ModelPart leaf_2;
	private final ModelPart leaf_tip_2;
	private final ModelPart leaf_3;
	private final ModelPart leaf_tip_3;
	private final ModelPart leaf_4;
	private final ModelPart leaf_tip_4;

	public FirePeaShooterModel(ModelPart root) {
		super(root);

		initAnimation = FirePeaAnimation.init.bake(root);
		idleAnimation = FirePeaAnimation.idle.bake(root);
		actionAnimation = FirePeaAnimation.action.bake(root);
		sleepAnimation = FirePeaAnimation.sleep.bake(root);

		this.body = root.getChild("body");
		this.stem = this.body.getChild("stem");
		this.stem_2 = this.stem.getChild("stem_2");
		this.head = this.stem_2.getChild("head");
		this.barrel = this.head.getChild("barrel");
		this.head_leaf = this.head.getChild("head_leaf");
		this.head_leaf_mid = this.head_leaf.getChild("head_leaf_mid");
		this.head_leaf_tip = this.head_leaf_mid.getChild("head_leaf_tip");
		this.head_leaf2 = this.head.getChild("head_leaf2");
		this.head_leaf_mid2 = this.head_leaf2.getChild("head_leaf_mid2");
		this.head_leaf_tip2 = this.head_leaf_mid2.getChild("head_leaf_tip2");
		this.head_leaf3 = this.head.getChild("head_leaf3");
		this.head_leaf_mid3 = this.head_leaf3.getChild("head_leaf_mid3");
		this.head_leaf_tip3 = this.head_leaf_mid3.getChild("head_leaf_tip3");
		this.head_leaf4 = this.head.getChild("head_leaf4");
		this.leaves = this.body.getChild("leaves");
		this.leaf_1 = this.leaves.getChild("leaf_1");
		this.leaf_tip_1 = this.leaf_1.getChild("leaf_tip_1");
		this.leaf_2 = this.leaves.getChild("leaf_2");
		this.leaf_tip_2 = this.leaf_2.getChild("leaf_tip_2");
		this.leaf_3 = this.leaves.getChild("leaf_3");
		this.leaf_tip_3 = this.leaf_3.getChild("leaf_tip_3");
		this.leaf_4 = this.leaves.getChild("leaf_4");
		this.leaf_tip_4 = this.leaf_4.getChild("leaf_tip_4");
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

		PartDefinition barrel = head.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(18, 25).addBox(-2.5F, -2.5F, -2.0F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -5.0F));

		PartDefinition head_leaf = head.addOrReplaceChild("head_leaf", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -4.9F, -2.0833F, 0.0F, -1.5708F, 1.5708F));

		PartDefinition cube_r1 = head_leaf.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0833F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_mid = head_leaf.addOrReplaceChild("head_leaf_mid", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 3.0833F));

		PartDefinition cube_r2 = head_leaf_mid.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(6, 25).addBox(0.0F, -5.0F, 0.0F, 0.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_tip = head_leaf_mid.addOrReplaceChild("head_leaf_tip", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 7.0F));

		PartDefinition cube_r3 = head_leaf_tip.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(20, 29).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf2 = head.addOrReplaceChild("head_leaf2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -5.9F, -0.0833F, 0.0F, -1.5708F, 1.5708F));

		PartDefinition cube_r4 = head_leaf2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0833F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_mid2 = head_leaf2.addOrReplaceChild("head_leaf_mid2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 3.0833F));

		PartDefinition cube_r5 = head_leaf_mid2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(6, 25).addBox(0.0F, -5.0F, 0.0F, 0.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_tip2 = head_leaf_mid2.addOrReplaceChild("head_leaf_tip2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 7.0F));

		PartDefinition cube_r6 = head_leaf_tip2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(20, 29).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf3 = head.addOrReplaceChild("head_leaf3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -5.9F, 2.9167F, 0.0F, -1.5708F, 1.5708F));

		PartDefinition cube_r7 = head_leaf3.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0833F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_mid3 = head_leaf3.addOrReplaceChild("head_leaf_mid3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 3.0833F));

		PartDefinition cube_r8 = head_leaf_mid3.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(6, 25).addBox(0.0F, -5.0F, 0.0F, 0.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_tip3 = head_leaf_mid3.addOrReplaceChild("head_leaf_tip3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 7.0F));

		PartDefinition cube_r9 = head_leaf_tip3.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(20, 29).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf4 = head.addOrReplaceChild("head_leaf4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.0F, 2.9167F, 0.0F, 0.0F, 1.5708F));

		PartDefinition cube_r10 = head_leaf4.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(6, 35).addBox(0.0F, -4.0F, -2.0F, 0.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0833F, 0.0F, 0.0F, 1.5708F));

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
		this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
		super.setupAnim(state);
		this.stem.yRot = state.yRot * (float) (Math.PI / 180.0);
	}
}