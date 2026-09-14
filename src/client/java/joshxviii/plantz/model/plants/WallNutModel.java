package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.WallNutAnimation;
import joshxviii.plantz.renderer.entity.plant.PlantRenderState;
import joshxviii.plantz.renderer.entity.plant.WallNutRenderState;
import joshxviii.plantz.renderer.entity.zombie.RoboZombieRenderState;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static joshxviii.plantz.UtilsKt.pazResource;

public class WallNutModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("wallnut"), "main");
	private final ModelPart roll;
	private final ModelPart body;
	private final ModelPart inner;

	public WallNutModel(ModelPart root) {
		super(root);

		initAnimation = WallNutAnimation.init.bake(root);
		idleAnimation = WallNutAnimation.idle.bake(root);
		actionAnimation = WallNutAnimation.action.bake(root);
		sleepAnimation = WallNutAnimation.sleep.bake(root);

		this.roll = root.getChild("roll");
		this.body = roll.getChild("body");
		this.inner = this.body.getChild("inner");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition roll = partdefinition.addOrReplaceChild("roll", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));

		PartDefinition body = roll.addOrReplaceChild("body", CubeListBuilder.create().texOffs(3, 5).addBox(-7.0F, -15.0F, -7.0F, 14.0F, 13.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(59, 37).addBox(-6.0F, -18.0F, -6.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(59, 54).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 9.0F, 0.0F));

		PartDefinition inner = body.addOrReplaceChild("inner", CubeListBuilder.create().texOffs(3, 37).addBox(-6.0F, -15.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(67, 2).addBox(-5.0F, -17.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(67, 16).addBox(-5.0F, -3.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		if (!(state instanceof WallNutRenderState wallNutState)) return;
		super.setupAnim(state);
		roll.xRot = 0.0F;
		roll.yRot = 0.0F;
		roll.zRot = 0.0F;

		Quaternionf q = wallNutState.getRollRotation();

		if (wallNutState.isRolling()) {
			roll.rotateBy(q);
		}
	}
}