package joshxviii.plantz.model.blueprint_machines;

import joshxviii.plantz.renderer.entity.BlueprintMachineRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jspecify.annotations.NonNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class ZombieDroneModel<T extends BlueprintMachineRenderState> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("drone_turret"), "main");
	private final ModelPart body;
	private final ModelPart hat;
	private final ModelPart propeller;
	private final ModelPart head;
	private final ModelPart jaw;

	public ZombieDroneModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
		this.hat = this.body.getChild("hat");
		this.propeller = this.hat.getChild("propeller");
		this.head = this.hat.getChild("head");
		this.jaw = this.head.getChild("jaw");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, -1.0F));

		PartDefinition hat = body.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition helmet_r1 = hat.addOrReplaceChild("helmet_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-4.5F, -4.0F, -4.5F, 9.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(16, 0).addBox(-4.5F, 0.0F, -6.5F, 9.0F, 0.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 34).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(3, 38).addBox(-0.5F, -6.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.5F, -5.0F, -5.5F, 3.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.75F, 0.0F, -0.0872F, 0.0038F, 0.0435F));

		PartDefinition propeller = hat.addOrReplaceChild("propeller", CubeListBuilder.create().texOffs(14, 12).addBox(-11.0F, -0.5F, -11.0F, 22.0F, 0.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = hat.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(52, 7).addBox(-4.0F, 6.0F, 1.0F, 8.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(83, 4).addBox(-3.5F, 6.0F, -3.75F, 7.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(83, 5).addBox(-3.5F, -1.0F, -4.75F, 7.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(52, 0).addBox(-4.0F, 0.0F, -5.0F, 8.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 64).addBox(-4.0F, -6.0F, -5.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 6.0F, 1.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NonNull T state) {
		super.setupAnim(state);
	}
}