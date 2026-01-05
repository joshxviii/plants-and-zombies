// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class WallNut<T extends WallNut> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "wallnut"), "main");
	private final ModelPart body;
	private final ModelPart inner;

	public WallNut(ModelPart root) {
		this.body = root.getChild("body");
		this.inner = this.body.getChild("inner");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -21.0F, -8.0F, 16.0F, 19.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(56, 35).addBox(-7.0F, -24.0F, -7.0F, 14.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(56, 52).addBox(-7.0F, -2.0F, -7.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition inner = body.addOrReplaceChild("inner", CubeListBuilder.create().texOffs(0, 35).addBox(-7.0F, -21.0F, -7.0F, 14.0F, 18.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-6.0F, -23.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(64, 14).addBox(-6.0F, -3.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(WallNut entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}