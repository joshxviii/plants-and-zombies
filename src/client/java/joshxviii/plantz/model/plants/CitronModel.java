package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.CitronAnimation;
import joshxviii.plantz.animation.plants.RoyalSunflowerAnimation;
import joshxviii.plantz.renderer.entity.plant.PlantRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class CitronModel extends PlantModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("citron"), "main");
	private final ModelPart body;
	private final ModelPart bot;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart glasses;
	private final ModelPart eyebrows;
	private final ModelPart left_eyebrow;
	private final ModelPart right_eyebrow;
	private final ModelPart gun;
	private final ModelPart barrel;
	private final ModelPart leg_1;
	private final ModelPart leg_sheild_1;
	private final ModelPart leg_2;
	private final ModelPart leg_sheild_2;
	private final ModelPart leg_3;
	private final ModelPart leg_sheild_3;
	private final ModelPart leg_4;
	private final ModelPart leg_sheild_4;
	private final ModelPart ball;

	private final KeyframeAnimation ballIdleAnimation;
	private final KeyframeAnimation transformBallAnimation;
	private final KeyframeAnimation transformIdleAnimation;

	public CitronModel(ModelPart root) {
		super(root);

		walkAnimation = CitronAnimation.walk.bake(root);
		idleAnimation = CitronAnimation.idle.bake(root);
		this.ballIdleAnimation = CitronAnimation.idle_ball.bake(root);
		this.transformBallAnimation = CitronAnimation.transform_ball.bake(root);
		this.transformIdleAnimation = CitronAnimation.transform_idle.bake(root);
		sleepAnimation = CitronAnimation.sleep.bake(root);

		this.body = root.getChild("body");
		this.bot = this.body.getChild("bot");
		this.neck = this.bot.getChild("neck");
		this.head = this.bot.getChild("head");
		this.glasses = this.head.getChild("glasses");
		this.eyebrows = this.head.getChild("eyebrows");
		this.left_eyebrow = this.eyebrows.getChild("left_eyebrow");
		this.right_eyebrow = this.eyebrows.getChild("right_eyebrow");
		this.gun = this.bot.getChild("gun");
		this.barrel = this.gun.getChild("barrel");
		this.leg_1 = this.bot.getChild("leg_1");
		this.leg_sheild_1 = this.leg_1.getChild("leg_sheild_1");
		this.leg_2 = this.bot.getChild("leg_2");
		this.leg_sheild_2 = this.leg_2.getChild("leg_sheild_2");
		this.leg_3 = this.bot.getChild("leg_3");
		this.leg_sheild_3 = this.leg_3.getChild("leg_sheild_3");
		this.leg_4 = this.bot.getChild("leg_4");
		this.leg_sheild_4 = this.leg_4.getChild("leg_sheild_4");
		this.ball = this.body.getChild("ball");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 18.0F, 0.0F));

		PartDefinition bot = body.addOrReplaceChild("bot", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition neck = bot.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(24, 62).addBox(-6.0F, -2.0F, 6.0F, 12.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 48).addBox(-6.0F, -2.0F, -6.0F, 0.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(24, 60).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 50).addBox(6.0F, -2.0F, -6.0F, 0.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 20).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = bot.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 34).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(44, -6).addBox(0.0F, -11.0F, -5.0F, 0.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(55, 3).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition glasses = head.addOrReplaceChild("glasses", CubeListBuilder.create().texOffs(50, 0).addBox(-3.5F, -1.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -5.25F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -3.5F, -5.0F));

		PartDefinition left_eyebrow = eyebrows.addOrReplaceChild("left_eyebrow", CubeListBuilder.create().texOffs(52, 2).addBox(-2.0F, -0.5F, 0.0F, 4.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, -0.025F));

		PartDefinition right_eyebrow = eyebrows.addOrReplaceChild("right_eyebrow", CubeListBuilder.create(), PartPose.offset(-3.0F, 0.0F, -0.025F));

		PartDefinition cube_r2 = right_eyebrow.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(52, 2).addBox(-2.0F, -0.5F, 0.0F, 4.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

		PartDefinition gun = bot.addOrReplaceChild("gun", CubeListBuilder.create().texOffs(3, 56).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -1.0F, 1.0F));

		PartDefinition barrel = gun.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(22, 51).addBox(-3.0F, -2.0F, -3.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(12, 51).addBox(-3.0F, -2.0F, -8.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 50).addBox(-2.5F, -1.5F, -7.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 0.0F));

		PartDefinition leg_1 = bot.addOrReplaceChild("leg_1", CubeListBuilder.create(), PartPose.offset(3.25F, 2.0F, -3.25F));

		PartDefinition cube_r3 = leg_1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 19).addBox(-2.0F, -3.5F, -1.5F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, -0.1465F, 0.0F, 3.1416F, 0.0F));

		PartDefinition leg_sheild_1 = leg_1.addOrReplaceChild("leg_sheild_1", CubeListBuilder.create().texOffs(50, 29).addBox(-2.0F, 0.0F, -0.3536F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.2929F));

		PartDefinition leg_2 = bot.addOrReplaceChild("leg_2", CubeListBuilder.create(), PartPose.offset(-3.25F, 2.0F, -3.25F));

		PartDefinition cube_r4 = leg_2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(48, 19).addBox(-2.0F, -3.5F, -1.5F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, -0.1465F, -3.1416F, 0.0F, 0.0F));

		PartDefinition leg_sheild_2 = leg_2.addOrReplaceChild("leg_sheild_2", CubeListBuilder.create().texOffs(50, 29).addBox(-2.0F, 0.0F, -0.3536F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.2929F));

		PartDefinition leg_3 = bot.addOrReplaceChild("leg_3", CubeListBuilder.create(), PartPose.offset(3.25F, 2.0F, 3.25F));

		PartDefinition cube_r5 = leg_3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(48, 19).addBox(-2.0F, -3.5F, -1.5F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, 0.1465F, 0.0F, 0.0F, -3.1416F));

		PartDefinition leg_sheild_3 = leg_3.addOrReplaceChild("leg_sheild_3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 2.2929F));

		PartDefinition cube_r6 = leg_sheild_3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(50, 29).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -0.1464F, 0.0F, 3.1416F, 0.0F));

		PartDefinition leg_4 = bot.addOrReplaceChild("leg_4", CubeListBuilder.create().texOffs(48, 19).addBox(-2.0F, -1.0F, -1.3536F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.25F, 2.0F, 3.25F));

		PartDefinition leg_sheild_4 = leg_4.addOrReplaceChild("leg_sheild_4", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 2.2929F));

		PartDefinition cube_r7 = leg_sheild_4.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(50, 29).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -0.1464F, 0.0F, 3.1416F, 0.0F));

		PartDefinition ball = body.addOrReplaceChild("ball", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.body.yRot = state.bodyRot * Mth.DEG_TO_RAD;
		this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
		this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
	}
}