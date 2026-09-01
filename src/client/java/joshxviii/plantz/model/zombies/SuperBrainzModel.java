package joshxviii.plantz.model.zombies;

import joshxviii.plantz.ai.ZombieState;
import joshxviii.plantz.animation.zombies.SuperBrainzAnimation;
import joshxviii.plantz.renderer.entity.PazZombieRenderState;
import joshxviii.plantz.renderer.entity.SuperBrainzRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static joshxviii.plantz.UtilsKt.pazResource;

public class SuperBrainzModel extends PazZombieModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("super_brainz"), "main");
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation flyAnimation;
    private final KeyframeAnimation laserAttackAnimation;
    private final KeyframeAnimation rightPunchAnimation;
    private final KeyframeAnimation leftPunchAnimation;
    ModelPart cape;

    public SuperBrainzModel(final ModelPart root) {
        super(null, root);
        cape = root.getChild("root").getChild("body").getChild("cape");
        this.walkAnimation = SuperBrainzAnimation.walk.bake(root.getChild("root"));
        this.flyAnimation = SuperBrainzAnimation.fly.bake(root.getChild("root"));
        this.laserAttackAnimation = SuperBrainzAnimation.action.bake(root.getChild("root"));
        this.rightPunchAnimation = SuperBrainzAnimation.right_punch.bake(root.getChild("root"));
        this.leftPunchAnimation = SuperBrainzAnimation.left_punch.bake(root.getChild("root"));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 44).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 61).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -17.0F, -1.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(66, 79).addBox(-3.0F, -2.0F, -1.0F, 6.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.75F, -4.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 29.0F, 1.0F));

        PartDefinition electro_brainz = head.addOrReplaceChild("electro_brainz", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition cube_r2 = electro_brainz.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(112, 44).addBox(-8.0F, -3.5F, 0.0F, 8.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.5F, -1.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r3 = electro_brainz.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(112, 35).addBox(0.0F, -3.5F, 0.0F, 8.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -1.5F, -1.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition toxic_brainz = head.addOrReplaceChild("toxic_brainz", CubeListBuilder.create().texOffs(88, 11).addBox(-5.0F, 3.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(88, 23).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(56, 12).addBox(-5.0F, -4.0F, -1.0F, 10.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(100, 0).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(88, 9).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(91, 13).addBox(-0.5F, -9.0F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(82, 4).addBox(-2.5F, -8.0F, -2.5F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(88, 9).addBox(6.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(91, 16).addBox(5.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(91, 16).addBox(-6.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(88, 9).addBox(-8.0F, -1.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(57, 25).addBox(-5.0F, 11.0F, -2.0F, 10.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.0F, 0.0F, -3.0F, 16.0F, 11.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(39, 1).addBox(-2.0F, 2.0F, -5.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.0F, 0.0F));

        PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(0, 77).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 17.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 4.0F));

        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(34, 49).addBox(0.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -15.0F, 0.0F));

        PartDefinition left_fist = left_arm.addOrReplaceChild("left_fist", CubeListBuilder.create().texOffs(34, 64).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(28, 27).addBox(-3.55F, -0.6F, -3.45F, 7.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 7.0F, 0.0F));

        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(58, 49).addBox(-6.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -15.0F, 0.0F));

        PartDefinition right_fist = right_arm.addOrReplaceChild("right_fist", CubeListBuilder.create().texOffs(58, 64).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 27).addBox(-3.55F, -0.6F, -3.45F, 7.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 7.0F, 0.0F));

        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(50, 79).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 0.0F, 0.0F));

        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(34, 79).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(54, 95).addBox(-2.0F, 9.0F, -4.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(34, 95).addBox(-2.5F, -0.5F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(@NotNull PazZombieRenderState state) {
        super.setupAnim(state);
        this.resetPose();
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, false, state);
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;

        if (!(state instanceof SuperBrainzRenderState superBrainzState)) return;
        float animationPos = state.walkAnimationPos;
        float animationSpeed = state.walkAnimationSpeed;
        if (superBrainzState.getZombieState() == ZombieState.FLYING)
            flyAnimation.applyWalk(animationPos, animationSpeed, 2f, 2f);
        else {
            this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
            walkAnimation.applyWalk(animationPos, animationSpeed, 2f, 2f);
        }

        laserAttackAnimation.apply(superBrainzState.getLaserAttackAnimationState(), state.ageInTicks);
        if (superBrainzState.getLaserAttackAnimationState().isStarted() && superBrainzState.getZombieState() != ZombieState.FLYING) {
            this.rightArm.getAllParts().forEach(ModelPart::resetPose);
            this.rightArm.y += 3;
            this.rightArm.z -= 3;
            this.rightArm.xRot = (state.yRot - 90) * Mth.DEG_TO_RAD;
            this.rightArm.yRot = (-state.xRot) * Mth.DEG_TO_RAD;
            this.rightArm.zRot = 90 * Mth.DEG_TO_RAD;
            this.rightArm.yRot += Mth.sin(state.ageInTicks * 1.5f)*0.07f;
        }
        if (superBrainzState.getRightPunchAnimationState().isStarted()) {
            this.rightArm.getAllParts().forEach(ModelPart::resetPose);
            AnimationUtils.animateZombieArms(this.rightArm, this.rightArm, false, state);
            rightPunchAnimation.apply(superBrainzState.getRightPunchAnimationState(), state.ageInTicks);
        }
        if (superBrainzState.getLeftPunchAnimationState().isStarted()) {
            this.leftArm.getAllParts().forEach(ModelPart::resetPose);
            AnimationUtils.animateZombieArms(this.leftArm, this.leftArm, false, state);
            leftPunchAnimation.apply(superBrainzState.getLeftPunchAnimationState(), state.ageInTicks);
        }


        cape.resetPose();
        cape.rotateBy(
                new Quaternionf()
                        .rotateY((float) -Math.PI)
                        .rotateX((6.0F + superBrainzState.getCapeLean() / 2.0F + superBrainzState.getCapeFlap()) * (float) -(Math.PI / 180.0))
                        .rotateZ(superBrainzState.getCapeLean2() / 2.0F * (float) -(Math.PI / 180.0))
                        .rotateY((180.0F - superBrainzState.getCapeLean2() / 2.0F) * (float) -(Math.PI / 180.0))
        );
    }
}
