package joshxviii.plantz.model.zombies;

import joshxviii.plantz.animation.zombies.AllStarAnimation;
import joshxviii.plantz.renderer.RenderingUtilsKt;
import joshxviii.plantz.renderer.entity.zombie.PazZombieRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class AllStarModel<S extends PazZombieRenderState> extends PazZombieModel<S> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("all_star"), "main");

    private final KeyframeAnimation actionAnimation;
    private final KeyframeAnimation walkAnimation;
    private final ModelPart helmet;

    public AllStarModel(final ModelPart root) {
        super(null, root);
        this.actionAnimation = AllStarAnimation.action.bake(root);
        this.walkAnimation = AllStarAnimation.walk.bake(root);
        this.helmet = RenderingUtilsKt.getChildOrNull(this.getHead(), "helmet");
    }

    @Override
    public <T extends PazZombieRenderState> PazZombieModel<T> forArmor(ModelPart root) {
        return new AllStarModel<>(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 4.5417F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -3.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5417F, -0.5F));

        PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(48, 48).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.75F))
                .texOffs(48, 64).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
                .texOffs(48, 39).addBox(5.75F, -7.35F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 39).addBox(4.75F, -4.35F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(49, 39).mirror().addBox(-6.75F, -7.35F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(49, 39).mirror().addBox(-5.75F, -4.35F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -4.0F, 0.5F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.5F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5417F, 0.0F));

        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 7.4583F, 0.0F));

        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 7.4583F, 0.0F));

        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 34).addBox(0.0F, -1.625F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 16).addBox(-0.1F, -1.875F, -2.5F, 7.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -2.9167F, 0.0F));

        PartDefinition cube_r1 = left_arm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 46).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -2.375F, -1.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r2 = left_arm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(48, 45).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -2.375F, -1.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r3 = left_arm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 45).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -2.375F, 1.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r4 = left_arm.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(48, 45).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -2.375F, 1.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r5 = left_arm.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(48, 46).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -2.375F, 1.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r6 = left_arm.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(48, 46).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -2.375F, -1.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 34).addBox(-4.0F, -1.625F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 25).addBox(-6.9F, -1.875F, -2.5F, 7.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -2.9167F, 0.0F));

        return LayerDefinition.create(meshdefinition, 80, 80);
    }

    @Override
    public void setupAnim(@NotNull S state) {
        super.setupAnim(state);
        //resetPose();
        if (this.helmet != null) this.helmet.visible = (state.headItem.isEmpty() && state.headEquipment.isEmpty());

        //AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, false, state);
        //walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1.5f, 2f);
        actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
    }
}
