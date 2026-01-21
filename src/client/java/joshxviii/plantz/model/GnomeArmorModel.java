package joshxviii.plantz.model;

import joshxviii.plantz.GnomeRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class GnomeArmorModel<T extends GnomeRenderState> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("gnome_armor"), "main");
	private ModelPart helmet = null;
	private ModelPart chest = null;
	private ModelPart chest_torso = null;
	private ModelPart chest_arms = null;
	private ModelPart chest_arm_L = null;
	private ModelPart chest_arm_R = null;
	private ModelPart leggings = null;
	private ModelPart leggings_legs = null;
	private ModelPart leggings_leg_L2 = null;
	private ModelPart leggings_leg_R2 = null;
	private ModelPart boots = null;
	private ModelPart boot_R = null;
	private ModelPart boot_L = null;

	//This is so buns, but it works...
	public GnomeArmorModel(ModelPart root) {
		super(root);
		try {this.helmet = root.getChild("helmet"); } catch (Exception ignored) {}
		try {this.chest = root.getChild("chest"); } catch (Exception ignored) {}
		try {this.chest_torso = this.chest.getChild("chest_torso"); } catch (Exception ignored) {}
		try {this.chest_arms = this.chest_torso.getChild("chest_arms"); } catch (Exception ignored) {}
		try {this.chest_arm_L = this.chest_arms.getChild("chest_arm_L"); } catch (Exception ignored) {}
		try {this.chest_arm_R = this.chest_arms.getChild("chest_arm_R"); } catch (Exception ignored) {}
		try {this.leggings = root.getChild("leggings"); } catch (Exception ignored) {}
		try {this.leggings_legs = this.leggings.getChild("leggings_legs"); } catch (Exception ignored) {}
		try {this.leggings_leg_L2 = this.leggings_legs.getChild("leggings_leg_L2"); } catch (Exception ignored) {}
		try {this.leggings_leg_R2 = this.leggings_legs.getChild("leggings_leg_R2"); } catch (Exception ignored) {}
		try {this.boots = root.getChild("boots"); } catch (Exception ignored) {}
		try {this.boot_R = this.boots.getChild("boot_R"); } catch (Exception ignored) {}
		try {this.boot_L = this.boots.getChild("boot_L"); } catch (Exception ignored) {}
	}

	public static LayerDefinition createHeadLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition helmet = partdefinition.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(7, 3).addBox(-2.5F, -4.75F, -2.5F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(3, 3).addBox(-0.5F, -4.75F, -2.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(20, 8).addBox(-2.5F, -3.75F, 1.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(5, 6).addBox(-2.5F, -3.75F, -1.5F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(11, 6).addBox(1.5F, -3.75F, -1.5F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	public static LayerDefinition createChestLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition chest_torso = chest.addOrReplaceChild("chest_torso", CubeListBuilder.create().texOffs(22, 22).addBox(-2.5F, -4.0F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition chest_arms = chest_torso.addOrReplaceChild("chest_arms", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition chest_arm_L = chest_arms.addOrReplaceChild("chest_arm_L", CubeListBuilder.create().texOffs(2, 26).mirror().addBox(-0.5F, -1.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offset(2.0F, -1.0F, 0.0F));

		PartDefinition chest_arm_R = chest_arms.addOrReplaceChild("chest_arm_R", CubeListBuilder.create().texOffs(2, 26).addBox(-2.5F, -1.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offset(-2.0F, -1.0F, 0.5F));


		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	public static LayerDefinition createLegsLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition leggings = partdefinition.addOrReplaceChild("leggings", CubeListBuilder.create().texOffs(19, 27).addBox(-2.5F, -4.0F, -1.5F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition leggings_legs = leggings.addOrReplaceChild("leggings_legs", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition leggings_leg_L2 = leggings_legs.addOrReplaceChild("leggings_leg_L2", CubeListBuilder.create().texOffs(2, 22).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 0.0F, 0.0F));

		PartDefinition leggings_leg_R2 = leggings_legs.addOrReplaceChild("leggings_leg_R2", CubeListBuilder.create().texOffs(2, 22).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-1.0F, 0.0F, 0.0F));


		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	public static LayerDefinition createBootsLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition boots = partdefinition.addOrReplaceChild("boots", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition boot_R = boots.addOrReplaceChild("boot_R", CubeListBuilder.create().texOffs(2, 26).addBox(-2.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition boot_L = boots.addOrReplaceChild("boot_L", CubeListBuilder.create().texOffs(2, 26).mirror().addBox(-2.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(@NotNull GnomeRenderState state) {
		if (helmet!=null) {
			helmet.xRot = state.xRot * (float) (Math.PI / 180.0);
			helmet.yRot = state.yRot * (float) (Math.PI / 180.0);
		}
		float animationPos = state.walkAnimationPos;
		float animationSpeed = state.walkAnimationSpeed;
		if (chest_arm_L!=null) chest_arm_L.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
		if (chest_arm_R!=null) chest_arm_R.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
	}
}