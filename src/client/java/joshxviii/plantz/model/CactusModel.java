package joshxviii.plantz.model;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.CactusAnimation;
import joshxviii.plantz.animation.CherryBombAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;
import static joshxviii.plantz.UtilsKt.pazResource;

public class CactusModel extends EntityModel<@NotNull PlantRenderState>{
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("cactus"), "main");
	private final ModelPart body;
	private final ModelPart trunk;
	private final ModelPart head;
	private final ModelPart barrel;
	private final ModelPart eyebrows;
	private final ModelPart eyebrow_R;
	private final ModelPart eyebrow_L;
	private final ModelPart arms;
	private final ModelPart arm_R;
	private final ModelPart arm_L;
	private final KeyframeAnimation idleAnimation;

	public CactusModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.trunk = this.body.getChild("trunk");
		this.head = this.trunk.getChild("head");
		this.barrel = this.head.getChild("barrel");
		this.eyebrows = this.head.getChild("eyebrows");
		this.eyebrow_R = this.eyebrows.getChild("eyebrow_R");
		this.eyebrow_L = this.eyebrows.getChild("eyebrow_L");
		this.arms = this.trunk.getChild("arms");
		this.arm_R = this.arms.getChild("arm_R");
		this.arm_L = this.arms.getChild("arm_L");
		this.idleAnimation = CactusAnimation.idle.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition trunk = body.addOrReplaceChild("trunk", CubeListBuilder.create().texOffs(0, 22).addBox(-6.0F, -10.0F, -6.0F, 12.0F, 10.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 40).addBox(6.0F, -10.0F, -7.0F, 0.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(0, 40).addBox(-6.0F, -10.0F, -7.0F, 0.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(0, 54).addBox(-7.0F, -10.0F, -6.0F, 14.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 54).addBox(-7.0F, -10.0F, 6.0F, 14.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = trunk.addOrReplaceChild("head", CubeListBuilder.create().texOffs(48, 39).addBox(-2.0F, -11.8939F, -2.2857F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(48, 19).addBox(-3.0F, -17.8939F, -0.2857F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 44).addBox(-7.0F, -9.8939F, -6.2857F, 14.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 44).addBox(-7.0F, -9.8939F, 5.7143F, 14.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 30).addBox(6.0F, -9.8939F, -7.2857F, 0.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(0, 30).addBox(-6.0F, -9.8939F, -7.2857F, 0.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-6.0F, -9.8939F, -6.2857F, 12.0F, 10.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(48, 45).addBox(-2.0F, -4.8939F, -10.2857F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.1061F, 0.2857F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 33).addBox(-4.0F, -6.0F, 0.0F, 8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.8939F, 1.7143F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(48, 25).addBox(0.0F, -6.0F, -4.0F, 0.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -9.8939F, -0.2857F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 25).addBox(0.0F, -6.0F, -4.0F, 0.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -9.8939F, -0.2857F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(48, 33).addBox(-4.0F, -6.0F, 0.0F, 8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.8939F, -2.2857F, 0.7854F, 0.0F, 0.0F));

		PartDefinition barrel = head.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(48, 53).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.8939F, -10.2857F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -7.8939F, -6.2857F));

		PartDefinition eyebrow_R = eyebrows.addOrReplaceChild("eyebrow_R", CubeListBuilder.create().texOffs(48, 61).mirror().addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.5F, 0.0F, 0.0F));

		PartDefinition eyebrow_L = eyebrows.addOrReplaceChild("eyebrow_L", CubeListBuilder.create().texOffs(48, 61).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 0.0F, 0.0F));

		PartDefinition arms = trunk.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition arm_R = arms.addOrReplaceChild("arm_R", CubeListBuilder.create().texOffs(28, 45).addBox(-6.0F, -2.25F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(28, 53).addBox(-6.0F, -9.25F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -1.75F, 0.0F));

		PartDefinition arm_L = arms.addOrReplaceChild("arm_L", CubeListBuilder.create(), PartPose.offset(6.0F, -0.25F, 0.0F));

		PartDefinition cube_r5 = arm_L.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(28, 53).addBox(-2.0F, -2.5F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -4.25F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r6 = arm_L.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(28, 45).addBox(-3.0F, -2.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.25F, 0.0F, 0.0F, 3.1416F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.body.yRot = state.yRot * (float) (Math.PI / 180.0);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
	}
}