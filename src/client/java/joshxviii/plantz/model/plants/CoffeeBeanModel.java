package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.plants.ChomperAnimation;
import joshxviii.plantz.animation.plants.CoffeeBeanAnimation;
import joshxviii.plantz.animation.plants.FirePeaAnimation;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import static joshxviii.plantz.UtilsKt.pazResource;


public class CoffeeBeanModel extends PlantModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("coffeebean"), "main");
	private final ModelPart body;
	private final ModelPart head;

	public CoffeeBeanModel(ModelPart root) {
		super(
			CoffeeBeanAnimation.init.bake(root),
			CoffeeBeanAnimation.idle.bake(root),
			CoffeeBeanAnimation.action.bake(root),
			CoffeeBeanAnimation.sleep.bake(root),
			null,
			root
		);
        this.body = root.getChild("body");
		this.head = this.body.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 10).addBox(-2.5F, -9.0F, -2.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-2.5F, -4.0F, -3.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 0.5F, -0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(@NotNull PlantRenderState state) {
		this.body.zRot = Mth.sin(state.ageInTicks * 1.5f)*0.01f;
		super.setupAnim(state);
		this.body.yRot = state.yRot * (float) (Mth.PI / 180.0);
	}
}