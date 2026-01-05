// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class PeaShooterModel<T extends PeaShooter> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "peashootermodel"), "main");
	private final ModelPart body;
	private final ModelPart stem;
	private final ModelPart stem_2;
	private final ModelPart head;
	private final ModelPart barrel;
	private final ModelPart head_leaf;
	private final ModelPart head_leaf_tip;
	private final ModelPart leaves;
	private final ModelPart leaf_1;
	private final ModelPart leaf_tip_1;
	private final ModelPart leaf_2;
	private final ModelPart leaf_tip_2;
	private final ModelPart leaf_3;
	private final ModelPart leaf_tip_3;
	private final ModelPart leaf_4;
	private final ModelPart leaf_tip_4;

	public PeaShooterModel(ModelPart root) {
		this.body = root.getChild("body");
		this.stem = this.body.getChild("stem");
		this.stem_2 = this.stem.getChild("stem_2");
		this.head = this.stem_2.getChild("head");
		this.barrel = this.head.getChild("barrel");
		this.head_leaf = this.head.getChild("head_leaf");
		this.head_leaf_tip = this.head_leaf.getChild("head_leaf_tip");
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

		PartDefinition head_leaf = head.addOrReplaceChild("head_leaf", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 2.9167F));

		PartDefinition cube_r1 = head_leaf.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(30, -2).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0833F, 0.0F, 0.0F, 1.5708F));

		PartDefinition head_leaf_tip = head_leaf.addOrReplaceChild("head_leaf_tip", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 3.0833F));

		PartDefinition cube_r2 = head_leaf_tip.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(36, -6).addBox(0.0F, -4.0F, 0.0F, 0.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

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
	public void setupAnim(PeaShooter entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}