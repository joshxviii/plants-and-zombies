package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.HypnoShroomAnimation;
import joshxviii.plantz.animation.PuffShroomAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;


public class HypnoShroomModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("hypnoshroom"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart cap;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation actionAnimation;
	private final KeyframeAnimation initAnimation;
	private final KeyframeAnimation sleepAnimation;

	public HypnoShroomModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.cap = this.body.getChild("cap");
		this.initAnimation = HypnoShroomAnimation.init.bake(root);
		this.idleAnimation = HypnoShroomAnimation.idle.bake(root);
		this.actionAnimation = HypnoShroomAnimation.action.bake(root);
		this.sleepAnimation = HypnoShroomAnimation.sleep.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cap = body.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(32, 20).addBox(-4.0F, -13.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.body.yRot = state.yRot * (float) (Math.PI / 180.0);
		this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
		this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
		this.sleepAnimation.apply(state.getSleepAnimationState(), state.ageInTicks);
	}
}