package joshxviii.plantz.mixin;

import joshxviii.plantz.item.component.BlockingItemBreakChance;
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
        if (BlockingItemBreakChance.handle(level, item, user, damage)) {
            ci.cancel();
        }
    }
}
