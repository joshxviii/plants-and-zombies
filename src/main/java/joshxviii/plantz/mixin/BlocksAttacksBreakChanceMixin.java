package joshxviii.plantz.mixin;

import joshxviii.plantz.PazComponents;
import joshxviii.plantz.item.component.BlockingItemBreakChance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlocksAttacks.class)
public abstract class BlocksAttacksBreakChanceMixin {
    @Inject(method = "hurtBlockingItem", at = @At("HEAD"), cancellable = true)
    private void plantz$breakWithoutDurability(
        Level level,
        ItemStack item,
        LivingEntity user,
        InteractionHand hand,
        float damage,
        CallbackInfo ci
    ) {
        if (item.has(PazComponents.WORN_BODY_BLOCKER) && item == user.getItemBySlot(EquipmentSlot.CHEST)) {
            if (!level.isClientSide() && damage > 0.0f) {
                int itemDamage = ((BlocksAttacks) (Object) this).itemDamage().apply(damage);
                if (itemDamage > 0) item.hurtAndBreak(itemDamage, user, EquipmentSlot.CHEST);
            }
            ci.cancel();
            return;
        }
        if (BlockingItemBreakChance.handle(level, item, user, damage, hand.asEquipmentSlot())) {
            ci.cancel();
        }
    }
}
