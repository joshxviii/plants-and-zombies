package joshxviii.plantz.model.blueprint_machines;

import joshxviii.plantz.renderer.entity.BlueprintMachineRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jspecify.annotations.NonNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class LawnMowerModel<T extends BlueprintMachineRenderState> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("lawn_mower"), "main");
	private final ModelPart body;
	private final ModelPart motor;
	private final ModelPart front_wheels;
	private final ModelPart back_wheels;
	private final ModelPart handle;

	public LawnMowerModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.motor = this.body.getChild("motor");
		this.front_wheels = this.body.getChild("front_wheels");
		this.back_wheels = this.body.getChild("back_wheels");
		this.handle = this.body.getChild("handle");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -5.0F, -7.0F, 14.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition motor = body.addOrReplaceChild("motor", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(40, 37).addBox(-5.0F, -4.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(28, 30).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(32, 18).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition front_wheels = body.addOrReplaceChild("front_wheels", CubeListBuilder.create().texOffs(28, 37).addBox(6.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(28, 37).addBox(-8.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -6.0F));

		PartDefinition back_wheels = body.addOrReplaceChild("back_wheels", CubeListBuilder.create().texOffs(28, 37).addBox(6.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(28, 37).addBox(-8.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 6.0F));

		PartDefinition handle = body.addOrReplaceChild("handle", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 6.0F));

		PartDefinition cube_r1 = handle.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 30).addBox(-7.0F, -16.0F, 0.0F, 14.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NonNull T state) {
		super.setupAnim(state);
	}
}