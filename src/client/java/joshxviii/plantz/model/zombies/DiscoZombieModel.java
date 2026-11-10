// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


package joshxviii.plantz.model.zombies;

import joshxviii.plantz.animation.zombies.DiscoZombieAnimation;
import joshxviii.plantz.renderer.RenderingUtilsKt;
import joshxviii.plantz.renderer.entity.zombie.PazZombieRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class DiscoZombieModel<S extends PazZombieRenderState> extends PazZombieModel<S> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("disco_zombie"), "main");
	private final ModelPart afro;
	private final KeyframeAnimation actionAnimation;

	public DiscoZombieModel(final ModelPart root) {
		super(
			DiscoZombieAnimation.init,
			root
		);
		this.afro = RenderingUtilsKt.getChildOrNull(this.getHead(), "afro");;
		this.actionAnimation = DiscoZombieAnimation.dance.bake(root);
	}

	@Override
	public <T extends PazZombieRenderState> PazZombieModel<T> forArmor(ModelPart root) {
		return new DiscoZombieModel<>(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition afro = head.addOrReplaceChild("afro", CubeListBuilder.create().texOffs(52, 66).addBox(-7.0F, -5.0F, -5.0F, 14.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 64).addBox(-7.5F, -5.5F, -5.5F, 15.0F, 11.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 2.5F));

		PartDefinition face = head.addOrReplaceChild("face", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition glasses = face.addOrReplaceChild("glasses", CubeListBuilder.create().texOffs(64, 8).addBox(-6.0F, -8.0F, -4.25F, 12.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(56, 16).addBox(0.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(4.0F, -2.0F, 0.0F));

		PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(48, 48).addBox(-3.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-4.0F, -2.0F, 0.0F));

		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 48).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(2.0F, 8.0F, 0.0F));

		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 32).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-2.0F, 8.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull S state) {
		super.setupAnim(state);
		if (afro != null) afro.visible = (state.headItem.isEmpty() && state.headEquipment.isEmpty());
		actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
	}
}
