package joshxviii.plantz.model.plants;

import joshxviii.plantz.ai.PlantState;
import joshxviii.plantz.animation.plants.PlantAnimations;
import joshxviii.plantz.renderer.RenderingUtilsKt;
import joshxviii.plantz.renderer.entity.plant.PlantRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * @author Josh
 */
public class PlantModel extends EntityModel<@NotNull PlantRenderState> {

    KeyframeAnimation initAnimation;
    KeyframeAnimation idleAnimation;
    KeyframeAnimation actionAnimation;
    KeyframeAnimation sleepAnimation;
    KeyframeAnimation walkAnimation;
    public ModelPart locator_hat;
    private java.util.List<ModelPart> locatorHatChain;
    final KeyframeAnimation bounceAnimation;
    Function<String, ModelPart> partLookup = root.createPartLookup();

    protected PlantModel(ModelPart root) {
        super(root);
        bounceAnimation = PlantAnimations.bounce.bake(root);
        this.locator_hat = partLookup.apply("locator_hat");
        if (this.locator_hat != null) {
            java.util.List<ModelPart> chain = new java.util.ArrayList<>();
            for (ModelPart part : root.getAllParts().stream().toList()) {
                if (part != locator_hat && part.getAllParts().stream().anyMatch(c -> c == locator_hat)) chain.add(part);
            }
            chain.sort((a, b) -> Long.compare(b.getAllParts().size(), a.getAllParts().size()));
            chain.add(locator_hat);
            this.locatorHatChain = chain;
        }
    }

    /** Applies root-to-locator_hat transforms (incl. current animation) to the stack, or returns false if absent. */
    public boolean applyLocatorHat(com.mojang.blaze3d.vertex.PoseStack poseStack) {
        if (locatorHatChain == null) return false;
        for (ModelPart part : locatorHatChain) part.translateAndRotate(poseStack);
        return true;
    }


    public KeyframeAnimation getProcessedInit(PlantRenderState state) {
        return this.initAnimation;
    };

    public KeyframeAnimation getProcessedIdle(PlantRenderState state) {
        return this.idleAnimation;
    };

    public KeyframeAnimation getProcessedSleep(PlantRenderState state) {
        return this.sleepAnimation;
    };

    public KeyframeAnimation getProcessedAction(PlantRenderState state) {
        return this.actionAnimation;
    }

    @Override
    public void setupAnim(@NotNull PlantRenderState state) {
        super.setupAnim(state);
        if (initAnimation!=null) getProcessedInit(state).apply(state.getInitAnimationState(), state.ageInTicks);
        if (idleAnimation!=null) getProcessedIdle(state).apply(state.getIdleAnimationState(), state.ageInTicks);
        if (actionAnimation!=null && state.getPlantState() != PlantState.IDLE) getProcessedAction(state).apply(state.getActionAnimationState(), state.ageInTicks);
        if (sleepAnimation!=null) getProcessedSleep(state).apply(state.getSleepAnimationState(), state.ageInTicks);
        this.bounceAnimation.apply(state.getBounceAnimationState(), state.ageInTicks);

        if (walkAnimation!=null) walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 4f, 4f);
    }
}
