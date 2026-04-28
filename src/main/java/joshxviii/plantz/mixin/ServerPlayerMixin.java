package joshxviii.plantz.mixin;

import joshxviii.plantz.PlantHeadAttachment;
import joshxviii.plantz.entity.plant.Plant;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
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
            Optional<ValueInput> rootTag = playerInput.child("plantz:AttachedPlant");
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

    //TODO probably should move to a EntityMixin instead
    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;", at = @At(value = "RETURN"))
    public void teleportAttachedPlant(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        if ( ((PlantHeadAttachment) this).plantz$getPlant() instanceof Plant plantAttachment ) {
                plantAttachment.detachFromEntity();
                plantAttachment.teleport(
                    new TeleportTransition(
                            transition.newLevel(),
                            plantAttachment.position(),
                            plantAttachment.getDeltaMovement(),
                            plantAttachment.yRotO,
                            plantAttachment.xRotO,
                            TeleportTransition.DO_NOTHING.then( entity -> {
                                if (entity instanceof Plant plantEntity) {
                                    plantEntity.setYRot(plantAttachment.yRotO);
                                    plantEntity.setXRot(plantAttachment.xRotO);
                                    plantEntity.attachToEntity( (ServerPlayer) (Object) this);
                                }
                            })
                    )
            );
        }
    }
}
