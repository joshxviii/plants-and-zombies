package joshxviii.plantz.model.plants;

import joshxviii.plantz.PlantRenderState;
import joshxviii.plantz.animation.plants.PlantAnimations;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

/**
 * @author Josh
 */
public class PlantModel extends EntityModel<@NotNull PlantRenderState> {

    KeyframeAnimation idleAnimation;
    KeyframeAnimation actionAnimation;
    KeyframeAnimation initAnimation;
    KeyframeAnimation sleepAnimation;
    KeyframeAnimation cooldownAnimation;
    KeyframeAnimation bounceAnimation;

    protected PlantModel(ModelPart root) {
        super(root);
        bounceAnimation = PlantAnimations.bounce.bake(root);
    }

    @Override
    public void setupAnim(@NotNull PlantRenderState state) {
        super.setupAnim(state);
        //this.initAnimation.apply(state.getInitAnimationState(), state.ageInTicks);
        //this.idleAnimation.apply(state.getIdleAnimationState(), state.ageInTicks);
        //if (actionAnimation!=null) this.actionAnimation.apply(state.getActionAnimationState(), state.ageInTicks);
        //this.sleepAnimation.apply(state.getSleepAnimationState(), state.ageInTicks);
        this.bounceAnimation.apply(state.getBounceAnimationState(), state.ageInTicks);
    }
}
