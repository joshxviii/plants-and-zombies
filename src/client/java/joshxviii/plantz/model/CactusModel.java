// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class Cactus<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "cactus"), "main");
	private final ModelPart body;
	private final ModelPart trunk;
	private final ModelPart arms;
	private final ModelPart arm_R;
	private final ModelPart arm_L;
	private final ModelPart barrel;
	private final ModelPart eyebrows;
	private final ModelPart eyebrow_R;
	private final ModelPart eyebrow_L;

	public Cactus(ModelPart root) {
		this.body = root.getChild("body");
		this.trunk = this.body.getChild("trunk");
		this.arms = this.trunk.getChild("arms");
		this.arm_R = this.arms.getChild("arm_R");
		this.arm_L = this.arms.getChild("arm_L");
		this.barrel = this.trunk.getChild("barrel");
		this.eyebrows = this.trunk.getChild("eyebrows");
		this.eyebrow_R = this.eyebrows.getChild("eyebrow_R");
		this.eyebrow_L = this.eyebrows.getChild("eyebrow_L");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition trunk = body.addOrReplaceChild("trunk", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -20.0F, -6.0F, 12.0F, 20.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(16, 32).addBox(-2.0F, -22.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(48, 19).addBox(-3.0F, -28.0F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(48, 0).addBox(-2.0F, -15.0F, -10.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 30).addBox(6.0F, -20.0F, -7.0F, 0.0F, 20.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(0, 30).addBox(-6.0F, -20.0F, -7.0F, 0.0F, 20.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(0, 44).addBox(-7.0F, -20.0F, -6.0F, 14.0F, 20.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 44).addBox(-7.0F, -20.0F, 6.0F, 14.0F, 20.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = trunk.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -6.0F, 0.0F, 8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -20.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r2 = trunk.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 24).addBox(0.0F, -6.0F, -4.0F, 0.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -20.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r3 = trunk.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 24).addBox(0.0F, -6.0F, -4.0F, 0.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -20.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r4 = trunk.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, -6.0F, 0.0F, 8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -20.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition arms = trunk.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition arm_R = arms.addOrReplaceChild("arm_R", CubeListBuilder.create().texOffs(32, 32).addBox(-6.0F, -2.25F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(32, 40).addBox(-6.0F, -9.25F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -1.75F, 0.0F));

		PartDefinition arm_L = arms.addOrReplaceChild("arm_L", CubeListBuilder.create(), PartPose.offset(6.0F, -0.25F, 0.0F));

		PartDefinition cube_r5 = arm_L.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(32, 40).addBox(-2.0F, -2.5F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -4.25F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r6 = arm_L.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(32, 32).addBox(-3.0F, -2.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.25F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition barrel = trunk.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(48, 8).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, -10.0F));

		PartDefinition eyebrows = trunk.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -18.0F, -6.0F));

		PartDefinition eyebrow_R = eyebrows.addOrReplaceChild("eyebrow_R", CubeListBuilder.create().texOffs(48, 16).mirror().addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.5F, 0.0F, 0.0F));

		PartDefinition eyebrow_L = eyebrows.addOrReplaceChild("eyebrow_L", CubeListBuilder.create().texOffs(48, 16).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}