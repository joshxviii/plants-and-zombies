package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.CoffeeBeanAnimation;
import joshxviii.plantz.animation.plants.SquashAnimation;
import joshxviii.plantz.renderer.entity.plant.PlantRenderState;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;


public class SquashModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("squash"), "main");
	private final ModelPart body;
	private final ModelPart brow;

	public SquashModel(ModelPart root) {
		super(root);

		initAnimation = SquashAnimation.init.bake(root);
		idleAnimation = SquashAnimation.idle.bake(root);
		actionAnimation = SquashAnimation.action.bake(root);
		sleepAnimation = SquashAnimation.sleep.bake(root);

		this.body = root.getChild("body");
		this.brow = this.body.getChild("brow");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -10.0F, -8.0F, 16.0F, 10.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(64, 0).addBox(-8.0F, -10.0F, -8.0F, 16.0F, 10.0F, 16.0F, new CubeDeformation(0.5F))
				.texOffs(56, 26).addBox(-7.0F, -18.0F, -7.0F, 14.0F, 8.0F, 14.0F, new CubeDeformation(0.5F))
				.texOffs(0, 26).addBox(-7.0F, -18.0F, -7.0F, 14.0F, 8.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(26, 46).addBox(0.0F, -4.0F, -1.0F, 0.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.0F, 0.0F, 0.331F, -0.1486F, 0.3656F));

		PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(26, 46).addBox(0.0F, -4.0F, -1.0F, 0.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.0F, 0.0F, 2.0025F, -1.2091F, -1.612F));

		PartDefinition brow = body.addOrReplaceChild("brow", CubeListBuilder.create().texOffs(0, 48).addBox(-6.0F, -1.0F, -1.0F, 12.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, -7.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.body.yRot += state.yRot * (float) (Mth.PI / 180.0);
	}
}