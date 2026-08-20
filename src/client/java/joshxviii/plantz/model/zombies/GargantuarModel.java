package joshxviii.plantz.model.zombies;

import joshxviii.plantz.animation.zombies.GargantuarAnimation;
import joshxviii.plantz.renderer.entity.GargantuarRenderState;
import joshxviii.plantz.renderer.entity.PazZombieRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class GargantuarModel extends PazZombieModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("gargantuar"), "main");
	private final KeyframeAnimation walkAnimation;
	private final KeyframeAnimation punchAnimation;
	private final KeyframeAnimation smashAnimation;
	private final KeyframeAnimation throwAnimation;

	public GargantuarModel(final ModelPart root) {
		super(
			GargantuarAnimation.init.bake(root.getChild("root")),
			root
		);
		this.walkAnimation = GargantuarAnimation.walk.bake(root.getChild("root"));
		this.punchAnimation = GargantuarAnimation.action.bake(root.getChild("root"));
		this.throwAnimation = GargantuarAnimation.toss.bake(root.getChild("root"));
		this.smashAnimation = GargantuarAnimation.attack.bake(root.getChild("root"));
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

		PartDefinition imp = basket.addOrReplaceChild("imp", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 3.6667F));

		PartDefinition head2 = imp.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(49, 84).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.3333F));

		PartDefinition hat2 = head2.addOrReplaceChild("hat2", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, -0.5F));

		PartDefinition body2 = imp.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(49, 98).addBox(-3.5F, 0.0F, -2.5F, 7.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.3333F));

		PartDefinition right_arm2 = imp.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(73, 98).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 1.0F, 0.3333F, -1.5708F, 0.0F, 0.0F));

		PartDefinition left_arm2 = imp.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(73, 106).addBox(0.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 1.0F, 0.3333F, -1.5708F, 0.0F, 0.0F));

		PartDefinition right_leg2 = imp.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(49, 109).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 6.0F, 0.3333F));

		PartDefinition left_leg2 = imp.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(57, 109).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 6.0F, 0.3333F));

		PartDefinition barrel = imp.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(13, 100).addBox(-4.5F, 30.0F, -14.1667F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -27.0F, 10.0F));

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
	public void setupAnim(@NotNull PazZombieRenderState state) {
		super.setupAnim(state);
		this.resetPose();
		this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
		this.head.yRot = state.yRot * (float) (Math.PI / 180.0);

		float animationPos = state.walkAnimationPos;
		float animationSpeed = state.walkAnimationSpeed;

		walkAnimation.applyWalk(animationPos, animationSpeed, 2.0f, 2.0f);

		GargantuarRenderState gargState = (GargantuarRenderState) state;
		initAnimation.apply(gargState.getEmergeAnimationState(), gargState.ageInTicks);
		punchAnimation.apply(gargState.getPunchAnimationState(), gargState.ageInTicks);
		smashAnimation.apply(gargState.getSmashAnimationState(), gargState.ageInTicks);
		throwAnimation.apply(gargState.getThrowImpAnimationState(), gargState.ageInTicks);
	}
}
