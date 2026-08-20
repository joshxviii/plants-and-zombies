package joshxviii.plantz.model.plants;

import joshxviii.plantz.animation.plants.SeaShroomAnimation;
import joshxviii.plantz.renderer.entity.PlantRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;


public class SeaShroomModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("seashroom"), "main");
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart barrel;
	private final ModelPart cap;
	private final ModelPart roots;
	private final ModelPart rootTR;
	private final ModelPart rootTL;
	private final ModelPart rootTBR;
	private final ModelPart rootBL;
	private final ModelPart rootML;
	private final ModelPart rootMR;
	private final KeyframeAnimation initLandAnimation;
	private final KeyframeAnimation idleLandAnimation;
	private final KeyframeAnimation sleepLandAnimation;

	public SeaShroomModel(ModelPart root) {
		super(
			SeaShroomAnimation.init.bake(root),
			SeaShroomAnimation.idle.bake(root),
			SeaShroomAnimation.action.bake(root),
			SeaShroomAnimation.sleep.bake(root),
			null,
			root
		);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.barrel = this.head.getChild("barrel");
		this.cap = this.head.getChild("cap");
		this.roots = this.body.getChild("roots");
		this.rootTR = this.roots.getChild("rootTR");
		this.rootTL = this.roots.getChild("rootTL");
		this.rootTBR = this.roots.getChild("rootTBR");
		this.rootBL = this.roots.getChild("rootBL");
		this.rootML = this.roots.getChild("rootML");
		this.rootMR = this.roots.getChild("rootMR");
		this.initLandAnimation = SeaShroomAnimation.init_land.bake(root);
		this.idleLandAnimation = SeaShroomAnimation.idle_land.bake(root);
		this.sleepLandAnimation = SeaShroomAnimation.sleep_land.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-3.0F, -5.9826F, -3.0133F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(24, 17).addBox(-1.0F, -2.4826F, -4.0133F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.0174F, 0.0133F));

		PartDefinition barrel = head.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(24, 13).addBox(-1.5F, -1.5F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.4826F, -4.0133F));

		PartDefinition cap = head.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(2, 1).addBox(-3.5F, -3.3108F, -3.1947F, 7.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.1946F, -0.0719F, -0.0873F, 0.0F, 0.0F));

		PartDefinition roots = body.addOrReplaceChild("roots", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition rootTR = roots.addOrReplaceChild("rootTR", CubeListBuilder.create().texOffs(26, 24).addBox(0.0F, 0.0F, -0.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, -1.5F));

		PartDefinition rootTL = roots.addOrReplaceChild("rootTL", CubeListBuilder.create().texOffs(25, 24).addBox(0.0F, 0.0F, -0.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, -1.5F));

		PartDefinition rootTBR = roots.addOrReplaceChild("rootTBR", CubeListBuilder.create().texOffs(26, 24).addBox(0.0F, 0.0F, -0.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 1.5F));

		PartDefinition rootBL = roots.addOrReplaceChild("rootBL", CubeListBuilder.create().texOffs(25, 24).addBox(0.0F, 0.0F, -0.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.0F, 1.5F));

		PartDefinition rootML = roots.addOrReplaceChild("rootML", CubeListBuilder.create().texOffs(25, 24).addBox(0.0F, 0.0F, -0.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.75F, 0.0F, 0.0F));

		PartDefinition rootMR = roots.addOrReplaceChild("rootMR", CubeListBuilder.create().texOffs(26, 24).addBox(0.0F, 0.0F, -0.5F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.75F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public KeyframeAnimation getProcessedInit(PlantRenderState state) {
		return state.isInWater ? super.getProcessedInit(state) :
			this.initLandAnimation;
	}

	@Override
	public KeyframeAnimation getProcessedIdle(PlantRenderState state) {
		return state.isInWater ? super.getProcessedIdle(state) :
			this.idleLandAnimation;
	}

	@Override
	public KeyframeAnimation getProcessedSleep(PlantRenderState state) {
		return state.isInWater ? super.getProcessedSleep(state) :
			this.sleepLandAnimation;
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
		this.body.yRot = state.yRot * (float) (Math.PI / 180.0);
	}
}