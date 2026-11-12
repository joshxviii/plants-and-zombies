package joshxviii.plantz.mixin;

import joshxviii.plantz.PazComponents;
import joshxviii.plantz.PazWorldGen;
import joshxviii.plantz.item.RollerSkatesItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method = "walkingStepSound", at = @At(value = "HEAD"), cancellable = true)
    private void plantz$rollerSkateStep(BlockPos onPos, BlockState onState, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity && RollerSkatesItem.Companion.isSkating(livingEntity)) {
            ci.cancel();
        }
    }

    @Inject(method = "deflection", at = @At("HEAD"), cancellable = true)
    private void plantz$deflectProjectilesWhileBlocking(
            Projectile projectile,
            CallbackInfoReturnable<ProjectileDeflection> cir
    ) {
        if (!((Object) this instanceof LivingEntity entity)) return;

        var blockingItem = entity.getItemBlockingWith();
        var wornBlocker = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (!wornBlocker.has(PazComponents.WORN_BODY_BLOCKER)) wornBlocker = null;
        if (blockingItem == null) blockingItem = wornBlocker;
        if (blockingItem == null) return;

        BlocksAttacks blocksAttacks = blockingItem.get(DataComponents.BLOCKS_ATTACKS);
        if (blocksAttacks == null) return;

        Vec3 directionToProjectile = projectile.position().subtract(entity.position());
        directionToProjectile = new Vec3(directionToProjectile.x, 0.0, directionToProjectile.z).normalize();
        Vec3 lookDirection = entity.calculateViewVector(0.0F, entity.getYHeadRot());
        lookDirection = new Vec3(lookDirection.x, 0.0, lookDirection.z).normalize();
        double angle = Math.acos(Math.max(-1.0, Math.min(1.0, directionToProjectile.dot(lookDirection))));

        final double finalAngle = angle;
        boolean canBlockFromDirection = blocksAttacks.damageReductions().stream().anyMatch(reduction ->
                (reduction.base() > 0.0F || reduction.factor() > 0.0F)
                        && finalAngle <= Math.toRadians(reduction.horizontalBlockingAngle())
        );
        if (!canBlockFromDirection && wornBlocker != null && wornBlocker != blockingItem) {
            BlocksAttacks wornAttacks = wornBlocker.get(DataComponents.BLOCKS_ATTACKS);
            if (wornAttacks != null) {
                blockingItem = wornBlocker;
                blocksAttacks = wornAttacks;
                canBlockFromDirection = wornAttacks.damageReductions().stream().anyMatch(reduction ->
                        (reduction.base() > 0.0F || reduction.factor() > 0.0F)
                                && finalAngle <= Math.toRadians(reduction.horizontalBlockingAngle())
                );
            }
        }
        if (canBlockFromDirection) {
            if (entity.level() instanceof ServerLevel serverLevel) {
                blocksAttacks.blockSound().ifPresent(sound ->
                        serverLevel.playSound(
                                null,
                                entity.getX(),
                                entity.getY(),
                                entity.getZ(),
                                sound.value(),
                                entity.getSoundSource()
                        )
                );
                blocksAttacks.hurtBlockingItem(
                        serverLevel,
                        blockingItem,
                        entity,
                        entity.getUsedItemHand(),
                        1.0F
                );
            }
            cir.setReturnValue(ProjectileDeflection.REVERSE);
        }
    }
}
