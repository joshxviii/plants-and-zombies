// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class Sunflower<T extends Sunflower> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "sunflower"), "main");
	private final ModelPart stem;
	private final ModelPart stem_2;
	private final ModelPart stem_3;
	private final ModelPart head;
	private final ModelPart leaves;
	private final ModelPart leaf_1;
	private final ModelPart leaf_seg_1;
	private final ModelPart leaf_2;
	private final ModelPart leaf_seg_2;
	private final ModelPart leaf_3;
	private final ModelPart leaf_seg_3;
	private final ModelPart leaf_4;
	private final ModelPart leaf_seg_4;

	public Sunflower(ModelPart root) {
		this.stem = root.getChild("stem");
		this.stem_2 = this.stem.getChild("stem_2");
		this.stem_3 = this.stem_2.getChild("stem_3");
		this.head = this.stem_3.getChild("head");
		this.leaves = root.getChild("leaves");
		this.leaf_1 = this.leaves.getChild("leaf_1");
		this.leaf_seg_1 = this.leaf_1.getChild("leaf_seg_1");
		this.leaf_2 = root.getChild("leaf_2");
		this.leaf_seg_2 = this.leaf_2.getChild("leaf_seg_2");
		this.leaf_3 = root.getChild("leaf_3");
		this.leaf_seg_3 = this.leaf_3.getChild("leaf_seg_3");
		this.leaf_4 = root.getChild("leaf_4");
		this.leaf_seg_4 = this.leaf_4.getChild("leaf_seg_4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition stem = partdefinition.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(4, 25).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition stem_2 = stem.addOrReplaceChild("stem_2", CubeListBuilder.create().texOffs(4, 18).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition cube_r1 = stem_2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(25, 4).mirror().addBox(-5.0F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 1.0457F, -0.3766F, 0.2495F));

		PartDefinition cube_r2 = stem_2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(25, 4).addBox(0.0F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.7648F, 0.3376F, -0.2222F));

		PartDefinition stem_3 = stem_2.addOrReplaceChild("stem_3", CubeListBuilder.create().texOffs(12, 18).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition head = stem_3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.0071F, -1.9867F, 12.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(3, 13).addBox(-2.0F, -2.0071F, 0.0133F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.9929F, -1.0133F));

		PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(29, 0).addBox(-3.0F, -4.0F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0071F, 0.0133F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(29, 0).addBox(-3.0F, -4.0F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9929F, 0.0133F, -2.3562F, 0.0F, 0.0F));

		PartDefinition cube_r5 = head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(29, 0).addBox(-3.0F, -4.4142F, -2.4142F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0071F, 1.4275F, -0.7854F, 0.0F, -1.5708F));

		PartDefinition cube_r6 = head.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(29, 0).addBox(-3.0F, -4.4142F, 2.4142F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0071F, 1.4275F, -2.3562F, 0.0F, -1.5708F));

		PartDefinition cube_r7 = head.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(38, 32).addBox(-7.0F, -9.0F, 0.0F, 7.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -1.0071F, 0.0133F, 0.0F, 0.3927F, 0.0F));

		PartDefinition cube_r8 = head.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(38, 32).mirror().addBox(0.0F, -9.0F, 0.0F, 7.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.0F, -1.0071F, 0.0133F, 0.0F, -0.3927F, 0.0F));

		PartDefinition cube_r9 = head.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 43).addBox(-9.0F, 0.0F, -1.0F, 18.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.9929F, 1.0133F, 0.3927F, 0.0F, 0.0F));

		PartDefinition cube_r10 = head.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 32).addBox(-9.0F, -7.0F, 0.0F, 18.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0071F, 0.0133F, -0.3927F, 0.0F, 0.0F));

		PartDefinition leaves = partdefinition.addOrReplaceChild("leaves", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition leaf_1 = leaves.addOrReplaceChild("leaf_1", CubeListBuilder.create().texOffs(31, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7071F, 0.0F, -0.6464F, 0.0F, -0.7854F, 0.0F));

		PartDefinition leaf_seg_1 = leaf_1.addOrReplaceChild("leaf_seg_1", CubeListBuilder.create().texOffs(31, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_2 = partdefinition.addOrReplaceChild("leaf_2", CubeListBuilder.create().texOffs(31, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7071F, 24.0F, 0.7678F, 0.0F, -2.3562F, 0.0F));

		PartDefinition leaf_seg_2 = leaf_2.addOrReplaceChild("leaf_seg_2", CubeListBuilder.create().texOffs(31, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_3 = partdefinition.addOrReplaceChild("leaf_3", CubeListBuilder.create().texOffs(31, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7071F, 24.0F, -0.6464F, 0.0F, 0.7854F, 0.0F));

		PartDefinition leaf_seg_3 = leaf_3.addOrReplaceChild("leaf_seg_3", CubeListBuilder.create().texOffs(31, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_4 = partdefinition.addOrReplaceChild("leaf_4", CubeListBuilder.create().texOffs(31, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7071F, 24.0F, 0.7678F, 0.0F, 2.3562F, 0.0F));

		PartDefinition leaf_seg_4 = leaf_4.addOrReplaceChild("leaf_seg_4", CubeListBuilder.create().texOffs(31, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Sunflower entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		stem.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leaves.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leaf_2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leaf_3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leaf_4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}