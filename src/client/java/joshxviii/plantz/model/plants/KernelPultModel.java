package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.plants.KernelPultAnimation;
import joshxviii.plantz.animation.plants.MelonPultAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;


public class KernelPultModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("kernelpult"), "main");
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

	public KernelPultModel(ModelPart root) {
		super(
			KernelPultAnimation.init.bake(root),
			KernelPultAnimation.idle.bake(root),
			KernelPultAnimation.action.bake(root),
			KernelPultAnimation.sleep.bake(root),
				null,
			root
		);
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

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 22).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(40, 36).addBox(-4.0F, -14.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(28, 42).addBox(-1.5F, -16.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition eyebrow_L = eyebrows.addOrReplaceChild("eyebrow_L", CubeListBuilder.create(), PartPose.offset(-2.0F, -3.5F, -5.125F));

		PartDefinition cube_r1 = eyebrow_L.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 29).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition eyebrow_R = eyebrows.addOrReplaceChild("eyebrow_R", CubeListBuilder.create().texOffs(0, 29).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -3.5F, -5.125F));

		PartDefinition coil = head.addOrReplaceChild("coil", CubeListBuilder.create().texOffs(12, 55).addBox(0.0F, -10.0F, -1.0F, 0.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, 0.0F));

		PartDefinition coil_2 = coil.addOrReplaceChild("coil_2", CubeListBuilder.create().texOffs(12, 55).addBox(0.0F, -10.0F, -1.0F, 0.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition basket = coil_2.addOrReplaceChild("basket", CubeListBuilder.create().texOffs(0, 42).addBox(-4.0F, -8.0F, 2.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(1, 58).addBox(-4.0F, -7.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(1, 52).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition cube_r2 = basket.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(1, 58).addBox(-0.5F, -3.0F, -2.5F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -4.0F, 0.5F, 0.0F, 0.0F, -3.1416F));

		PartDefinition cube_r3 = basket.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(1, 52).addBox(-4.0F, -0.5F, -2.5F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

		PartDefinition projectile = basket.addOrReplaceChild("projectile", CubeListBuilder.create().texOffs(74, 0).addBox(-6.0F, -3.5F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(74, 8).addBox(-10.0F, -4.0F, -3.0F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.5F, 3.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition leaves = body.addOrReplaceChild("leaves", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -10.1F, -5.5F, 11.0F, 11.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.4F, 0.0F));

		PartDefinition leaf_1 = leaves.addOrReplaceChild("leaf_1", CubeListBuilder.create().texOffs(44, 0).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.6F, -5.0F));

		PartDefinition leaf_tip_1 = leaf_1.addOrReplaceChild("leaf_tip_1", CubeListBuilder.create().texOffs(48, 8).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -8.0F));

		PartDefinition leaf_2 = leaves.addOrReplaceChild("leaf_2", CubeListBuilder.create().texOffs(44, 0).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -2.6F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition leaf_tip_2 = leaf_2.addOrReplaceChild("leaf_tip_2", CubeListBuilder.create().texOffs(48, 8).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -8.0F));

		PartDefinition leaf_3 = leaves.addOrReplaceChild("leaf_3", CubeListBuilder.create().texOffs(44, 0).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.6F, 5.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition leaf_tip_3 = leaf_3.addOrReplaceChild("leaf_tip_3", CubeListBuilder.create().texOffs(48, 8).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -8.0F));

		PartDefinition leaf_4 = leaves.addOrReplaceChild("leaf_4", CubeListBuilder.create().texOffs(44, 0).addBox(-3.0F, 0.0F, -8.0F, 6.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -2.6F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition leaf_tip_4 = leaf_4.addOrReplaceChild("leaf_tip_4", CubeListBuilder.create().texOffs(48, 8).addBox(-3.0F, 0.0F, -4.0F, 6.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -8.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}


	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
		super.setupAnim(state);
	}
}