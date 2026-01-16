package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.WallNutAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;
import static joshxviii.plantz.UtilsKt.pazResource;

public class WallNutModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("wallnut"), "main");
	private final ModelPart body;
	private final ModelPart inner;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation initAnimation;
	private final KeyframeAnimation actionAnimation;

	public WallNutModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.inner = this.body.getChild("inner");
		this.initAnimation = WallNutAnimation.init.bake(root);
		this.idleAnimation = WallNutAnimation.idle.bake(root);
		this.actionAnimation = WallNutAnimation.action.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(3, 5).addBox(-7.0F, -15.0F, -7.0F, 14.0F, 13.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(59, 37).addBox(-6.0F, -18.0F, -6.0F, 12.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(59, 54).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition inner = body.addOrReplaceChild("inner", CubeListBuilder.create().texOffs(3, 37).addBox(-6.0F, -15.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(67, 2).addBox(-5.0F, -17.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(67, 16).addBox(-5.0F, -3.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		body.yRot = 0.0F;
		this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
		this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
	}
}