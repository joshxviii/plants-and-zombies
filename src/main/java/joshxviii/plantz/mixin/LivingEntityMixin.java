package joshxviii.plantz.mixin;

import joshxviii.plantz.PazComponents;
import joshxviii.plantz.PazTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "actuallyHurt", at = @At(value = "HEAD"), cancellable = true)
    public void paz$blockProjectile(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if( canArmorAbsorb(source) ) {
            ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
            float breakChance = Objects.requireNonNull(helmet.getComponents().get(PazComponents.BLOCKS_HEAD_DAMAGE)).getBreakChance();
            if (entity.getRandom().nextFloat() < breakChance) {
                helmet.shrink(1);
                entity.makeSound(SoundEvents.ITEM_BREAK.value());
            }
            ci.cancel();
        }
    }

    @Inject(method = "playHurtSound", at = @At(value = "HEAD"), cancellable = true)
    public void paz$blockProjectileSound(DamageSource source, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (canArmorAbsorb(source)) {
            entity.makeSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR);
            ci.cancel();
        }
    }

    @Unique
    private boolean canArmorAbsorb(final DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return entity.getItemBySlot(EquipmentSlot.HEAD).getComponents().has(PazComponents.BLOCKS_HEAD_DAMAGE) && source.is(PazTags.DamageTypes.BLOCKABLE_WITH_HELMET);
    }
}