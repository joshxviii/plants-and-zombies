package joshxviii.plantz.model.blueprint_machines;

import joshxviii.plantz.renderer.entity.BlueprintMachineRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jspecify.annotations.NonNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class ElectroTurretModel<T extends BlueprintMachineRenderState> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("electro_turret"), "main");
	private final ModelPart body;
	private final ModelPart neck;
	private final ModelPart right_arm;
	private final ModelPart right_hand;
	private final ModelPart left_arm;
	private final ModelPart left_hand;
	private final ModelPart head;
	private final ModelPart turrent;

	public ElectroTurretModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.neck = this.body.getChild("neck");
		this.right_arm = this.neck.getChild("right_arm");
		this.right_hand = this.right_arm.getChild("right_hand");
		this.left_arm = this.neck.getChild("left_arm");
		this.left_hand = this.left_arm.getChild("left_hand");
		this.head = this.neck.getChild("head");
		this.turrent = this.head.getChild("turrent");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-6.0F, -3.0F, -6.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.5F, -3.5F, -6.5F, 13.0F, 4.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(32, 57).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(36, 66).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition right_arm = neck.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(53, 24).addBox(0.0F, -1.0F, -0.9917F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(53, 17).addBox(3.0F, -1.0F, -5.9917F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -4.0F, -0.0083F));

		PartDefinition right_hand = right_arm.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(52, 1).addBox(0.0F, -4.0F, -8.0F, 0.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 0.0F, -4.9917F));

		PartDefinition left_arm = neck.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(53, 24).mirror().addBox(-5.0F, -1.0F, -0.9917F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(53, 17).mirror().addBox(-5.0F, -1.0F, -5.9917F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, -4.0F, -0.0083F));

		PartDefinition left_hand = left_arm.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(52, 1).mirror().addBox(0.0F, -4.0F, -7.0F, 0.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-4.0F, 0.0F, -5.9917F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(48, 31).addBox(4.0F, -5.5F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(48, 57).addBox(-7.0F, -6.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(48, 31).addBox(-5.0F, -5.5F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(32, 43).addBox(-0.5F, -12.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 57).addBox(-1.0F, -14.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -7.0F, -1.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(33, 36).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -11.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition cube_r2 = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(29, 41).addBox(-3.5F, 0.0F, -3.5F, 7.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 0.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition cube_r3 = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 57).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -5.0F, -1.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition turrent = head.addOrReplaceChild("turrent", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NonNull T state) {
		super.setupAnim(state);
	}
}