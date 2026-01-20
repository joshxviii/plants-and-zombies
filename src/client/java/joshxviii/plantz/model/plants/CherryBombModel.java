package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.CherryBombAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;
import static joshxviii.plantz.UtilsKt.pazResource;

public class CherryBombModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("cherrybomb"), "main");
	private final ModelPart body;
	private final ModelPart stem;
	private final ModelPart leaves;
	private final ModelPart leaf1;
	private final ModelPart leaf1_tip_2;
	private final ModelPart leaf2;
	private final ModelPart leaf2_tip_2;
	private final ModelPart head_1;
	private final ModelPart head_2;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation initAnimation;

	public CherryBombModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.stem = this.body.getChild("stem");
		this.leaves = this.stem.getChild("leaves");
		this.leaf1 = this.leaves.getChild("leaf1");
		this.leaf1_tip_2 = this.leaf1.getChild("leaf1_tip_2");
		this.leaf2 = this.leaves.getChild("leaf2");
		this.leaf2_tip_2 = this.leaf2.getChild("leaf2_tip_2");
		this.head_1 = this.stem.getChild("head_1");
		this.head_2 = this.stem.getChild("head_2");
		this.initAnimation = CherryBombAnimation.init.bake(root);
		this.idleAnimation = CherryBombAnimation.idle.bake(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, -1.0F));

		PartDefinition stem = body.addOrReplaceChild("stem", CubeListBuilder.create().texOffs(32, 0).addBox(-6.0F, -0.125F, -0.25F, 12.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.875F, 1.25F));

		PartDefinition leaves = stem.addOrReplaceChild("leaves", CubeListBuilder.create(), PartPose.offset(1.0F, -0.125F, -0.25F));

		PartDefinition leaf1 = leaves.addOrReplaceChild("leaf1", CubeListBuilder.create().texOffs(32, 11).addBox(0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leaf1_tip_2 = leaf1.addOrReplaceChild("leaf1_tip_2", CubeListBuilder.create().texOffs(34, 17).addBox(0.0F, 0.0F, -3.0F, 3.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition leaf2 = leaves.addOrReplaceChild("leaf2", CubeListBuilder.create().texOffs(32, 11).addBox(0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition leaf2_tip_2 = leaf2.addOrReplaceChild("leaf2_tip_2", CubeListBuilder.create().texOffs(34, 17).addBox(0.0F, 0.0F, -3.0F, 3.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition head_1 = stem.addOrReplaceChild("head_1", CubeListBuilder.create().texOffs(0, 16).addBox(-3.5F, 0.0F, -4.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, 10.875F, 0.25F));

		PartDefinition head_2 = stem.addOrReplaceChild("head_2", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, 0.0F, -4.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 9.875F, -0.25F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
		this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
		this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
	}
}