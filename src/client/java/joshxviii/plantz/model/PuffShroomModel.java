package joshxviii.plantz.model;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.CherryBombAnimation;
import joshxviii.plantz.animation.IcePeaAnimation;
import joshxviii.plantz.animation.PuffShroomAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.CopperGolemAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;
import static joshxviii.plantz.UtilsKt.pazResource;


public class PuffShroomModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("puffshroom"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart barrel;
	private final ModelPart cap;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation actionAnimation;
	private final KeyframeAnimation initAnimation;

	public PuffShroomModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.barrel = this.head.getChild("barrel");
		this.cap = this.head.getChild("cap");
		this.initAnimation = PuffShroomAnimation.init.bake(root);
		this.idleAnimation = PuffShroomAnimation.idle.bake(root);
		this.actionAnimation = PuffShroomAnimation.action.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 25).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(24, 29).addBox(-1.0F, -3.0F, -4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition barrel = head.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(24, 25).addBox(-1.5F, -1.5F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.0F));

		PartDefinition cap = head.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.5F, 0.0F, -0.1309F, 0.0F, 0.0F));

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