package joshxviii.plantz.model.zombies;

import joshxviii.plantz.PazEntities;
import joshxviii.plantz.PazZombieRenderState;
import joshxviii.plantz.animation.zombies.AllStarAnimation;
import joshxviii.plantz.animation.zombies.DiscoZombieAnimation;
import joshxviii.plantz.entity.zombie.AllStar;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class AllStarModel extends PazZombieModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("all_star"), "main");

    private final KeyframeAnimation actionAnimation;

    public AllStarModel(final ModelPart root) {
        super(null, root.getChild("root"));
        this.actionAnimation = AllStarAnimation.action.bake(root.getChild("root"));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 6.5417F, 0.9167F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -3.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.5417F, -0.4167F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.5F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5417F, 0.0833F));

        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 5.4583F, 0.0833F));

        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 5.4583F, 0.0833F));

        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 34).addBox(0.0F, -1.625F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 16).addBox(0.0F, -1.875F, -2.5F, 7.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -4.9167F, 0.0833F));

        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 34).addBox(-4.0F, -1.625F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 25).addBox(-7.0F, -1.875F, -2.5F, 7.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -4.9167F, 0.0833F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(@NotNull ZombieRenderState state) {
        super.setupAnim(state);
        PazZombieRenderState pazState = (PazZombieRenderState) state;
        actionAnimation.apply(pazState.getActionAnimationState(),  pazState.ageInTicks);
    }
}
