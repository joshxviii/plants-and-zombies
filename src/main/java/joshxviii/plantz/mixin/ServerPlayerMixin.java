package joshxviii.plantz.mixin;

import joshxviii.plantz.PlantHeadAttachment;
import joshxviii.plantz.entity.plant.Plant;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Josh
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Shadow
    public abstract ServerLevel level();

    @Inject(method = "loadAndSpawnParentVehicle", at = @At(value = "HEAD"))
    public void respawnAttachedPlant(ValueInput playerInput, CallbackInfo ci) {
        if ( !((PlantHeadAttachment) this).plantz$getPlantData().isEmpty() ) {
            Optional<ValueInput> rootTag = playerInput.child("plantz:PlantEntityOnHead");
            if (rootTag.isPresent()) {
                ServerLevel serverLevel = this.level();
                Entity entity = EntityType.loadEntityRecursive(
                        rootTag.get(), serverLevel, EntitySpawnReason.LOAD, e -> !serverLevel.addWithUUID(e) ? null : e
                );
                if (entity instanceof Plant plantAttachment) {
                    plantAttachment.attachToEntity((ServerPlayer) (Object) this);
                }
            }
        }
    }

    //TODO probably add to LivingEntityMixin instead
    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;", at = @At(value = "TAIL"))
    public void teleportAttachedPlant(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {

    }
}
