package joshxviii.plantz.model.zombies;

import joshxviii.plantz.ai.ZombieState;
import joshxviii.plantz.animation.zombies.PazZombieAnimations;
import joshxviii.plantz.renderer.RenderingUtilsKt;
import joshxviii.plantz.renderer.entity.zombie.PazZombieRenderState;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static joshxviii.plantz.UtilsKt.pazResource;

public class PazZombieModel<S extends PazZombieRenderState> extends ZombieModel<S> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(pazResource("paz_zombie"), "main");

    final KeyframeAnimation emergeAnimation;
    final KeyframeAnimation retreatAnimation;
    final KeyframeAnimation floatAnimation;
    ModelPart propeller;

    public PazZombieModel(
            @Nullable AnimationDefinition initAnimationOverride,
            final ModelPart root
    ) {
        var mainRoot = root.hasChild("root") ? root.getChild("root") : root;
        super(mainRoot);

        this.propeller = RenderingUtilsKt.getChildOrNull(this.getHead(), "propeller");

        this.emergeAnimation = Objects.requireNonNullElse(initAnimationOverride, PazZombieAnimations.emerge).bake(mainRoot);
        this.retreatAnimation = PazZombieAnimations.retreat.bake(mainRoot);
        this.floatAnimation = PazZombieAnimations.balloon_float.bake(mainRoot);

    }

    /**
     * Any model that should override this must not have any animations that animate bones that aren't a part of the base PazZombieModel.
     * @param root
     * @return
     * @param <T>
     */
    public <T extends PazZombieRenderState> PazZombieModel<T> forArmor(ModelPart root) {
        return new PazZombieModel<>(null, root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition propeller = head.addOrReplaceChild("propeller", CubeListBuilder.create().texOffs(17, 1).addBox(-3.5F, -1.925F, -3.5375F, 7.0F, 0.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, -1).addBox(0.0F, -1.925F, -0.4625F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.075F, 3.9625F));

        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(final @NotNull S state) {
        if (state.getHeadOnly()) {
            this.head.visible = true;
            this.body.visible = false;
            this.hat.visible = false;
            this.leftArm.visible = false;
            this.rightArm.visible = false;
            this.leftLeg.visible = false;
            this.rightLeg.visible = false;
        }
        else {
            this.head.visible = true;
            this.body.visible = true;
            this.hat.visible = true;
            this.leftArm.visible = true;
            this.rightArm.visible = true;
            this.leftLeg.visible = true;
            this.rightLeg.visible = true;
        }
        super.setupAnim(state);
        if (this.propeller != null) this.propeller.visible = (state.headItem.isEmpty() && state.headEquipment.isEmpty());
        if (state.isFlying()) {
            var animationPos = state.walkAnimationPos;
            var animationSpeed = state.walkAnimationSpeed + 0.2f;
            floatAnimation.applyWalk(animationPos, animationSpeed, 1f, 2f);
            if (this.propeller != null) propeller.yRot = animationPos;
        }
        emergeAnimation.apply(state.getEmergeAnimationState(), state.ageInTicks);
        retreatAnimation.apply(state.getRetreatAnimationState(), state.ageInTicks);
    }
}
