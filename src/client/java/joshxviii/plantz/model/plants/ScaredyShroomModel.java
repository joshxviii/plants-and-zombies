package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.plants.ScaredyShroomAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;


public class ScaredyShroomModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("scaredyshroom"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart barrel;
	private final ModelPart cap;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation actionAnimation;
	private final KeyframeAnimation initAnimation;
	private final KeyframeAnimation sleepAnimation;
	private final KeyframeAnimation hideAnimation;

	public ScaredyShroomModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.barrel = this.head.getChild("barrel");
		this.cap = this.body.getChild("cap");
		this.initAnimation = ScaredyShroomAnimation.init.bake(root);
		this.idleAnimation = ScaredyShroomAnimation.idle.bake(root);
		this.actionAnimation = ScaredyShroomAnimation.action.bake(root);
		this.sleepAnimation = ScaredyShroomAnimation.sleep.bake(root);

		this.hideAnimation = ScaredyShroomAnimation.hide.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 15).addBox(-2.0F, -10.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(24, 22).addBox(-1.0F, -7.0F, -3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition barrel = head.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(16, 22).addBox(-1.5F, -1.5F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -3.0F));

		PartDefinition cap = body.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(17, 16).addBox(-2.0F, -7.0F, 4.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.body.yRot = state.yRot * (float) (Math.PI / 180.0);
		this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
		this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
		this.sleepAnimation.apply(state.getSleepAnimationState(), state.ageInTicks);

		this.hideAnimation.apply(state.getSpecialAnimation(), state.ageInTicks);
		if (state.getSpecialAnimation().isStarted()) {
			this.body.yRot += (float)(Math.cos((double)((float) Mth.floor(state.ageInTicks) * 3.25F)) * Math.PI * (double)0.015F);
		}
	}
}