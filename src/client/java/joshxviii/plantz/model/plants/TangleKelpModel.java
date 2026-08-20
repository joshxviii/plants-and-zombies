package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.TangleKelpAnimation;
import joshxviii.plantz.renderer.entity.PlantRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class TangleKelpModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("tanglekelp"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart right_kelp;
	private final ModelPart right_kelp2;
	private final ModelPart left_kelp;
	private final ModelPart left_kelp2;
	private final ModelPart left_top_kelp;
	private final ModelPart left_top_kelp2;
	private final ModelPart right_top_kelp;
	private final ModelPart right_top_kelp2;
	private final ModelPart left_bottom_kelp;
	private final ModelPart left_bottom_kelp2;
	private final ModelPart right_bottom_kelp;
	private final ModelPart right_bottom_kelp2;
	private final ModelPart roots;
	private final KeyframeAnimation initLandAnimation;
	private final KeyframeAnimation idleLandAnimation;

	public TangleKelpModel(ModelPart root) {
		super(
				TangleKelpAnimation.init.bake(root),
				TangleKelpAnimation.idle.bake(root),
				null,
				TangleKelpAnimation.idle_land.bake(root),
null,
				root
		);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.right_kelp = this.body.getChild("right_kelp");
		this.right_kelp2 = this.right_kelp.getChild("right_kelp2");
		this.left_kelp = this.body.getChild("left_kelp");
		this.left_kelp2 = this.left_kelp.getChild("left_kelp2");
		this.left_top_kelp = this.body.getChild("left_top_kelp");
		this.left_top_kelp2 = this.left_top_kelp.getChild("left_top_kelp2");
		this.right_top_kelp = this.body.getChild("right_top_kelp");
		this.right_top_kelp2 = this.right_top_kelp.getChild("right_top_kelp2");
		this.left_bottom_kelp = this.body.getChild("left_bottom_kelp");
		this.left_bottom_kelp2 = this.left_bottom_kelp.getChild("left_bottom_kelp2");
		this.right_bottom_kelp = this.body.getChild("right_bottom_kelp");
		this.right_bottom_kelp2 = this.right_bottom_kelp.getChild("right_bottom_kelp2");
		this.roots = this.body.getChild("roots");
		this.initLandAnimation = TangleKelpAnimation.init_land.bake(root);
		this.idleLandAnimation = TangleKelpAnimation.idle_land.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 23.5F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 16).addBox(-7.0F, -2.2333F, -5.025F, 14.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-7.5F, -2.4833F, -5.525F, 15.0F, 5.0F, 11.0F, new CubeDeformation(0.0F))
				.texOffs(32, 33).addBox(-5.0F, -4.2333F, -3.025F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(32, 33).addBox(2.0F, -4.2333F, -4.025F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(32, 33).addBox(3.0F, -4.2333F, 2.975F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(32, 33).addBox(2.0F, 1.7667F, -1.025F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(32, 33).addBox(-4.0F, 1.7667F, 2.975F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(28, 30).addBox(6.0F, -2.2333F, 3.975F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.2333F, 0.025F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(28, 30).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.5F, 1.7667F, -4.525F, 0.0F, 1.5708F, 0.0F));

		PartDefinition right_kelp = body.addOrReplaceChild("right_kelp", CubeListBuilder.create().texOffs(0, 30).addBox(-5.0F, 0.0F, -3.0F, 2.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.0F, 0.0F));

		PartDefinition right_kelp2 = right_kelp.addOrReplaceChild("right_kelp2", CubeListBuilder.create().texOffs(14, 30).addBox(-4.0F, 0.0F, -3.0F, 4.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.0F, 0.0F));

		PartDefinition cube_r2 = right_kelp2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 33).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition left_kelp = body.addOrReplaceChild("left_kelp", CubeListBuilder.create().texOffs(0, 36).addBox(3.0F, 0.0F, -3.0F, 2.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, 0.0F));

		PartDefinition cube_r3 = left_kelp.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(32, 33).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 0.0F, 2.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition left_kelp2 = left_kelp.addOrReplaceChild("left_kelp2", CubeListBuilder.create().texOffs(47, 43).addBox(0.0F, 0.0F, -3.0F, 4.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition left_top_kelp = body.addOrReplaceChild("left_top_kelp", CubeListBuilder.create().texOffs(51, 34).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 0.0F, 5.0F));

		PartDefinition left_top_kelp2 = left_top_kelp.addOrReplaceChild("left_top_kelp2", CubeListBuilder.create().texOffs(14, 37).addBox(-1.5F, 0.0F, 2.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(32, 33).addBox(0.5F, -1.0F, 3.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, 0.0F));

		PartDefinition right_top_kelp = body.addOrReplaceChild("right_top_kelp", CubeListBuilder.create().texOffs(51, 38).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 0.0F, 5.0F));

		PartDefinition right_top_kelp2 = right_top_kelp.addOrReplaceChild("right_top_kelp2", CubeListBuilder.create().texOffs(24, 37).addBox(-3.5F, 0.0F, 2.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(32, 33).addBox(-3.5F, -1.0F, 3.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, 0.0F));

		PartDefinition left_bottom_kelp = body.addOrReplaceChild("left_bottom_kelp", CubeListBuilder.create().texOffs(51, 36).addBox(-1.5F, 0.0F, -4.0F, 5.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 0.0F, -3.0F));

		PartDefinition left_bottom_kelp2 = left_bottom_kelp.addOrReplaceChild("left_bottom_kelp2", CubeListBuilder.create().texOffs(38, 30).addBox(-2.5F, 0.0F, -5.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(32, 33).addBox(-0.5F, -1.0F, -1.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, -4.0F));

		PartDefinition right_bottom_kelp = body.addOrReplaceChild("right_bottom_kelp", CubeListBuilder.create().texOffs(51, 40).addBox(-3.5F, 0.0F, -4.0F, 5.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(32, 33).addBox(-3.5F, -1.0F, -4.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 0.0F, -3.0F));

		PartDefinition right_bottom_kelp2 = right_bottom_kelp.addOrReplaceChild("right_bottom_kelp2", CubeListBuilder.create().texOffs(38, 35).addBox(-2.5F, 0.0F, -5.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 0.0F, -4.0F));

		PartDefinition roots = body.addOrReplaceChild("roots", CubeListBuilder.create().texOffs(0, 42).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.5F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public KeyframeAnimation getProcessedInit(PlantRenderState state) {
		return state.isInWater ? super.getProcessedInit(state) :
				this.initLandAnimation;
	}

	@Override
	public KeyframeAnimation getProcessedIdle(PlantRenderState state) {
		return state.isInWater ? super.getProcessedIdle(state) :
				this.idleLandAnimation;
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		this.body.yRot = state.yRot;
		super.setupAnim(state);
	}
}