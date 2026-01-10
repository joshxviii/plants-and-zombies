package joshxviii.plantz.model;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.FumeShroomAnimation;
import joshxviii.plantz.animation.PuffShroomAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;


public class FumeShroomModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("fumeshroom"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart eyebrows;
	private final ModelPart eyebrow_L;
	private final ModelPart eyebrow_R;
	private final ModelPart cap;
	private final ModelPart spout;
	private final ModelPart barrel;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation actionAnimation;
	private final KeyframeAnimation initAnimation;

	public FumeShroomModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.eyebrows = this.head.getChild("eyebrows");
		this.eyebrow_L = this.eyebrows.getChild("eyebrow_L");
		this.eyebrow_R = this.eyebrows.getChild("eyebrow_R");
		this.cap = this.head.getChild("cap");
		this.spout = this.cap.getChild("spout");
		this.barrel = this.spout.getChild("barrel");
		this.initAnimation = FumeShroomAnimation.init.bake(root);
		this.idleAnimation = FumeShroomAnimation.idle.bake(root);
		this.actionAnimation = FumeShroomAnimation.action.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 23).addBox(-5.0F, -7.0F, -5.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition eyebrows = head.addOrReplaceChild("eyebrows", CubeListBuilder.create(), PartPose.offset(0.0F, -4.5F, -5.1F));

		PartDefinition eyebrow_L = eyebrows.addOrReplaceChild("eyebrow_L", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 0.0F));

		PartDefinition eyebrow_R = eyebrows.addOrReplaceChild("eyebrow_R", CubeListBuilder.create().texOffs(2, 2).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 0.0F, 0.0F));

		PartDefinition cap = head.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -8.75F, -7.0F, 14.0F, 9.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.25F, 0.0F));

		PartDefinition spout = cap.addOrReplaceChild("spout", CubeListBuilder.create().texOffs(0, 46).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.75F, -7.0F));

		PartDefinition barrel = spout.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(0, 40).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -2.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}


	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.body.yRot = state.yRot * (float) (Math.PI / 180.0);
		this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
		this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
	}
}