package joshxviii.plantz.mixin;

import joshxviii.plantz.PazWorldGen;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getGravity", at = @At("RETURN"), cancellable = true)
    private void plantz$gnomeSpaceGravity(CallbackInfoReturnable<Double> callback) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().dimension().equals(PazWorldGen.GNOME_SPACE)) {
            callback.setReturnValue(callback.getReturnValue() * 0.25);
        }
    }
}
