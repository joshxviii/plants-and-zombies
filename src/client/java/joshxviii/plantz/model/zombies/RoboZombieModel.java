package joshxviii.plantz.model.zombies;

import joshxviii.plantz.animation.zombies.RoboZombieAnimation;
import joshxviii.plantz.renderer.entity.GargantuarRenderState;
import joshxviii.plantz.renderer.entity.PazZombieRenderState;
import joshxviii.plantz.renderer.entity.RoboZombieRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static joshxviii.plantz.UtilsKt.pazResource;

public class RoboZombieModel extends PazZombieModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("robo_zombie"), "main");
    private final KeyframeAnimation bashAnimation;
    private final KeyframeAnimation shootAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation tankIdleAnimation;
    ModelPart treads;

    public RoboZombieModel(final ModelPart root) {
        super(null, root);
        treads = root.getChild("root").getChild("treads");
        this.bashAnimation = RoboZombieAnimation.attack.bake(root.getChild("root"));
        this.shootAnimation = RoboZombieAnimation.missile.bake(root.getChild("root"));
        this.walkAnimation = RoboZombieAnimation.walk.bake(root.getChild("root"));
        this.idleAnimation = RoboZombieAnimation.idle.bake(root.getChild("root"));
        this.tankIdleAnimation = RoboZombieAnimation.tank_idle.bake(root.getChild("root"));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(74, 75).addBox(-4.0F, -6.9167F, -6.125F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(60, 55).addBox(4.0F, -6.4167F, -3.125F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(62, 55).addBox(-5.0F, -6.4167F, -3.125F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(46, 94).addBox(-7.0F, -6.9167F, -4.125F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(92, 30).addBox(-4.0F, -0.9167F, -1.125F, 8.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(60, 53).addBox(-3.5F, -0.9167F, -5.875F, 7.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(60, 26).addBox(-4.0F, -7.9167F, -6.125F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F))
                .texOffs(95, 47).addBox(-5.5F, -5.05F, -7.75F, 11.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.0833F, -2.875F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(66, 83).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -5.9167F, -3.125F, 0.0F, -1.5708F, 0.0F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 27.0833F, -2.125F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(60, 54).addBox(-3.5F, -1.0F, -4.75F, 7.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(92, 23).addBox(-4.0F, 0.0F, -5.0F, 8.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(74, 42).addBox(-4.0F, -6.0F, -5.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -0.9167F, -1.125F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(34, 83).addBox(-5.0F, 11.0F, -2.0F, 10.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 56).addBox(-7.0F, 0.0F, -3.0F, 14.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -16.0F, -1.0F));

        PartDefinition rocket_pack = body.addOrReplaceChild("rocket_pack", CubeListBuilder.create().texOffs(60, 0).addBox(-3.0F, -9.3333F, 0.0F, 6.0F, 20.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(46, 56).addBox(-3.5F, -9.3333F, -0.5F, 7.0F, 20.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.3333F, 6.0F));

        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(84, 0).addBox(0.0F, -2.0F, -2.5F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -14.0F, -0.5F));

        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 88).addBox(-5.0F, -2.0F, -2.5F, 5.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, -14.0F, -0.5F));

        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(86, 89).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(32, 94).addBox(-2.5F, 9.0F, -4.5F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 0.0F, -0.5F));

        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(66, 89).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(92, 35).addBox(-2.5F, 9.0F, -4.5F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 0.0F, -0.5F));

        PartDefinition treads = root.addOrReplaceChild("treads", CubeListBuilder.create().texOffs(74, 58).addBox(-3.0F, 0.0F, -5.0F, 6.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 76).addBox(-6.0F, 3.0F, -3.0F, 12.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition tires = treads.addOrReplaceChild("tires", CubeListBuilder.create().texOffs(0, 0).addBox(2.0F, -1.5F, -10.5F, 9.0F, 7.0F, 21.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(-11.0F, -1.5F, -10.5F, 9.0F, 7.0F, 21.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.5F, -0.5F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(@NotNull PazZombieRenderState state) {
        super.setupAnim(state);
        this.resetPose();
        this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
        this.head.yRot = state.yRot * (float) (Math.PI / 180.0);

        if (!(state instanceof RoboZombieRenderState roboState)) return;
        float animationPos = state.walkAnimationPos;
        float animationSpeed = state.walkAnimationSpeed;
        walkAnimation.applyWalk(animationPos, animationSpeed, 2f, 2f);

        if (roboState.isTankTransformation()) {
            tankIdleAnimation.apply(roboState.getIdleAnimationState(), roboState.ageInTicks);
            var dir = state.getMovementDirection();
            if (dir.lengthSqr() > 0.005) {
                this.treads.yRot = -roboState.bodyRot * (float) (Math.PI / 180.0);
                this.treads.yRot += dir.rotation().y * Mth.DEG_TO_RAD;
            }
        }
        else idleAnimation.apply(roboState.getIdleAnimationState(), roboState.ageInTicks);
        shootAnimation.apply(roboState.getShootAnimationState(), roboState.ageInTicks);
        bashAnimation.apply(roboState.getBashAnimationState(), roboState.ageInTicks);
    }
}
