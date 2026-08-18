package joshxviii.plantz.model.blueprint_machines;

import joshxviii.plantz.animation.blueprint_machines.ZombieTurretAnimation;
import joshxviii.plantz.animation.zombies.EngineerZombieAnimation;
import joshxviii.plantz.renderer.entity.BlueprintMachineRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jspecify.annotations.NonNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class ZombieTurretModel<T extends BlueprintMachineRenderState> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("zombie_turret"), "main");
	private final ModelPart body;
	private final ModelPart neck;
	private final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart turrent;
	private final KeyframeAnimation initAnimation;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation actionAnimation;


	public ZombieTurretModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.neck = this.body.getChild("neck");
		this.head = this.neck.getChild("head");
		this.jaw = this.head.getChild("jaw");
		this.turrent = this.head.getChild("turrent");
		this.initAnimation = ZombieTurretAnimation.init.bake(root);
		this.idleAnimation = ZombieTurretAnimation.idle.bake(root);
		this.actionAnimation = ZombieTurretAnimation.action.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 17).addBox(-6.0F, -3.0F, -6.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.5F, -3.5F, -6.5F, 13.0F, 4.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(32, 57).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(52, 7).addBox(-4.0F, -2.0F, 1.0F, 8.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(48, 31).addBox(4.0F, -5.5F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(48, 57).addBox(-7.0F, -6.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(48, 31).addBox(-5.0F, -5.5F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(83, 4).addBox(-3.5F, -2.0F, -3.75F, 7.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -5.0F, -1.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 57).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -5.0F, -1.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(83, 5).addBox(-3.5F, -1.0F, -4.75F, 7.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(52, 0).addBox(-4.0F, 0.0F, -5.0F, 8.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 64).addBox(-4.0F, -6.0F, -5.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -2.0F, 1.0F));

		PartDefinition turrent = head.addOrReplaceChild("turrent", CubeListBuilder.create().texOffs(48, 16).addBox(-2.0F, -4.0F, -6.0F, 4.0F, 4.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(32, 32).addBox(-2.5F, -4.5F, -5.5F, 5.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NonNull T state) {
		super.setupAnim(state);
		this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
		this.neck.yRot = state.yRot * (float) (Math.PI / 180.0);
		this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
		this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
	}
}