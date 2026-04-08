package joshxviii.plantz.mixin;

import joshxviii.plantz.PazComponents;
import joshxviii.plantz.PazItems;
import joshxviii.plantz.PazSounds;
import joshxviii.plantz.item.component.BlocksProjectileDamage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(Projectile.class)
public class ProjectileMixin {

    @Inject(method = "onHit", at = @At(value = "HEAD"), cancellable = true)
    public void paz$onHit(final HitResult hitResult, CallbackInfo ci) {
        var type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            var entity = ((EntityHitResult) hitResult).getEntity();
            var projectile = (Projectile) (Object) this;
            var level = entity.level();
            if (entity instanceof LivingEntity livingEntity && level instanceof ServerLevel serverLevel) {
                for (EquipmentSlot slot : slots) {
                    ItemStack item = livingEntity.getItemBySlot(slot);
                    BlocksProjectileDamage component = item.getComponents().get(PazComponents.BLOCKS_PROJECTILE_DAMAGE);
                    if (component==null) continue;
                    if (component.getMustBeUsing() && !livingEntity.isUsingItem()) continue;

                    EquipmentSlotGroup validSlot = component.getSlot();
                    boolean matchesSlot = validSlot.test(slot);
                    if (!matchesSlot) continue;

                    float breakChance = component.getBreakChance();
                    if (entity.getRandom().nextFloat() < breakChance) {
                        item.shrink(1);
                        projectile.playSound(SoundEvents.ITEM_BREAK.value());
                    }
                    else {
                        SoundEvent hitSound;

                        if (item.is(PazItems.NEWSPAPER)) hitSound = PazSounds.PROJECTILE_HIT_PAPER;
                        else if (item.is(Items.BUCKET)) hitSound = PazSounds.PROJECTILE_HIT_BUCKET;
                        else hitSound = PazSounds.PROJECTILE_HIT_CONE;

                        projectile.playSound(hitSound);
                        ci.cancel();
                    }
                }
            }
        }
    }

    @Unique
    Set<EquipmentSlot> slots = Set.of(EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
}
