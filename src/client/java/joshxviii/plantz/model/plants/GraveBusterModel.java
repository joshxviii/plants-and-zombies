package joshxviii.plantz.model.plants;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import joshxviii.plantz.animation.plants.GraveBusterAnimation;
import joshxviii.plantz.renderer.entity.plant.PlantRenderState;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class GraveBusterModel extends PlantModel{
	public static final float BODY_DROP_DISTANCE = 14.0F;
	public static final int BODY_DROP_DURATION = 37;
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("grave_buster"), "main");
	private final ModelPart body;
	private final float bodyStartY;
	private final ModelPart head;
	private final ModelPart leaves_4;
	private final ModelPart leaves_3;
	private final ModelPart leaves_2;
	private final ModelPart leaves_1;
	private final ModelPart teeth_1;
	private final ModelPart teeth_2;

	public GraveBusterModel(ModelPart root) {
		super(root);

		initAnimation = GraveBusterAnimation.init.bake(root);
		idleAnimation = GraveBusterAnimation.idle.bake(root);
		actionAnimation = GraveBusterAnimation.action.bake(root);
		sleepAnimation = GraveBusterAnimation.sleep.bake(root);

		this.body = root.getChild("body");
		this.bodyStartY = this.body.y;
		this.head = this.body.getChild("head");
		this.leaves_4 = this.head.getChild("leaves_4");
		this.leaves_3 = this.head.getChild("leaves_3");
		this.leaves_2 = this.head.getChild("leaves_2");
		this.leaves_1 = this.head.getChild("leaves_1");
		this.teeth_1 = this.head.getChild("teeth_1");
		this.teeth_2 = this.head.getChild("teeth_2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-7.5F, -10.0F, -4.0F, 15.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(0, 44).addBox(-8.0F, -10.0F, -4.5F, 16.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leaves_4 = head.addOrReplaceChild("leaves_4", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.5F, -10.0F, 0.5F, 0.0F, 0.0F, 0.3927F));

		PartDefinition cube_r1 = leaves_4.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(40, 19).addBox(0.0F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.182F, -3.182F, 0.5F, 0.0F, -0.7854F, 2.3562F));

		PartDefinition cube_r2 = leaves_4.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(26, 12).addBox(0.0F, -7.0F, -3.5F, 0.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition leaves_3 = head.addOrReplaceChild("leaves_3", CubeListBuilder.create(), PartPose.offsetAndRotation(5.5F, -10.0F, 0.5F, 0.0F, 0.0F, -0.3927F));

		PartDefinition cube_r3 = leaves_3.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 19).addBox(0.0F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8891F, -3.8891F, -1.5F, 0.0F, 0.7854F, 0.7854F));

		PartDefinition cube_r4 = leaves_3.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(26, 12).addBox(0.0F, -7.0F, -3.5F, 0.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition leaves_2 = head.addOrReplaceChild("leaves_2", CubeListBuilder.create(), PartPose.offset(-0.4212F, -9.9855F, 4.0234F));

		PartDefinition cube_r5 = leaves_2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(40, 19).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6744F, -4.8827F, -4.1041F, -1.9635F, -0.7854F, -1.5708F));

		PartDefinition cube_r6 = leaves_2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 19).mirror().addBox(-6.5F, -8.0F, 0.0F, 13.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.4212F, -0.0145F, -0.0234F, 0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r7 = leaves_2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(40, 19).addBox(0.0F, -1.6F, 1.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8996F, -1.0036F, -2.0234F, 0.9553F, -0.5236F, -0.6155F));

		PartDefinition leaves_1 = head.addOrReplaceChild("leaves_1", CubeListBuilder.create(), PartPose.offset(0.0F, -10.0F, -3.0F));

		PartDefinition cube_r8 = leaves_1.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 19).addBox(-6.5F, -8.0F, 0.0F, 13.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r9 = leaves_1.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(40, 19).addBox(0.0F, -1.6F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4784F, -0.9891F, 1.0F, -0.9553F, 0.5236F, -0.6155F));

		PartDefinition teeth_1 = head.addOrReplaceChild("teeth_1", CubeListBuilder.create().texOffs(32, 33).addBox(-7.475F, 0.0F, -3.18F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(48, 6).addBox(-1.5F, 0.0F, -3.18F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(44, 29).addBox(4.475F, 0.0F, -3.18F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(32, 26).addBox(5.475F, 0.0F, 1.77F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(40, 22).addBox(-7.475F, 0.0F, 1.77F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -0.77F));

		PartDefinition teeth_2 = head.addOrReplaceChild("teeth_2", CubeListBuilder.create().texOffs(48, 0).addBox(-4.5F, 0.0F, -2.225F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(48, 12).addBox(1.5F, 0.0F, -2.225F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(22, 26).addBox(5.475F, 0.0F, -0.275F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(22, 33).addBox(-7.475F, 0.0F, -0.275F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.725F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		if (state.getCooldown() > -1) {
			float progress = Mth.lerp(
				state.getPartialTick(),
				Mth.clamp((BODY_DROP_DURATION - state.getCooldown()) / (float) BODY_DROP_DURATION, 0.0F, 1.0F),
				Mth.clamp((BODY_DROP_DURATION - state.getCooldown()) / (float) BODY_DROP_DURATION, 0.0F, 1.0F)
			);
			body.y = bodyStartY + progress * BODY_DROP_DISTANCE;
			body.xRot = 0f;
			body.yRot = 0f;
		} else {
			body.y = bodyStartY;
			body.xRot = state.xRot * (float) (Math.PI / 180.0) * 0.25f;
			body.yRot = state.yRot * (float) (Math.PI / 180.0);
		}
	}
}
