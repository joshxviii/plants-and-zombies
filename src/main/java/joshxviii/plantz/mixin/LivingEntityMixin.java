package joshxviii.plantz.mixin;

import joshxviii.plantz.PazComponents;
import joshxviii.plantz.PazEffects;
import joshxviii.plantz.PazSounds;
import joshxviii.plantz.PazTags;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Mixin(LivingEntity.class)
abstract public class LivingEntityMixin {

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_HYPNO_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Unique
    public boolean plantz$getHypnoId() {
        return ((Entity) (Object) this).getEntityData().get(DATA_HYPNO_ID);
    }

    @Inject(method = "defineSynchedData", at = @At(value = "TAIL"))
    public void defineData(SynchedEntityData.Builder entityData, CallbackInfo ci) {
        entityData.define(DATA_HYPNO_ID, false);
    }
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveHypnoFlag(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("plantz:IsHypnotized", ((Entity) (Object) this).getEntityData().get(DATA_HYPNO_ID));
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadHypnoFlag(ValueInput input, CallbackInfo ci) {
        ((Entity) (Object) this).getEntityData().set(DATA_HYPNO_ID, input.getBooleanOr("plantz:IsHypnotized", false));
    }
    @Inject(method = "onEffectAdded", at = @At(value = "TAIL"))
    public void onHypnoAdded(MobEffectInstance effect, Entity source, CallbackInfo ci) {
        ((Entity) (Object) this).getEntityData().set(DATA_HYPNO_ID, this.hasEffect(PazEffects.HYPNOTIZE));
    }
    @Inject(method = "onEffectsRemoved", at = @At(value = "TAIL"))
    public void onHypnoRemoved(Collection<MobEffectInstance> effects, CallbackInfo ci) {
        ((Entity) (Object) this).getEntityData().set(DATA_HYPNO_ID, this.hasEffect(PazEffects.HYPNOTIZE));
    }
    @Inject(method = "canBeAffected", at = @At(value = "RETURN"), cancellable = true)
    public void immuneToHypnosis(MobEffectInstance newEffect, CallbackInfoReturnable<Boolean> cir) {
        if (newEffect.is(PazEffects.HYPNOTIZE)) {
            cir.setReturnValue(!((Entity) (Object) this).is(PazTags.EntityTypes.CANNOT_HYPNOTIZE));
        }
    }

    @Inject(method = "actuallyHurt", at = @At(value = "HEAD"), cancellable = true)
    public void paz$blockProjectile(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        if( canArmorAbsorbDamage(source, true) ) {
            ci.cancel();
        }
    }

    @Inject(method = "playHurtSound", at = @At(value = "HEAD"), cancellable = true)
    public void paz$blockProjectileSound(DamageSource source, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (canArmorAbsorbDamage(source, false)) {
            if (entity.getItemBySlot(EquipmentSlot.HEAD).is(Items.BUCKET)) entity.makeSound(PazSounds.PROJECTILE_HIT_BUCKET);
            else entity.makeSound(PazSounds.PROJECTILE_HIT_CONE);
            ci.cancel();
        }
    }

    @Unique
    private boolean canArmorAbsorbDamage(final DamageSource source, boolean tryBreak) {
        LivingEntity entity = (LivingEntity) (Object) this;
        for (EquipmentSlot slot : slots) {
            ItemStack item = entity.getItemBySlot(slot);
            boolean canAbsorb = item.getComponents().has(PazComponents.BLOCKS_PROJECTILE_DAMAGE) && source.is(PazTags.DamageTypes.BLOCKABLE_DAMAGE);
            if(canAbsorb) {
                if(tryBreak) {
                    float breakChance = Objects.requireNonNull(item.getComponents().get(PazComponents.BLOCKS_PROJECTILE_DAMAGE)).getBreakChance();
                    if (entity.getRandom().nextFloat() < breakChance) {
                        item.shrink(1);
                        entity.makeSound(SoundEvents.ITEM_BREAK.value());
                    }
                }
                return canAbsorb;
            }
        }
        return false;
    }

    @Unique
    Set<EquipmentSlot> slots = Set.of(EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
}