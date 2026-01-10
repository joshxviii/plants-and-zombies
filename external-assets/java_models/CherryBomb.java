// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class CherryBomb<T extends CherryBomb> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "cherrybomb"), "main");
	private final ModelPart body;
	private final ModelPart stem;
	private final ModelPart leaves;
	private final ModelPart leaf1;
	private final ModelPart leaf1_tip_2;
	private final ModelPart leaf2;
	private final ModelPart leaf2_tip_2;
	private final ModelPart head_1;
	private final ModelPart head_2;

	public CherryBomb(ModelPart root) {
		this.body = root.getChild("body");
		this.stem = this.body.getChild("stem");
		this.leaves = this.stem.getChild("leaves");
		this.leaf1 = this.leaves.getChild("leaf1");
		this.leaf1_tip_2 = this.leaf1.getChild("leaf1_tip_2");
		this.leaf2 = this.leaves.getChild("leaf2");
		this.leaf2_tip_2 = this.leaf2.getChild("leaf2_tip_2");
		this.head_1 = this.stem.getChild("head_1");
		this.head_2 = this.stem.getChild("head_2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, -1.0F));

		PartDefinition stem = body.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(24, 14).addBox(-6.0F, -0.125F, -0.25F, 12.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.875F, 1.25F));

		PartDefinition leaves = stem.addOrReplaceChild("leaves", CubeListBuilder.create(), PartPose.offset(1.0F, -0.125F, -0.25F));

		PartDefinition leaf1 = leaves.addOrReplaceChild("leaf1", CubeListBuilder.create().texOffs(24, 25).addBox(0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leaf1_tip_2 = leaf1.addOrReplaceChild("leaf1_tip_2", CubeListBuilder.create().texOffs(26, 31).addBox(0.0F, 0.0F, -3.0F, 3.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition leaf2 = leaves.addOrReplaceChild("leaf2", CubeListBuilder.create().texOffs(24, 25).addBox(0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition leaf2_tip_2 = leaf2.addOrReplaceChild("leaf2_tip_2", CubeListBuilder.create().texOffs(26, 31).addBox(0.0F, 0.0F, -3.0F, 3.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition head_1 = stem.addOrReplaceChild("head_1", CubeListBuilder.create().texOffs(0, 14).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, 10.875F, -0.25F));

		PartDefinition head_2 = stem.addOrReplaceChild("head_2", CubeListBuilder.create(), PartPose.offset(3.5F, 9.875F, -0.25F));

		PartDefinition cube_r1 = head_2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.5F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(CherryBomb entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}