package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.PotatoMineAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;
import static joshxviii.plantz.UtilsKt.pazResource;

public class PotatoMineModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("potatomine"), "main");
	private final ModelPart body;
	private final ModelPart tiny_dirt;
	private final ModelPart dirt;
	private final ModelPart head;
	private final ModelPart potato;
	private final ModelPart blinker;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation actionAnimation;
	private final KeyframeAnimation initAnimation;

	public PotatoMineModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.tiny_dirt = this.body.getChild("tiny_dirt");
		this.dirt = this.body.getChild("dirt");
		this.head = this.body.getChild("head");
		this.potato = this.head.getChild("potato");
		this.blinker = this.head.getChild("blinker");
		this.idleAnimation = PotatoMineAnimation.idle.bake(root);
		this.initAnimation = PotatoMineAnimation.init.bake(root);
		this.actionAnimation = PotatoMineAnimation.action.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition tiny_dirt = body.addOrReplaceChild("tiny_dirt", CubeListBuilder.create().texOffs(36, 6).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.5F, 0.0F));

		PartDefinition tiny_dirt_r1 = tiny_dirt.addOrReplaceChild("tiny_dirt_r1", CubeListBuilder.create().texOffs(36, 6).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition dirt = body.addOrReplaceChild("dirt", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition potato = head.addOrReplaceChild("potato", CubeListBuilder.create().texOffs(0, 14).addBox(-5.0F, -7.0F, -5.0F, 10.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition blinker = head.addOrReplaceChild("blinker", CubeListBuilder.create().texOffs(16, 30).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 30).addBox(-2.0F, -8.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
		this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
	}
}