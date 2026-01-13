// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class MelonPult<T extends MelonPult> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "melonpult"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart eyebrows;
	private final ModelPart eyebrow_L;
	private final ModelPart eyebrow_R;
	private final ModelPart coil;
	private final ModelPart coil_2;
	private final ModelPart basket;
	private final ModelPart projectile;
	private final ModelPart leaves;
	private final ModelPart leaf_1;
	private final ModelPart leaf_tip_1;
	private final ModelPart leaf_2;
	private final ModelPart leaf_tip_2;
	private final ModelPart leaf_3;
	private final ModelPart leaf_tip_3;
	private final ModelPart leaf_4;
	private final ModelPart leaf_tip_4;

	public MelonPult(ModelPart root) {
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.eyebrows = this.head.getChild("eyebrows");
		this.eyebrow_L = this.eyebrows.getChild("eyebrow_L");
		this.eyebrow_R = this.eyebrows.getChild("eyebrow_R");
		this.coil = this.head.getChild("coil");
		this.coil_2 = this.coil.getChild("coil_2");
		this.basket = this.coil_2.getChild("basket");
		this.projectile = this.basket.getChild("projectile");
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

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -10.0F, -6.0F, 14.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition eyebrow_L = eyebrows.addOrReplaceChild("eyebrow_L", CubeListBuilder.create().texOffs(68, 6).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -3.0F, -6.025F));

		PartDefinition eyebrow_R = eyebrows.addOrReplaceChild("eyebrow_R", CubeListBuilder.create().texOffs(68, 6).mirror().addBox(-2.0F, -1.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(3.0F, -3.0F, -6.025F));

		PartDefinition coil = head.addOrReplaceChild("coil", CubeListBuilder.create().texOffs(52, 13).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 6.0F));

		PartDefinition coil_2 = coil.addOrReplaceChild("coil_2", CubeListBuilder.create().texOffs(52, 3).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition basket = coil_2.addOrReplaceChild("basket", CubeListBuilder.create().texOffs(0, 45).addBox(-7.0F, -15.0F, 4.0F, 14.0F, 13.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 47).addBox(6.0F, -15.0F, -3.0F, 1.0F, 13.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 60).addBox(-6.0F, -15.0F, -3.0F, 12.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition cube_r1 = basket.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 60).addBox(-6.0F, -1.0F, -3.5F, 12.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 0.5F, 0.0F, 0.0F, -3.1416F));

		PartDefinition cube_r2 = basket.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 47).addBox(0.0F, -6.5F, -3.5F, 1.0F, 13.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -8.5F, 0.5F, 0.0F, 0.0F, 3.1416F));

		PartDefinition projectile = basket.addOrReplaceChild("projectile", CubeListBuilder.create().texOffs(0, 22).addBox(-7.0F, -5.5F, -6.0F, 14.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.5F, -3.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition leaves = body.addOrReplaceChild("leaves", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leaf_1 = leaves.addOrReplaceChild("leaf_1", CubeListBuilder.create().texOffs(62, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7071F, 0.0F, -0.6464F, 0.0F, -1.1781F, 0.0F));

		PartDefinition leaf_tip_1 = leaf_1.addOrReplaceChild("leaf_tip_1", CubeListBuilder.create().texOffs(62, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_2 = leaves.addOrReplaceChild("leaf_2", CubeListBuilder.create().texOffs(62, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7071F, 0.0F, 0.7678F, 0.0F, -1.9635F, 0.0F));

		PartDefinition leaf_tip_2 = leaf_2.addOrReplaceChild("leaf_tip_2", CubeListBuilder.create().texOffs(62, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_3 = leaves.addOrReplaceChild("leaf_3", CubeListBuilder.create().texOffs(62, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.7071F, 0.0F, -0.6464F, 0.0F, 1.1781F, 0.0F));

		PartDefinition leaf_tip_3 = leaf_3.addOrReplaceChild("leaf_tip_3", CubeListBuilder.create().texOffs(62, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_4 = leaves.addOrReplaceChild("leaf_4", CubeListBuilder.create().texOffs(62, 10).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.7071F, 0.0F, 0.7678F, 0.0F, 1.9635F, 0.0F));

		PartDefinition leaf_tip_4 = leaf_4.addOrReplaceChild("leaf_tip_4", CubeListBuilder.create().texOffs(62, 16).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(MelonPult entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}