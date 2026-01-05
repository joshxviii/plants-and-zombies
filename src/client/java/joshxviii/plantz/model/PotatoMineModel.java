package joshxviii.plantz.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.entity.PotatoMine;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;
import static joshxviii.plantz.UtilsKt.pazResource;

public class PotatoMineModel extends EntityModel<@NotNull PlantRenderState> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("potatomine"), "main");
	private final ModelPart dirt;
	private final ModelPart body;

	public PotatoMineModel(ModelPart root) {
		super(root);
		this.dirt = root.getChild("dirt");
		this.body = this.dirt.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition dirt = partdefinition.addOrReplaceChild("dirt", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 9.5F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition body = dirt.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 18).addBox(-6.0F, -7.0F, -6.0F, 12.0F, 7.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(16, 37).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 37).addBox(-2.0F, -15.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		super.setupAnim(state);
	}
}