package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.ai.PlantState;
import joshxviii.plantz.animation.plants.PlantAnimations;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Josh
 */
public class PlantModel extends EntityModel<@NotNull PlantRenderState> {

    final KeyframeAnimation cooldownAnimation;
    final KeyframeAnimation idleAnimation;
    final KeyframeAnimation actionAnimation;
    final KeyframeAnimation initAnimation;
    final KeyframeAnimation sleepAnimation;
    final KeyframeAnimation bounceAnimation;

    protected PlantModel(
            @Nullable KeyframeAnimation initAnimation,
            @Nullable KeyframeAnimation idleAnimation,
            @Nullable KeyframeAnimation actionAnimation,
            @Nullable KeyframeAnimation sleepAnimation,
            @Nullable KeyframeAnimation cooldownAnimation,
            ModelPart root
    ) {
        super(root);
        this.idleAnimation = idleAnimation;
        this.actionAnimation = actionAnimation;
        this.initAnimation = initAnimation;
        this.sleepAnimation = sleepAnimation;
        this.cooldownAnimation = cooldownAnimation;
        bounceAnimation = PlantAnimations.bounce.bake(root);
    }

    @Override
    public void setupAnim(@NotNull PlantRenderState state) {
        super.setupAnim(state);
        if (initAnimation!=null)     this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
        if (idleAnimation!=null && !state.getCoolDownAnimationState().isStarted())     this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
        if (actionAnimation!=null)   this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
        if (sleepAnimation!=null)    this.sleepAnimation.apply(state.getSleepAnimationState(), state.ageInTicks);
        if (cooldownAnimation!=null && !state.getInitAnimationState().isStarted()) this.cooldownAnimation.apply(state.getCoolDownAnimationState(), state.ageInTicks);
        if (bounceAnimation!=null)   this.bounceAnimation.apply(state.getBounceAnimationState(), state.ageInTicks);
    }
}
