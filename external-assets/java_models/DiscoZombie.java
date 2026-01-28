// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class DiscoZombie<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "discozombie"), "main");
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart left_leg;
	private final ModelPart right_leg;

	public DiscoZombie(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.head = this.root.getChild("head");
		this.hat = this.head.getChild("hat");
		this.left_arm = this.root.getChild("left_arm");
		this.right_arm = this.root.getChild("right_arm");
		this.left_leg = this.root.getChild("left_leg");
		this.right_leg = this.root.getChild("right_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(48, 22).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 42).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 22).addBox(-7.0F, -12.0F, -2.5F, 14.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(34, 50).addBox(-6.0F, -8.0F, -4.25F, 12.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-7.5F, -12.5F, -3.0F, 15.0F, 11.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 58).addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(52, 9).addBox(0.0F, 5.0F, -2.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -10.0F, 0.0F));

		PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(14, 58).addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(70, 9).addBox(-4.0F, 5.0F, -2.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -10.0F, 0.0F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(44, 58).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(72, 1).addBox(-2.0F, 7.0F, -2.5F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(28, 58).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(52, 1).addBox(-3.0F, 7.0F, -2.5F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

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