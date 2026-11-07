// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


package joshxviii.plantz.model.zombies;

import joshxviii.plantz.animation.zombies.ZombieGoatAnimation;
import joshxviii.plantz.renderer.entity.zombie.ZombatRenderState;
import joshxviii.plantz.renderer.entity.zombie.ZombieGoatRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.BatAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class ZombieGoatModel<S extends ZombieGoatRenderState> extends QuadrupedModel<S> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("zombie_goat"), "main");
	private final KeyframeAnimation walkAnimation;

	public ZombieGoatModel(final ModelPart root) {
		super(root);
		this.walkAnimation = ZombieGoatAnimation.walk.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(12, 16).addBox(0.0F, 2.0F, -5.0F, 0.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 30).addBox(-4.5F, -3.25F, -1.25F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(6, 30).addBox(2.5F, -3.25F, -1.25F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(28, 39).addBox(0.49F, -4.25F, -1.25F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(28, 7).addBox(0.49F, -7.25F, -1.25F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(36, 39).addBox(-2.49F, -4.25F, -1.25F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(32, 25).addBox(-2.49F, -7.25F, -1.25F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, -5.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(20, 15).addBox(-3.0F, -3.0F, -2.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(28, 0).addBox(-2.0F, -2.0F, -5.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -0.25F, -1.25F, 0.7854F, 0.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 5).addBox(0.0F, 5.0F, 0.0F, 0.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 33).addBox(-1.0F, 1.5F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, -5.0F));

		PartDefinition right_hind_leg = root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 25).addBox(0.5F, 2.0F, 0.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 17).addBox(0.5F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 19.0F, 4.0F));

		PartDefinition left_hind_leg = root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(8, 25).addBox(0.5F, 2.0F, 0.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(20, 25).addBox(0.5F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 19.0F, 4.0F));

		PartDefinition right_front_leg = root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(32, 32).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 19.0F, -4.0F));

		PartDefinition left_front_leg = root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(20, 33).addBox(-0.5F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 19.0F, -4.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull S state) {
		super.setupAnim(state);
		resetPose();
		float animationPos = state.walkAnimationPos;
		float animationSpeed = state.walkAnimationSpeed;
		walkAnimation.applyWalk(animationPos, animationSpeed, 3.0F, 3.0F);
	}
}
