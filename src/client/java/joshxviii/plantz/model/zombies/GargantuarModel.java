package joshxviii.plantz.model.zombies;

import joshxviii.plantz.PazZombieRenderState;
import joshxviii.plantz.animation.zombies.GargantuarAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class GargantuarModel extends PazZombieModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("gargantuar"), "main");
	private final KeyframeAnimation actionAnimation;
	private final KeyframeAnimation initAnimation;


	public GargantuarModel(final ModelPart root) {
		super(root.getChild("root"));
		this.initAnimation = GargantuarAnimation.init.bake(root.getChild("root"));
		this.actionAnimation = GargantuarAnimation.attack.bake(root.getChild("root"));
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(46, 63).addBox(-5.0F, -9.2648F, -9.591F, 10.0F, 11.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.7352F, -4.409F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, -3.75F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -13.0593F, -5.3701F, 20.0F, 13.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(64, 0).addBox(-6.0F, -14.0593F, -6.3701F, 12.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 63).addBox(-7.0F, -0.0593F, -2.3701F, 14.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.9407F, -0.6299F));

		PartDefinition basket = body.addOrReplaceChild("basket", CubeListBuilder.create().texOffs(72, 15).addBox(-4.0F, -2.0F, 0.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0593F, 6.6299F));

		PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(36, 25).addBox(-8.0F, -3.0F, -5.0F, 8.0F, 28.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-10.0F, -18.0F, 0.0F));

		PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 25).addBox(0.0F, -3.0F, -5.0F, 8.0F, 28.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(10.0F, -18.0F, 0.0F));

		PartDefinition pole = left_arm.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(114, 102).addBox(-0.5F, -10.5F, -32.2857F, 2.0F, 21.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(114, 102).addBox(-0.5F, -10.5F, -21.2857F, 2.0F, 21.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(112, 94).addBox(-1.5F, -9.5F, -36.2857F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(112, 94).addBox(-1.5F, 5.5F, -36.2857F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(112, 94).addBox(-1.5F, -9.5F, -25.2857F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(112, 94).addBox(-1.5F, 5.5F, -25.2857F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.5F, 20.5F, -0.7143F));

		PartDefinition pole_r1 = pole.addOrReplaceChild("pole_r1", CubeListBuilder.create().texOffs(0, 118).addBox(-23.0F, -2.5F, -2.5F, 47.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 0.0F, -10.7857F, 0.0F, 1.5708F, 0.0F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 80).addBox(-4.0F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 0.0F, 2.0F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(72, 34).addBox(-2.0F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 2.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull ZombieRenderState state) {
		state.isAggressive = false;
		super.setupAnim(state);

		float animationPos = state.walkAnimationPos;
		float animationSpeed = state.walkAnimationSpeed;
		this.rightArm.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 0.6F * animationSpeed * 0.5F / state.speedValue;
		this.leftArm.xRot = Mth.cos(animationPos * 0.6662F) * 0.6F * animationSpeed * 0.5F / state.speedValue;


		PazZombieRenderState pazState = (PazZombieRenderState) state;
		initAnimation.apply(pazState.getInitAnimationState(), pazState.ageInTicks);
		actionAnimation.applyWalk(state.attackTime*4, 1.0f, 1.0f, 1.0f);
	}
}