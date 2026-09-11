package joshxviii.plantz.model.plants;

import joshxviii.plantz.ai.PlantState;
import joshxviii.plantz.animation.plants.PlantAnimations;
import joshxviii.plantz.renderer.entity.plant.PlantRenderState;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Josh
 */
public class PlantModel extends EntityModel<@NotNull PlantRenderState> {

    KeyframeAnimation initAnimation;
    KeyframeAnimation idleAnimation;
    KeyframeAnimation actionAnimation;
    KeyframeAnimation sleepAnimation;
    KeyframeAnimation cooldownAnimation;
    KeyframeAnimation walkAnimation;
    final KeyframeAnimation bounceAnimation;

    protected PlantModel(ModelPart root) {
        super(root);
        bounceAnimation = PlantAnimations.bounce.bake(root);
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
        if (initAnimation!=null)     getProcessedInit(state).apply(state.getInitAnimationState(), state.ageInTicks);
        if (idleAnimation!=null && !state.getCoolDownAnimationState().isStarted()) getProcessedIdle(state).apply(state.getIdleAnimationState(), state.ageInTicks);
        if (actionAnimation!=null && state.getPlantState() != PlantState.IDLE) getProcessedAction(state).apply(state.getActionAnimationState(), state.ageInTicks);
        if (sleepAnimation!=null)    getProcessedSleep(state).apply(state.getSleepAnimationState(), state.ageInTicks);
        if (cooldownAnimation!=null && !state.getInitAnimationState().isStarted()) this.cooldownAnimation.apply(state.getCoolDownAnimationState(), state.ageInTicks);
        this.bounceAnimation.apply(state.getBounceAnimationState(), state.ageInTicks);

        if (walkAnimation!=null) walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 1f, 1f);
    }
}
