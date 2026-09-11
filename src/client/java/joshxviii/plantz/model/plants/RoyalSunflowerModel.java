package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.RoyalSunflowerAnimation;
import joshxviii.plantz.animation.plants.SunflowerAnimation;
import joshxviii.plantz.renderer.entity.plant.PlantRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class RoyalSunflowerModel extends PlantModel {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("royal_sunflower"), "main");
	private final ModelPart body;
	private final ModelPart stem;
	private final ModelPart stem_2;
	private final ModelPart right_hand;
	private final ModelPart left_hand;
	private final ModelPart stem_3;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart petals;
	private final ModelPart back_petals;
	private final ModelPart back_petal_1;
	private final ModelPart back_petal_2;
	private final ModelPart back_petal_3;
	private final ModelPart back_petal_4;
	private final ModelPart back_petal_5;
	private final ModelPart back_petal_6;
	private final ModelPart front_petals;
	private final ModelPart front_petal_1;
	private final ModelPart front_petal_2;
	private final ModelPart front_petal_3;
	private final ModelPart front_petal_4;
	private final ModelPart crown_petals;
	private final ModelPart crown_petal_1;
	private final ModelPart crown_petal_2;
	private final ModelPart roots;
	private final ModelPart root_1;
	private final ModelPart root_tip_1;
	private final ModelPart leaf_1;
	private final ModelPart leaf_mid_1;
	private final ModelPart leaf_tip_1;
	private final ModelPart root_2;
	private final ModelPart root_tip_2;
	private final ModelPart leaf_2;
	private final ModelPart leaf_mid_2;
	private final ModelPart leaf_tip_2;
	private final ModelPart root_3;
	private final ModelPart root_tip_3;
	private final ModelPart leaf_3;
	private final ModelPart leaf_mid_3;
	private final ModelPart leaf_tip_3;
	private final ModelPart root_4;
	private final ModelPart root_tip_4;
	private final ModelPart leaf_4;
	private final ModelPart leaf_mid_4;
	private final ModelPart leaf_tip_4;

	final KeyframeAnimation walkAnimation;
	final KeyframeAnimation chargeAnimation;

	public RoyalSunflowerModel(ModelPart root) {
		super(root);

		idleAnimation = RoyalSunflowerAnimation.idle.bake(root);
		sleepAnimation = RoyalSunflowerAnimation.sleep.bake(root);
		actionAnimation = RoyalSunflowerAnimation.action.bake(root);

		this.walkAnimation = RoyalSunflowerAnimation.walk.bake(root);
		this.chargeAnimation = RoyalSunflowerAnimation.charge.bake(root);

		this.body = root.getChild("body");
		this.stem = this.body.getChild("stem");
		this.stem_2 = this.stem.getChild("stem_2");
		this.right_hand = this.stem_2.getChild("right_hand");
		this.left_hand = this.stem_2.getChild("left_hand");
		this.stem_3 = this.stem_2.getChild("stem_3");
		this.neck = this.stem_3.getChild("neck");
		this.head = this.neck.getChild("head");
		this.petals = this.head.getChild("petals");
		this.back_petals = this.petals.getChild("back_petals");
		this.back_petal_1 = this.back_petals.getChild("back_petal_1");
		this.back_petal_2 = this.back_petals.getChild("back_petal_2");
		this.back_petal_3 = this.back_petals.getChild("back_petal_3");
		this.back_petal_4 = this.back_petals.getChild("back_petal_4");
		this.back_petal_5 = this.back_petals.getChild("back_petal_5");
		this.back_petal_6 = this.back_petals.getChild("back_petal_6");
		this.front_petals = this.petals.getChild("front_petals");
		this.front_petal_1 = this.front_petals.getChild("front_petal_1");
		this.front_petal_2 = this.front_petals.getChild("front_petal_2");
		this.front_petal_3 = this.front_petals.getChild("front_petal_3");
		this.front_petal_4 = this.front_petals.getChild("front_petal_4");
		this.crown_petals = this.petals.getChild("crown_petals");
		this.crown_petal_1 = this.crown_petals.getChild("crown_petal_1");
		this.crown_petal_2 = this.crown_petals.getChild("crown_petal_2");
		this.roots = this.body.getChild("roots");
		this.root_1 = this.roots.getChild("root_1");
		this.root_tip_1 = this.root_1.getChild("root_tip_1");
		this.leaf_1 = this.root_1.getChild("leaf_1");
		this.leaf_mid_1 = this.leaf_1.getChild("leaf_mid_1");
		this.leaf_tip_1 = this.leaf_mid_1.getChild("leaf_tip_1");
		this.root_2 = this.roots.getChild("root_2");
		this.root_tip_2 = this.root_2.getChild("root_tip_2");
		this.leaf_2 = this.root_2.getChild("leaf_2");
		this.leaf_mid_2 = this.leaf_2.getChild("leaf_mid_2");
		this.leaf_tip_2 = this.leaf_mid_2.getChild("leaf_tip_2");
		this.root_3 = this.roots.getChild("root_3");
		this.root_tip_3 = this.root_3.getChild("root_tip_3");
		this.leaf_3 = this.root_3.getChild("leaf_3");
		this.leaf_mid_3 = this.leaf_3.getChild("leaf_mid_3");
		this.leaf_tip_3 = this.leaf_mid_3.getChild("leaf_tip_3");
		this.root_4 = this.roots.getChild("root_4");
		this.root_tip_4 = this.root_4.getChild("root_tip_4");
		this.leaf_4 = this.root_4.getChild("leaf_4");
		this.leaf_mid_4 = this.leaf_4.getChild("leaf_mid_4");
		this.leaf_tip_4 = this.leaf_mid_4.getChild("leaf_tip_4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 27.0F, 0.0F));

		PartDefinition stem = body.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(64, 38).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition stem_2 = stem.addOrReplaceChild("stem_2", CubeListBuilder.create().texOffs(60, 20).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition right_hand = stem_2.addOrReplaceChild("right_hand", CubeListBuilder.create(), PartPose.offset(-4.0F, -8.0F, 0.0F));

		PartDefinition cube_r1 = right_hand.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(40, 22).addBox(0.0F, 0.0F, -3.0F, 4.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, 0.0F, 0.0F, 3.1416F, 0.0F, -2.3562F));

		PartDefinition cube_r2 = right_hand.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 28).addBox(-5.0F, 0.0F, -4.0F, 10.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition left_hand = stem_2.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(32, 28).addBox(0.0F, 0.0F, -4.0F, 10.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -8.0F, 0.0F));

		PartDefinition cube_r3 = left_hand.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 22).addBox(0.0F, 0.0F, -3.0F, 4.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition stem_3 = stem_2.addOrReplaceChild("stem_3", CubeListBuilder.create().texOffs(64, 0).addBox(-3.0F, -14.0F, -3.0F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition neck = stem_3.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(24, 27).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -7.0071F, -6.9867F, 18.0F, 6.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(20, 20).addBox(-3.0F, -1.0071F, -2.9867F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.9929F, -0.0133F));

		PartDefinition cube_r4 = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(36, 74).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.8213F, 5.8417F, -1.5708F, -0.7854F, 1.5708F));

		PartDefinition cube_r5 = head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(36, 74).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.8284F, 1.8213F, 0.0133F, -3.1416F, 0.0F, 2.3562F));

		PartDefinition cube_r6 = head.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(36, 74).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.3126F, -6.9259F, 1.5708F, 1.3963F, 1.5708F));

		PartDefinition cube_r7 = head.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(36, 74).addBox(0.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -1.0071F, 0.0133F, 0.0F, 0.0F, 0.7854F));

		PartDefinition petals = head.addOrReplaceChild("petals", CubeListBuilder.create().texOffs(-22, 52).addBox(-15.0F, -6.5F, -25.0F, 30.0F, 0.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 14.0F));

		PartDefinition back_petals = petals.addOrReplaceChild("back_petals", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0071F, -13.6534F));

		PartDefinition back_petal_1 = back_petals.addOrReplaceChild("back_petal_1", CubeListBuilder.create(), PartPose.offset(-7.0F, 0.0F, -5.3333F));

		PartDefinition cube_r8 = back_petal_1.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(28, 75).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.4142F, -8.4142F, 2.3543F, 0.0617F, 3.0798F));

		PartDefinition cube_r9 = back_petal_1.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(20, 79).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition back_petal_2 = back_petals.addOrReplaceChild("back_petal_2", CubeListBuilder.create(), PartPose.offsetAndRotation(-9.0F, 0.0F, 0.6667F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r10 = back_petal_2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(28, 75).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.4142F, -8.4142F, 2.3562F, 0.0F, -3.1416F));

		PartDefinition cube_r11 = back_petal_2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(20, 79).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, -3.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition back_petal_3 = back_petals.addOrReplaceChild("back_petal_3", CubeListBuilder.create(), PartPose.offset(7.0F, 0.0F, -5.3333F));

		PartDefinition cube_r12 = back_petal_3.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(28, 75).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.4142F, -8.4142F, 2.3562F, 0.0F, -3.1416F));

		PartDefinition cube_r13 = back_petal_3.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(20, 79).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition back_petal_4 = back_petals.addOrReplaceChild("back_petal_4", CubeListBuilder.create(), PartPose.offsetAndRotation(9.0F, 0.0F, 0.6667F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r14 = back_petal_4.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(28, 75).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.4142F, -8.4142F, 2.3562F, 0.0F, -3.1416F));

		PartDefinition cube_r15 = back_petal_4.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(20, 79).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, -3.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition back_petal_5 = back_petals.addOrReplaceChild("back_petal_5", CubeListBuilder.create().texOffs(20, 79).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 0.0F, 4.6667F));

		PartDefinition cube_r16 = back_petal_5.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(28, 75).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition back_petal_6 = back_petals.addOrReplaceChild("back_petal_6", CubeListBuilder.create().texOffs(20, 79).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 0.0F, 4.6667F));

		PartDefinition cube_r17 = back_petal_6.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(28, 75).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 7.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition front_petals = petals.addOrReplaceChild("front_petals", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition front_petal_1 = front_petals.addOrReplaceChild("front_petal_1", CubeListBuilder.create().texOffs(1, 79).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, -5.0F, -11.0F));

		PartDefinition cube_r18 = front_petal_1.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(11, 74).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition front_petal_2 = front_petals.addOrReplaceChild("front_petal_2", CubeListBuilder.create(), PartPose.offset(9.0F, -5.0F, -16.9734F));

		PartDefinition cube_r19 = front_petal_2.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(11, 74).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.7678F, -9.7678F, 2.3562F, 0.0F, -3.1416F));

		PartDefinition cube_r20 = front_petal_2.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(1, 79).addBox(-4.0F, 0.0F, -5.5F, 8.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition front_petal_3 = front_petals.addOrReplaceChild("front_petal_3", CubeListBuilder.create().texOffs(1, 79).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, -5.0F, -11.0F));

		PartDefinition cube_r21 = front_petal_3.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(11, 74).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition front_petal_4 = front_petals.addOrReplaceChild("front_petal_4", CubeListBuilder.create(), PartPose.offset(-9.0F, -5.0F, -16.9734F));

		PartDefinition cube_r22 = front_petal_4.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(11, 74).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.7678F, -9.7678F, 2.3562F, 0.0F, -3.1416F));

		PartDefinition cube_r23 = front_petal_4.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(1, 79).addBox(-4.0F, 0.0F, -5.5F, 8.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition crown_petals = petals.addOrReplaceChild("crown_petals", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition crown_petal_1 = crown_petals.addOrReplaceChild("crown_petal_1", CubeListBuilder.create().texOffs(-12, 81).mirror().addBox(-3.0F, 0.0F, -2.0F, 6.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, -6.0F, -7.0F));

		PartDefinition cube_r24 = crown_petal_1.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(-5, 74).mirror().addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.0F, 0.0F, 10.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition crown_petal_2 = crown_petals.addOrReplaceChild("crown_petal_2", CubeListBuilder.create().texOffs(-12, 81).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 0.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -6.0F, -7.0F));

		PartDefinition cube_r25 = crown_petal_2.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(-5, 74).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 10.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition roots = body.addOrReplaceChild("roots", CubeListBuilder.create().texOffs(2, 46).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition root_1 = roots.addOrReplaceChild("root_1", CubeListBuilder.create().texOffs(2, 30).addBox(-1.0F, -1.0F, -7.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition root_tip_1 = root_1.addOrReplaceChild("root_tip_1", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -6.0F));

		PartDefinition cube_r26 = root_tip_1.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -1.0F, -9.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition leaf_1 = root_1.addOrReplaceChild("leaf_1", CubeListBuilder.create().texOffs(66, 92).addBox(-5.0F, 0.0F, -6.0F, 10.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -2.0F));

		PartDefinition leaf_mid_1 = leaf_1.addOrReplaceChild("leaf_mid_1", CubeListBuilder.create().texOffs(63, 82).addBox(-5.0F, 0.0F, -9.0F, 10.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -6.0F));

		PartDefinition leaf_tip_1 = leaf_mid_1.addOrReplaceChild("leaf_tip_1", CubeListBuilder.create().texOffs(72, 78).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -9.0F));

		PartDefinition root_2 = roots.addOrReplaceChild("root_2", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition cube_r27 = root_2.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(2, 42).addBox(-3.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition root_tip_2 = root_2.addOrReplaceChild("root_tip_2", CubeListBuilder.create(), PartPose.offset(-6.0F, 1.0F, 0.0F));

		PartDefinition cube_r28 = root_tip_2.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition leaf_2 = root_2.addOrReplaceChild("leaf_2", CubeListBuilder.create().texOffs(66, 67).mirror().addBox(-6.0F, 0.0F, -5.0F, 6.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, -1.0F, 0.0F));

		PartDefinition leaf_mid_2 = leaf_2.addOrReplaceChild("leaf_mid_2", CubeListBuilder.create().texOffs(63, 56).mirror().addBox(-9.0F, 0.0F, -5.0F, 9.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 0.0F, 0.0F));

		PartDefinition leaf_tip_2 = leaf_mid_2.addOrReplaceChild("leaf_tip_2", CubeListBuilder.create().texOffs(72, 50).mirror().addBox(-4.0F, 0.0F, -3.0F, 4.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-9.0F, 0.0F, 0.0F));

		PartDefinition root_3 = roots.addOrReplaceChild("root_3", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition cube_r29 = root_3.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(2, 30).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition root_tip_3 = root_3.addOrReplaceChild("root_tip_3", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 6.0F));

		PartDefinition cube_r30 = root_tip_3.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition leaf_3 = root_3.addOrReplaceChild("leaf_3", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 2.0F));

		PartDefinition cube_r31 = leaf_3.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(66, 92).addBox(-5.0F, -1.0F, -2.0F, 10.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 4.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition leaf_mid_3 = leaf_3.addOrReplaceChild("leaf_mid_3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 6.0F));

		PartDefinition cube_r32 = leaf_mid_3.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(63, 82).addBox(-5.0F, -0.5F, -4.5F, 10.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, 4.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition leaf_tip_3 = leaf_mid_3.addOrReplaceChild("leaf_tip_3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 9.0F));

		PartDefinition cube_r33 = leaf_tip_3.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(72, 78).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition root_4 = roots.addOrReplaceChild("root_4", CubeListBuilder.create().texOffs(2, 42).addBox(1.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition root_tip_4 = root_4.addOrReplaceChild("root_tip_4", CubeListBuilder.create(), PartPose.offset(6.0F, 1.0F, 0.0F));

		PartDefinition cube_r34 = root_tip_4.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(0, 38).addBox(1.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition leaf_4 = root_4.addOrReplaceChild("leaf_4", CubeListBuilder.create().texOffs(66, 67).addBox(0.0F, 0.0F, -5.0F, 6.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -1.0F, 0.0F));

		PartDefinition leaf_mid_4 = leaf_4.addOrReplaceChild("leaf_mid_4", CubeListBuilder.create().texOffs(63, 56).addBox(0.0F, 0.0F, -5.0F, 9.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));

		PartDefinition leaf_tip_4 = leaf_mid_4.addOrReplaceChild("leaf_tip_4", CubeListBuilder.create().texOffs(72, 50).addBox(0.0F, 0.0F, -3.0F, 4.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 144, 144);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
		this.stem.yRot = state.yRot * Mth.DEG_TO_RAD;
		this.roots.yRot = state.bodyRot * Mth.DEG_TO_RAD;

		walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 3.5f,3.5f);
	}
}