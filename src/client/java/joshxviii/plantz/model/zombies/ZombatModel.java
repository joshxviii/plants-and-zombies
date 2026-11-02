// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


package joshxviii.plantz.model.zombies;

import joshxviii.plantz.renderer.entity.zombie.ZombatRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.BatAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class ZombatModel<S extends ZombatRenderState> extends EntityModel<S> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("zombat"), "main");
	private final ModelPart head;
	private final ModelPart top_hat;
	private final ModelPart body;
	private final ModelPart feet;
	private final ModelPart left_wing;
	private final ModelPart left_wing_tip;
	private final ModelPart right_wing;
	private final ModelPart right_wing_tip;
	private final KeyframeAnimation flyingAnimation;
	private final KeyframeAnimation restingAnimation;

	public ZombatModel(final ModelPart root) {
		super(root);
		this.head = root.getChild("head");
		this.top_hat = this.head.getChild("top_hat");
		this.body = root.getChild("body");
		this.feet = this.body.getChild("feet");
		this.left_wing = this.body.getChild("left_wing");
		this.left_wing_tip = this.left_wing.getChild("left_wing_tip");
		this.right_wing = this.body.getChild("right_wing");
		this.right_wing_tip = this.right_wing.getChild("right_wing_tip");
		this.flyingAnimation = BatAnimation.BAT_FLYING.bake(root);
		this.restingAnimation = BatAnimation.BAT_RESTING.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 15).addBox(1.5F, -8.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(8, 15).addBox(-4.5F, -8.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 20).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, 0.0F));

		PartDefinition top_hat = head.addOrReplaceChild("top_hat", CubeListBuilder.create().texOffs(0, 45).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.5F))
				.texOffs(2, 56).addBox(-3.5F, 0.0F, -3.5F, 7.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.75F, 0.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, 0.0F));

		PartDefinition feet = body.addOrReplaceChild("feet", CubeListBuilder.create().texOffs(16, 16).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(12, 0).addBox(0.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 0.0F, 0.0F));

		PartDefinition left_wing_tip = left_wing.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(12, 7).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 0.0F, 0.0F));

		PartDefinition right_wing_tip = right_wing.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(16, 8).addBox(-6.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull S state) {
		super.setupAnim(state);
		this.flyingAnimation.applyWalk(state.ageInTicks, state.walkAnimationSpeed+0.5f, 2f, 2f);
		//this.restingAnimation.apply(state.restAnimationState, state.ageInTicks);
	}
}
