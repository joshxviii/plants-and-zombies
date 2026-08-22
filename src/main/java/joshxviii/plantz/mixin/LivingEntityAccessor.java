package joshxviii.plantz.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Invoker("plantz$getHypnoId")
    boolean plantz$getHypnoId();

    @Invoker("plantz$getChillId")
    boolean plantz$getChillId();

    @Invoker("plantz$getPaintedColors")
    Map<Integer, Integer> plantz$getPaintedColors();

}
