// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class GnomeArmor<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "gnomearmor"), "main");
	private final ModelPart helmet;
	private final ModelPart chest;
	private final ModelPart chest_torso;
	private final ModelPart chest_arms;
	private final ModelPart chest_arm_L;
	private final ModelPart chest_arm_R;
	private final ModelPart leggings;
	private final ModelPart leggings_legs;
	private final ModelPart leggings_leg_L2;
	private final ModelPart leggings_leg_R2;
	private final ModelPart boots;
	private final ModelPart boot_R;
	private final ModelPart boot_L;

	public GnomeArmor(ModelPart root) {
		this.helmet = root.getChild("helmet");
		this.chest = root.getChild("chest");
		this.chest_torso = this.chest.getChild("chest_torso");
		this.chest_arms = this.chest_torso.getChild("chest_arms");
		this.chest_arm_L = this.chest_arms.getChild("chest_arm_L");
		this.chest_arm_R = this.chest_arms.getChild("chest_arm_R");
		this.leggings = root.getChild("leggings");
		this.leggings_legs = this.leggings.getChild("leggings_legs");
		this.leggings_leg_L2 = this.leggings_legs.getChild("leggings_leg_L2");
		this.leggings_leg_R2 = this.leggings_legs.getChild("leggings_leg_R2");
		this.boots = root.getChild("boots");
		this.boot_R = this.boots.getChild("boot_R");
		this.boot_L = this.boots.getChild("boot_L");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition helmet = partdefinition.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(7, 3).addBox(-2.5F, -4.75F, -2.5F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(3, 3).addBox(-0.5F, -4.75F, -2.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(20, 8).addBox(-2.5F, -3.75F, 1.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 6).addBox(-2.5F, -3.75F, -1.5F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(11, 6).addBox(1.5F, -3.75F, -1.5F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, 0.0F));

		PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition chest_torso = chest.addOrReplaceChild("chest_torso", CubeListBuilder.create().texOffs(22, 22).addBox(-2.5F, -4.0F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition chest_arms = chest_torso.addOrReplaceChild("chest_arms", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition chest_arm_L = chest_arms.addOrReplaceChild("chest_arm_L", CubeListBuilder.create().texOffs(2, 26).mirror().addBox(-0.5F, -1.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, -1.0F, 0.0F));

		PartDefinition chest_arm_R = chest_arms.addOrReplaceChild("chest_arm_R", CubeListBuilder.create().texOffs(2, 26).addBox(-2.5F, -1.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -1.0F, 0.5F));

		PartDefinition leggings = partdefinition.addOrReplaceChild("leggings", CubeListBuilder.create().texOffs(19, 27).addBox(-2.5F, -4.0F, -1.5F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition leggings_legs = leggings.addOrReplaceChild("leggings_legs", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition leggings_leg_L2 = leggings_legs.addOrReplaceChild("leggings_leg_L2", CubeListBuilder.create().texOffs(2, 22).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, 0.0F));

		PartDefinition leggings_leg_R2 = leggings_legs.addOrReplaceChild("leggings_leg_R2", CubeListBuilder.create().texOffs(2, 22).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-1.0F, 0.0F, 0.0F));

		PartDefinition boots = partdefinition.addOrReplaceChild("boots", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition boot_R = boots.addOrReplaceChild("boot_R", CubeListBuilder.create().texOffs(2, 26).addBox(-2.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition boot_L = boots.addOrReplaceChild("boot_L", CubeListBuilder.create().texOffs(2, 26).mirror().addBox(-2.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		helmet.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		chest.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leggings.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		boots.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}