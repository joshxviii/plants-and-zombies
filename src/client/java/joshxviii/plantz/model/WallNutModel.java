package joshxviii.plantz.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.entity.WallNut;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;
import static joshxviii.plantz.UtilsKt.pazResource;

public class WallNutModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("wallnut"), "main");
	private final ModelPart body;
	private final ModelPart inner;

	public WallNutModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.inner = this.body.getChild("inner");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -21.0F, -8.0F, 16.0F, 19.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(56, 35).addBox(-7.0F, -24.0F, -7.0F, 14.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(56, 52).addBox(-7.0F, -2.0F, -7.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition inner = body.addOrReplaceChild("inner", CubeListBuilder.create().texOffs(0, 35).addBox(-7.0F, -21.0F, -7.0F, 14.0F, 18.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-6.0F, -23.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(64, 14).addBox(-6.0F, -3.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
	}
}