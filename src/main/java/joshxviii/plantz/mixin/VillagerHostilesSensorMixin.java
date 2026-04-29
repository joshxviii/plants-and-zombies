package joshxviii.plantz.mixin;

import joshxviii.plantz.PazTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Josh
 */
@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {
    @Inject(method = "isMatchingEntity", at = @At(value = "HEAD"), cancellable = true)
    public void addPazZombies(ServerLevel level, LivingEntity body, LivingEntity mob, CallbackInfoReturnable<Boolean> cir) {
        if (mob.is(PazTags.EntityTypes.ZOMBIE_RAIDERS)) {
            if (mob.distanceToSqr(body) <= 64f) {
                cir.setReturnValue(true);
            }
        }
    }
}
