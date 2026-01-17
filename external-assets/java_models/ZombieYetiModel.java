// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class ZombieYeti<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "zombieyeti"), "main");
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public ZombieYeti(ModelPart root) {
		this.root = root.getChild("root");
		this.body = this.root.getChild("body");
		this.head = this.root.getChild("head");
		this.hat = this.head.getChild("hat");
		this.left_arm = this.root.getChild("left_arm");
		this.right_arm = this.root.getChild("right_arm");
		this.right_leg = this.root.getChild("right_leg");
		this.left_leg = this.root.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(42, 56).addBox(-6.5F, -9.5F, -5.5F, 13.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(38, 41).addBox(-6.5F, -16.5F, -3.5F, 13.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 51).addBox(-6.0F, -9.0F, -5.0F, 12.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 37).addBox(-6.0F, -16.0F, -3.0F, 12.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -6.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(35, 0).addBox(-4.5F, -7.5F, -6.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -7.5F, -2.0F));

		PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(64, 18).addBox(-0.5F, -2.5F, -3.0F, 6.0F, 17.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(20, 16).addBox(0.0F, -2.0F, -2.5F, 5.0F, 16.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -14.25F, 0.5F));

		PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 18).addBox(-5.5F, -2.5F, -3.0F, 6.0F, 17.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-5.0F, -2.0F, -2.5F, 5.0F, 16.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -14.25F, 0.5F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(64, 76).addBox(-3.0F, -0.5F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 69).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 0.0F, -0.5F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(40, 76).addBox(-3.0F, -0.5F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(20, 69).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 0.0F, -0.5F));

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