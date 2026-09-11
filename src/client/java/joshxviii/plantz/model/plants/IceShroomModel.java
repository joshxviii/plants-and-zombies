package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.HypnoShroomAnimation;
import joshxviii.plantz.animation.plants.IceShroomAnimation;
import joshxviii.plantz.entity.plant.IceShroom;
import joshxviii.plantz.renderer.entity.plant.PlantRenderState;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class IceShroomModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("iceshroom"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart cap;

	public IceShroomModel(ModelPart root) {
		super(root);

		initAnimation = IceShroomAnimation.init.bake(root);
		idleAnimation = IceShroomAnimation.idle.bake(root);
		actionAnimation = IceShroomAnimation.action.bake(root);
		sleepAnimation = IceShroomAnimation.sleep.bake(root);

		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.cap = this.body.getChild("cap");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cap = body.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -1.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 27).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition cube_r1 = cap.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(18, 35).addBox(-0.5F, -6.0F, -0.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -1.0F, -0.5F, 0.0F, 0.0F, 1.0036F));

		PartDefinition cube_r2 = cap.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(18, 35).addBox(-1.5F, -6.0F, -0.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -1.0F, 1.5F, -1.0036F, 0.0F, 0.0F));

		PartDefinition cube_r3 = cap.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(18, 35).addBox(-1.5F, -6.0F, -1.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -1.0F, 0.5F, 0.0F, 0.0F, -1.0036F));

		PartDefinition cube_r4 = cap.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(18, 35).addBox(-0.5F, -6.0F, -1.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.0F, -1.5F, 1.0036F, 0.0F, 0.0F));

		PartDefinition cube_r5 = cap.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(16, 27).addBox(-1.5F, -5.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.25F, -1.0F, -2.25F, 0.3655F, -0.147F, 0.3655F));

		PartDefinition cube_r6 = cap.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(16, 27).addBox(-1.5F, -5.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.25F, -1.0F, -2.25F, 0.3655F, 0.147F, -0.3655F));

		PartDefinition cube_r7 = cap.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(16, 27).addBox(-1.5F, -5.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.25F, -1.0F, 2.25F, -0.3655F, -0.147F, -0.3655F));

		PartDefinition cube_r8 = cap.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(16, 27).addBox(-1.5F, -5.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.25F, -1.0F, 2.25F, -0.3655F, 0.147F, 0.3655F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 12).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.body.yRot = state.yRot * (float) (Math.PI / 180.0);
	}
}