// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class PotatoMine<T extends PotatoMine> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "potatomine"), "main");
	private final ModelPart dirt;
	private final ModelPart body;

	public PotatoMine(ModelPart root) {
		this.dirt = root.getChild("dirt");
		this.body = this.dirt.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition dirt = partdefinition.addOrReplaceChild("dirt", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 9.5F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition body = dirt.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 18).addBox(-6.0F, -7.0F, -6.0F, 12.0F, 7.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(16, 37).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 37).addBox(-2.0F, -15.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(PotatoMine entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		dirt.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}