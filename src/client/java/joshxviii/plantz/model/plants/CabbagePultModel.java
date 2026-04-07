package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.plants.CabbagePultAnimation;
import joshxviii.plantz.animation.plants.CactusAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class CabbagePultModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("cabbagepult"), "main");
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
	private final ModelPart leaf_2;
	private final ModelPart leaf_3;
	private final ModelPart leaf_4;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation actionAnimation;
	private final KeyframeAnimation initAnimation;
	private final KeyframeAnimation sleepAnimation;

	public CabbagePultModel(ModelPart root) {
		super(root);
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
		this.leaf_2 = this.leaves.getChild("leaf_2");
		this.leaf_3 = this.leaves.getChild("leaf_3");
		this.leaf_4 = this.leaves.getChild("leaf_4");
		this.initAnimation = CabbagePultAnimation.init.bake(root);
		this.idleAnimation = CabbagePultAnimation.idle.bake(root);
		this.actionAnimation = CabbagePultAnimation.action.bake(root);
		this.sleepAnimation = CabbagePultAnimation.sleep.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(28, 33).addBox(-1.5F, -12.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(25, 1).addBox(-6.0F, -5.7F, 0.0F, 13.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -6.5F, -2.0F, 0.9599F, 0.0F, 0.0F));

		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 1).addBox(-6.0F, -6.0F, 0.0F, 13.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -5.5F, 3.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -6.5F, 0.0F));

		PartDefinition eyebrow_L = eyebrows.addOrReplaceChild("eyebrow_L", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, -5.125F));

		PartDefinition cube_r3 = eyebrow_L.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition eyebrow_R = eyebrows.addOrReplaceChild("eyebrow_R", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, -5.125F));

		PartDefinition coil = head.addOrReplaceChild("coil", CubeListBuilder.create().texOffs(12, 46).addBox(0.0F, -10.0F, -1.0F, 0.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition coil_2 = coil.addOrReplaceChild("coil_2", CubeListBuilder.create().texOffs(12, 46).addBox(0.0F, -10.0F, -1.0F, 0.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition basket = coil_2.addOrReplaceChild("basket", CubeListBuilder.create().texOffs(0, 33).addBox(-4.0F, -8.0F, 2.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(1, 49).addBox(-4.0F, -7.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(1, 43).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

		PartDefinition cube_r4 = basket.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(1, 49).addBox(-0.5F, -3.0F, -2.5F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -4.0F, 0.5F, 0.0F, 0.0F, -3.1416F));

		PartDefinition cube_r5 = basket.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(1, 43).addBox(-4.0F, -0.5F, -2.5F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 0.0F, 0.0F, -3.1416F));

		PartDefinition projectile = basket.addOrReplaceChild("projectile", CubeListBuilder.create().texOffs(71, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, -1.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition leaves = body.addOrReplaceChild("leaves", CubeListBuilder.create(), PartPose.offset(0.0F, -0.4F, 0.0F));

		PartDefinition leaf_1 = leaves.addOrReplaceChild("leaf_1", CubeListBuilder.create().texOffs(43, 0).addBox(-5.0F, 0.0F, -6.0F, 10.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.4F, -5.0F));

		PartDefinition leaf_2 = leaves.addOrReplaceChild("leaf_2", CubeListBuilder.create().texOffs(43, 0).addBox(-5.0F, 0.0F, -6.0F, 10.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 0.4F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition leaf_3 = leaves.addOrReplaceChild("leaf_3", CubeListBuilder.create().texOffs(43, 0).addBox(-5.0F, 0.0F, -6.0F, 10.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.4F, 5.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition leaf_4 = leaves.addOrReplaceChild("leaf_4", CubeListBuilder.create().texOffs(43, 0).addBox(-5.0F, 0.0F, -6.0F, 10.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.4F, 0.0F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
		this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
		this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
		this.sleepAnimation.apply(state.getSleepAnimationState(), state.ageInTicks);
	}
}