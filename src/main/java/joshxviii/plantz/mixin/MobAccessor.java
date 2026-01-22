package joshxviii.plantz.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobAccessor {

    @Accessor("goalSelector")
    GoalSelector getGoalSelector();

    @Accessor("targetSelector")
    GoalSelector getTargetSelector();
}