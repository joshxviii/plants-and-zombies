package joshxviii.plantz.model.blueprint_machines;

import joshxviii.plantz.animation.blueprint_machines.ZombieDroneAnimation;
import joshxviii.plantz.renderer.entity.BlueprintMachineRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class ZombieDroneModel<T extends BlueprintMachineRenderState> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("drone_turret"), "main");
	private final ModelPart body;
	private final ModelPart hat;
	private final ModelPart propeller;
	private final ModelPart head;
	private final ModelPart face;
	private final ModelPart jaw;
	private final KeyframeAnimation initAnimation;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation actionAnimation;

	public ZombieDroneModel(ModelPart root) {
        super(root);
		this.body = root.getChild("body");
		this.propeller = this.body.getChild("propeller");
		this.head = this.body.getChild("head");
		this.face = this.head.getChild("face");
		this.jaw = this.face.getChild("jaw");
		this.hat = this.head.getChild("hat");
		this.initAnimation = ZombieDroneAnimation.init.bake(root);
		this.idleAnimation = ZombieDroneAnimation.idle.bake(root);
		this.actionAnimation = ZombieDroneAnimation.action.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, -1.0F));

		PartDefinition propeller = body.addOrReplaceChild("propeller", CubeListBuilder.create().texOffs(14, 12).addBox(-11.0F, -0.5F, -11.0F, 22.0F, 0.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition face = head.addOrReplaceChild("face", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -0.05F, -3.95F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(52, 7).addBox(-4.0F, 5.95F, 1.05F, 8.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(83, 4).addBox(-3.5F, 5.95F, -3.7F, 7.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-4.0F, -0.05F, -3.95F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.05F, -0.05F));

		PartDefinition jaw = face.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(83, 5).addBox(-3.5F, -1.0F, -4.75F, 7.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(52, 0).addBox(-4.0F, 0.0F, -5.0F, 8.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(0, 64).addBox(-4.0F, -6.0F, -5.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 5.95F, 1.05F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition helmet_r1 = hat.addOrReplaceChild("helmet_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-4.5F, -4.0F, -4.5F, 9.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(16, 0).addBox(-4.5F, 0.0F, -6.5F, 9.0F, 0.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 34).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(3, 38).addBox(-0.5F, -6.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-1.5F, -5.0F, -5.5F, 3.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.75F, 0.0F, -0.0872F, 0.0038F, 0.0435F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NonNull T state) {
		super.setupAnim(state);
		this.body.xRot = state.xRot * Mth.DEG_TO_RAD * .25f;

		this.head.xRot = state.xRot * Mth.DEG_TO_RAD * .1f;
		this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
		this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
		this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
	}
}