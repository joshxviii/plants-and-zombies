package joshxviii.plantz.model.zombies;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class ZombieYetiModel extends ZombieModel<@NotNull ZombieRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("zombie_yeti"), "main");
	public ZombieYetiModel(final ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(64, 18).addBox(-0.5F, -2.5F, -3.0F, 6.0F, 17.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(20, 16).addBox(0.0F, -2.0F, -2.5F, 5.0F, 16.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -1.25F, 0.5F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 18).addBox(-5.5F, -2.5F, -3.0F, 6.0F, 17.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 16).addBox(-5.0F, -2.0F, -2.5F, 5.0F, 16.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -1.25F, 0.5F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(42, 56).addBox(-6.5F, -9.5F, -5.5F, 13.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(38, 41).addBox(-6.5F, -16.5F, -3.5F, 13.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 51).addBox(-6.0F, -9.0F, -5.0F, 12.0F, 9.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(0, 37).addBox(-6.0F, -16.0F, -3.0F, 12.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 0.0F));

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -6.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(35, 0).addBox(-4.5F, -7.5F, -6.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -7.5F, -2.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(64, 76).addBox(-3.0F, -0.5F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 69).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 13.0F, -0.5F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(40, 76).addBox(-3.0F, -0.5F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(20, 69).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 13.0F, -0.5F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull ZombieRenderState state) {
		super.setupAnim(state);
	}
}