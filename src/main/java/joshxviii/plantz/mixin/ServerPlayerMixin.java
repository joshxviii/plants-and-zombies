package joshxviii.plantz.mixin;

import joshxviii.plantz.PlantHeadAttachment;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Josh
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void plantz$restoreWornPlant(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        if (restoreAll) PlantHeadAttachment.transfer(oldPlayer, (ServerPlayer) (Object) this);
    }
}
