package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.BonkChoyAnimation;
import joshxviii.plantz.renderer.entity.PlantRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class BonkChoyModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("bonkchoy"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart head_top;
	private final ModelPart leaves;
	private final ModelPart front_leaf;
	private final ModelPart mid_leaf;
	private final ModelPart back_leaf;
	private final ModelPart right_arm;
	private final ModelPart right_arm_2;
	private final ModelPart right_arm_3;
	private final ModelPart left_arm;
	private final ModelPart left_arm2;
	private final ModelPart left_arm3;
	private final KeyframeAnimation megaPunchAnimation;

	public BonkChoyModel(ModelPart root) {
		super(
				BonkChoyAnimation.init.bake(root),
				BonkChoyAnimation.idle.bake(root),
				BonkChoyAnimation.action.bake(root),
				BonkChoyAnimation.sleep.bake(root),
null,
				root
		);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.head_top = this.head.getChild("head_top");
		this.leaves = this.head_top.getChild("leaves");
		this.front_leaf = this.leaves.getChild("front_leaf");
		this.mid_leaf = this.leaves.getChild("mid_leaf");
		this.back_leaf = this.leaves.getChild("back_leaf");
		this.right_arm = this.head.getChild("right_arm");
		this.right_arm_2 = this.right_arm.getChild("right_arm_2");
		this.right_arm_3 = this.right_arm_2.getChild("right_arm_3");
		this.left_arm = this.head.getChild("left_arm");
		this.left_arm2 = this.left_arm.getChild("left_arm2");
		this.left_arm3 = this.left_arm2.getChild("left_arm3");
		this.megaPunchAnimation = BonkChoyAnimation.uppercut.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.9875F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0125F, 0.0F));

		PartDefinition head_top = head.addOrReplaceChild("head_top", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -5.0F, -2.0F, 6.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.9875F, 0.0F));

		PartDefinition leaves = head_top.addOrReplaceChild("leaves", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition front_leaf = leaves.addOrReplaceChild("front_leaf", CubeListBuilder.create().texOffs(0, 27).addBox(-6.0F, -8.0F, 0.0F, 12.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition mid_leaf = leaves.addOrReplaceChild("mid_leaf", CubeListBuilder.create().texOffs(0, 16).addBox(-8.0F, -10.0F, 0.0F, 16.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition back_leaf = leaves.addOrReplaceChild("back_leaf", CubeListBuilder.create().texOffs(24, 27).addBox(-6.0F, -8.0F, 0.0F, 12.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));

		PartDefinition right_arm = head.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 21).addBox(0.0F, 0.0F, -2.0F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0125F, 0.0F));

		PartDefinition right_arm_2 = right_arm.addOrReplaceChild("right_arm_2", CubeListBuilder.create().texOffs(32, 11).addBox(0.0F, 0.0F, -3.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 0.0F));

		PartDefinition right_arm_3 = right_arm_2.addOrReplaceChild("right_arm_3", CubeListBuilder.create().texOffs(0, 35).addBox(0.0F, -4.0F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));

		PartDefinition left_arm = head.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(34, 35).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 0.0125F, 0.0F));

		PartDefinition left_arm2 = left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(32, 17).addBox(-5.0F, 0.0F, -2.0F, 5.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition left_arm3 = left_arm2.addOrReplaceChild("left_arm3", CubeListBuilder.create().texOffs(20, 35).addBox(-3.0F, -3.0F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public KeyframeAnimation getProcessedAction(PlantRenderState state) {
		return !state.getUseSpecialAction() ? super.getProcessedAction(state) :
				this.megaPunchAnimation;
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.head.xRot = state.xRot * (float) (Math.PI / 360.0);
		this.body.yRot = state.yRot * (float) (Math.PI / 180.0);
	}
}