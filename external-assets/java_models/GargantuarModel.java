// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class GargantuarModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "gargantuarmodel"), "main");
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart body;
	private final ModelPart basket;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart pole;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public GargantuarModel(ModelPart root) {
		this.root = root.getChild("root");
		this.head = this.root.getChild("head");
		this.hat = this.head.getChild("hat");
		this.body = this.root.getChild("body");
		this.basket = this.body.getChild("basket");
		this.right_arm = this.root.getChild("right_arm");
		this.left_arm = this.root.getChild("left_arm");
		this.pole = this.left_arm.getChild("pole");
		this.right_leg = this.root.getChild("right_leg");
		this.left_leg = this.root.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(46, 63).addBox(-5.0F, -9.2648F, -9.591F, 10.0F, 11.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.7352F, -4.409F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, -3.75F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -13.0593F, -5.3701F, 20.0F, 13.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-6.0F, -14.0593F, -6.3701F, 12.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 63).addBox(-7.0F, -0.0593F, -2.3701F, 14.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.9407F, -0.6299F));

		PartDefinition basket = body.addOrReplaceChild("basket", CubeListBuilder.create().texOffs(72, 15).addBox(-4.0F, -2.0F, 0.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0593F, 6.6299F));

		PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(36, 25).addBox(-8.0F, -3.0F, -5.0F, 8.0F, 28.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.0F, -18.0F, 0.0F));

		PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 25).addBox(0.0F, -3.0F, -5.0F, 8.0F, 28.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(10.0F, -18.0F, 0.0F));

		PartDefinition pole = left_arm.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(114, 102).addBox(-0.5F, -10.5F, -32.2857F, 2.0F, 21.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(114, 102).addBox(-0.5F, -10.5F, -21.2857F, 2.0F, 21.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(112, 94).addBox(-1.5F, -9.5F, -36.2857F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(112, 94).addBox(-1.5F, 5.5F, -36.2857F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(112, 94).addBox(-1.5F, -9.5F, -25.2857F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(112, 94).addBox(-1.5F, 5.5F, -25.2857F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.5F, 20.5F, -0.7143F));

		PartDefinition pole_r1 = pole.addOrReplaceChild("pole_r1", CubeListBuilder.create().texOffs(0, 118).addBox(-23.0F, -2.5F, -2.5F, 47.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, -10.7857F, 0.0F, 1.5708F, 0.0F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 80).addBox(-4.0F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 0.0F, 2.0F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(72, 34).addBox(-2.0F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 2.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}