package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.plants.DoomShroomAnimation;
import joshxviii.plantz.animation.plants.HypnoShroomAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;


public class DoomShroomModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("doomshroom"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart cap;
	private final ModelPart eyes;

	public DoomShroomModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.cap = this.head.getChild("cap");
		this.eyes = this.cap.getChild("eyes");
		this.initAnimation = DoomShroomAnimation.init.bake(root);
		this.idleAnimation = DoomShroomAnimation.idle.bake(root);
		this.actionAnimation = DoomShroomAnimation.action.bake(root);
		this.sleepAnimation = DoomShroomAnimation.sleep.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 21).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cap = head.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -7.0F, -7.0F, 14.0F, 7.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition eyes = cap.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(32, 29).addBox(-4.0F, -3.0F, -7.025F, 8.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.body.yRot = state.yRot * (float) (Math.PI / 180.0);
	}
}