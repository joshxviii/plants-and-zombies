package joshxviii.plantz.mixin;

import joshxviii.plantz.PlantHeadAttachment;
import joshxviii.plantz.entity.plant.Plant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Shadow
    protected abstract void tickPassenger(Entity vehicle, Entity entity);

    @Shadow
    @Final
    private EntityTickList entityTickList;

    @Shadow
    public abstract void tickNonPassenger(Entity entity);

    @Inject(method = "tickNonPassenger", at = @At(value = "HEAD"), cancellable = true)
    public void tickSyncPlant(Entity entity, CallbackInfo ci) {
        if (entity instanceof Plant) {
            var attachedEntity = ((Plant) entity).getAttachedEntity();
            if (attachedEntity!=null) {
                tickNonPassenger(attachedEntity);
            }
        }
    }

    @Inject(method = "tickNonPassenger", at = @At(value = "HEAD"), cancellable = true)
    public void tickCancel(Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity && ((PlantHeadAttachment) entity).plantz$hasPlantOnHead()) {
            ci.cancel();
        }
    }

}
