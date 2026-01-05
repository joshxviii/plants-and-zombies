// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class ChomperModel<T extends Chomper> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "chompermodel"), "main");
	private final ModelPart body;
	private final ModelPart stem;
	private final ModelPart stem_2;
	private final ModelPart stem_3;
	private final ModelPart stem_4;
	private final ModelPart head;
	private final ModelPart head_top;
	private final ModelPart head_bottom;
	private final ModelPart toung;
	private final ModelPart toung_2;
	private final ModelPart toung_3;
	private final ModelPart leaves;
	private final ModelPart leaf_1;
	private final ModelPart leaf_mid_1;
	private final ModelPart leaf_tip_1;
	private final ModelPart leaf_2;
	private final ModelPart leaf_mid_2;
	private final ModelPart leaf_tip_2;
	private final ModelPart leaf_3;
	private final ModelPart leaf_mid_3;
	private final ModelPart leaf_tip_3;
	private final ModelPart leaf_4;
	private final ModelPart leaf_mid_4;
	private final ModelPart leaf_tip_4;

	public ChomperModel(ModelPart root) {
		this.body = root.getChild("body");
		this.stem = this.body.getChild("stem");
		this.stem_2 = this.stem.getChild("stem_2");
		this.stem_3 = this.stem_2.getChild("stem_3");
		this.stem_4 = this.stem_3.getChild("stem_4");
		this.head = this.stem_4.getChild("head");
		this.head_top = this.head.getChild("head_top");
		this.head_bottom = this.head.getChild("head_bottom");
		this.toung = this.head_bottom.getChild("toung");
		this.toung_2 = this.toung.getChild("toung_2");
		this.toung_3 = this.toung_2.getChild("toung_3");
		this.leaves = this.body.getChild("leaves");
		this.leaf_1 = this.leaves.getChild("leaf_1");
		this.leaf_mid_1 = this.leaf_1.getChild("leaf_mid_1");
		this.leaf_tip_1 = this.leaf_mid_1.getChild("leaf_tip_1");
		this.leaf_2 = this.leaves.getChild("leaf_2");
		this.leaf_mid_2 = this.leaf_2.getChild("leaf_mid_2");
		this.leaf_tip_2 = this.leaf_mid_2.getChild("leaf_tip_2");
		this.leaf_3 = this.leaves.getChild("leaf_3");
		this.leaf_mid_3 = this.leaf_3.getChild("leaf_mid_3");
		this.leaf_tip_3 = this.leaf_mid_3.getChild("leaf_tip_3");
		this.leaf_4 = this.leaves.getChild("leaf_4");
		this.leaf_mid_4 = this.leaf_4.getChild("leaf_mid_4");
		this.leaf_tip_4 = this.leaf_mid_4.getChild("leaf_tip_4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition stem = body.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(56, 21).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition stem_2 = stem.addOrReplaceChild("stem_2", CubeListBuilder.create().texOffs(56, 14).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition stem_3 = stem_2.addOrReplaceChild("stem_3", CubeListBuilder.create().texOffs(56, 7).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition stem_4 = stem_3.addOrReplaceChild("stem_4", CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition head = stem_4.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 36).addBox(-5.0F, -6.0071F, -3.9867F, 10.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(41, 37).addBox(0.0F, -10.0071F, -3.9867F, 0.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(54, 28).addBox(-2.0F, -2.0071F, -0.9867F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.9929F, -0.0133F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(52, 33).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0071F, -0.9867F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(52, 33).addBox(-3.0F, -6.0F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9929F, -0.9867F, -2.3562F, 0.0F, 0.0F));

		PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(52, 33).addBox(-3.0F, -6.4142F, -2.4142F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0071F, 0.4275F, -0.7854F, 0.0F, -1.5708F));

		PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(52, 33).addBox(-3.0F, -6.4142F, 2.4142F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0071F, 0.4275F, -2.3562F, 0.0F, -1.5708F));

		PartDefinition head_top = head.addOrReplaceChild("head_top", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.0071F, -9.9867F, 12.0F, 5.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(34, 28).addBox(0.0F, -10.0071F, -8.9867F, 0.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 51).addBox(-7.0F, -0.9071F, -10.4867F, 14.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, -4.0F));

		PartDefinition cube_r5 = head_top.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(2, 49).addBox(-4.0F, -1.0F, 0.0F, 9.5F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, 0.9929F, -3.9867F, 0.0F, 1.5708F, 3.1416F));

		PartDefinition cube_r6 = head_top.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(2, 49).addBox(-4.0F, -1.0F, 0.0F, 9.5F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, 0.9929F, -3.9867F, 0.0F, 1.5708F, 3.1416F));

		PartDefinition cube_r7 = head_top.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 49).addBox(-6.0F, -1.0F, 4.0F, 11.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.9929F, -5.4867F, 3.1416F, 0.0F, 0.0F));

		PartDefinition head_bottom = head.addOrReplaceChild("head_bottom", CubeListBuilder.create().texOffs(0, 16).addBox(-7.0F, 0.0F, -10.0F, 12.0F, 6.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(0, 51).addBox(-8.0F, -0.1F, -10.5F, 14.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -1.5071F, -3.9867F));

		PartDefinition cube_r8 = head_bottom.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(2, 49).addBox(-7.125F, -1.0F, 5.5F, 9.5F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(2, 49).addBox(-7.125F, -1.0F, -5.5F, 9.5F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, -7.125F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r9 = head_bottom.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 49).addBox(-5.5F, -1.0F, 2.375F, 11.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, -7.125F, 3.1416F, 0.0F, -3.1416F));

		PartDefinition toung = head_bottom.addOrReplaceChild("toung", CubeListBuilder.create().texOffs(35, 0).addBox(-1.5F, -1.0F, -4.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -1.0F, 0.0F));

		PartDefinition toung_2 = toung.addOrReplaceChild("toung_2", CubeListBuilder.create().texOffs(43, 6).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -4.0F));

		PartDefinition toung_3 = toung_2.addOrReplaceChild("toung_3", CubeListBuilder.create().texOffs(35, 7).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -3.0F));

		PartDefinition leaves = body.addOrReplaceChild("leaves", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leaf_1 = leaves.addOrReplaceChild("leaf_1", CubeListBuilder.create().texOffs(46, 39).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7071F, 0.0F, -0.6464F, 0.0F, -0.7854F, 0.0F));

		PartDefinition leaf_mid_1 = leaf_1.addOrReplaceChild("leaf_mid_1", CubeListBuilder.create().texOffs(44, 45).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_tip_1 = leaf_mid_1.addOrReplaceChild("leaf_tip_1", CubeListBuilder.create().texOffs(49, 53).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -8.0F));

		PartDefinition leaf_2 = leaves.addOrReplaceChild("leaf_2", CubeListBuilder.create().texOffs(46, 39).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7071F, 0.0F, 0.7678F, 0.0F, -2.3562F, 0.0F));

		PartDefinition leaf_mid_2 = leaf_2.addOrReplaceChild("leaf_mid_2", CubeListBuilder.create().texOffs(44, 45).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_tip_2 = leaf_mid_2.addOrReplaceChild("leaf_tip_2", CubeListBuilder.create().texOffs(49, 53).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -8.0F));

		PartDefinition leaf_3 = leaves.addOrReplaceChild("leaf_3", CubeListBuilder.create().texOffs(46, 39).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7071F, 0.0F, -0.6464F, 0.0F, 0.7854F, 0.0F));

		PartDefinition leaf_mid_3 = leaf_3.addOrReplaceChild("leaf_mid_3", CubeListBuilder.create().texOffs(44, 45).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_tip_3 = leaf_mid_3.addOrReplaceChild("leaf_tip_3", CubeListBuilder.create().texOffs(49, 53).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -8.0F));

		PartDefinition leaf_4 = leaves.addOrReplaceChild("leaf_4", CubeListBuilder.create().texOffs(46, 39).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7071F, 0.0F, 0.7678F, 0.0F, 2.3562F, 0.0F));

		PartDefinition leaf_mid_4 = leaf_4.addOrReplaceChild("leaf_mid_4", CubeListBuilder.create().texOffs(44, 45).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_tip_4 = leaf_mid_4.addOrReplaceChild("leaf_tip_4", CubeListBuilder.create().texOffs(49, 53).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -8.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Chomper entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}