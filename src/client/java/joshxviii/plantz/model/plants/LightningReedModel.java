package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.LightningReedAnimation;
import joshxviii.plantz.renderer.entity.PlantRenderState;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;


public class LightningReedModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("lightning_reed"), "main");
	private final ModelPart body;
	private final ModelPart stem;
	private final ModelPart head;
	private final ModelPart eyes;
	private final ModelPart eyebrows;
	private final ModelPart left_eyebrow;
	private final ModelPart right_eyebrow;
	private final ModelPart leaves;
	private final ModelPart leaf_1;
	private final ModelPart leaf_1_tip;
	private final ModelPart leaf_2;
	private final ModelPart leaf_2_tip;

	public LightningReedModel(ModelPart root) {
		super(
			LightningReedAnimation.init.bake(root),
			LightningReedAnimation.idle.bake(root),
			LightningReedAnimation.action.bake(root),
			LightningReedAnimation.sleep.bake(root),
			null,
			root
		);
		this.body = root.getChild("body");
		this.stem = this.body.getChild("stem");
		this.head = this.stem.getChild("head");
		this.eyes = this.head.getChild("eyes");
		this.eyebrows = this.eyes.getChild("eyebrows");
		this.left_eyebrow = this.eyebrows.getChild("left_eyebrow");
		this.right_eyebrow = this.eyebrows.getChild("right_eyebrow");
		this.leaves = this.body.getChild("leaves");
		this.leaf_1 = this.leaves.getChild("leaf_1");
		this.leaf_1_tip = this.leaf_1.getChild("leaf_1_tip");
		this.leaf_2 = this.leaves.getChild("leaf_2");
		this.leaf_2_tip = this.leaf_2.getChild("leaf_2_tip");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition stem = body.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(2, 2).addBox(0.0F, -8.0F, -4.0F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = stem.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 16).addBox(-1.5F, -7.0F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(32, 32).addBox(2.0F, 0.5F, 2.0F, -4.0F, -8.0F, -4.0F, new CubeDeformation(0.0F))
				.texOffs(20, 14).addBox(0.0F, -9.0F, -0.5F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(27, 30).addBox(0.5F, -6.5F, 0.5F, -1.0F, -3.0F, -1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(17, 10).addBox(-2.5F, -1.25F, 0.0F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.75F, -1.75F));

		PartDefinition eyebrows = eyes.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -2.25F, 0.0F));

		PartDefinition left_eyebrow = eyebrows.addOrReplaceChild("left_eyebrow", CubeListBuilder.create().texOffs(17, 9).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 0.0F, 0.0F));

		PartDefinition right_eyebrow = eyebrows.addOrReplaceChild("right_eyebrow", CubeListBuilder.create().texOffs(17, 9).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 0.0F, 0.0F));

		PartDefinition leaves = body.addOrReplaceChild("leaves", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = leaves.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(12, 12).mirror().addBox(0.0F, -3.0F, -2.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 2.3562F));

		PartDefinition cube_r2 = leaves.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(12, 12).addBox(0.0F, -3.0F, -2.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaf_1 = leaves.addOrReplaceChild("leaf_1", CubeListBuilder.create().texOffs(16, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.5F));

		PartDefinition leaf_1_tip = leaf_1.addOrReplaceChild("leaf_1_tip", CubeListBuilder.create().texOffs(16, 13).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 4.0F));

		PartDefinition leaf_2 = leaves.addOrReplaceChild("leaf_2", CubeListBuilder.create().texOffs(16, 4).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -0.5F));

		PartDefinition leaf_2_tip = leaf_2.addOrReplaceChild("leaf_2_tip", CubeListBuilder.create().texOffs(20, 15).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -4.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		//this.body.zRot = Mth.sin(state.ageInTicks * 1.5f)*0.01f;
		this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
		super.setupAnim(state);
		this.stem.yRot = state.yRot * (float) (Math.PI / 180.0);
	}
}