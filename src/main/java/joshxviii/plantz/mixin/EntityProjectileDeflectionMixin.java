package joshxviii.plantz.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityProjectileDeflectionMixin {
    @Inject(method = "deflection", at = @At("HEAD"), cancellable = true)
    private void plantz$deflectProjectilesWhileBlocking(
        Projectile projectile,
        CallbackInfoReturnable<ProjectileDeflection> cir
    ) {
        if (!((Object) this instanceof LivingEntity entity)) return;

        var blockingItem = entity.getItemBlockingWith();
        if (blockingItem == null) return;

        BlocksAttacks blocksAttacks = blockingItem.get(DataComponents.BLOCKS_ATTACKS);
        if (blocksAttacks == null) return;

        Vec3 directionToProjectile = projectile.position().subtract(entity.position());
        directionToProjectile = new Vec3(directionToProjectile.x, 0.0, directionToProjectile.z).normalize();
        Vec3 lookDirection = entity.calculateViewVector(0.0F, entity.getYHeadRot());
        lookDirection = new Vec3(lookDirection.x, 0.0, lookDirection.z).normalize();
        double angle = Math.acos(Math.max(-1.0, Math.min(1.0, directionToProjectile.dot(lookDirection))));

        boolean canBlockFromDirection = blocksAttacks.damageReductions().stream().anyMatch(reduction ->
            (reduction.base() > 0.0F || reduction.factor() > 0.0F)
                && angle <= Math.toRadians(reduction.horizontalBlockingAngle())
        );
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
