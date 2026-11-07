package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.CoffeeBeanAnimation;
import joshxviii.plantz.animation.plants.PumpkinAnimation;
import joshxviii.plantz.renderer.entity.plant.PlantRenderState;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;


public class PumpkinModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("pumpkin"), "main");
	private final ModelPart body;

	public PumpkinModel(ModelPart root) {
		super(root);

		initAnimation = PumpkinAnimation.init.bake(root);
		idleAnimation = PumpkinAnimation.idle.bake(root);
		sleepAnimation = PumpkinAnimation.sleep.bake(root);

		this.body = root.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(4, 24).addBox(-7.0F, -4.0F, -7.0F, 14.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
	}
}