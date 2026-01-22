package joshxviii.plantz.mixin;

import joshxviii.plantz.PazComponents;
import joshxviii.plantz.PazEffects;
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

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Objects;

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